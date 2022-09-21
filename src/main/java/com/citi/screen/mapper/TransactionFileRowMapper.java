package com.citi.screen.mapper;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.citi.screen.model.Transaction;

public class TransactionFileRowMapper implements FieldSetMapper<Transaction> {

	@Override
	public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
		Transaction transaction = new Transaction();
		transaction.setTransactionId(fieldSet.readString("transactionId"));
		
		boolean validDate = false;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		String dateString = fieldSet.readString("impactDate");
		LocalDate currentDate = LocalDate.now();
		LocalDate impactDate = LocalDate.parse(dateString, dateTimeFormatter);
		transaction.setImpactDate(impactDate);
		
		if(currentDate.compareTo(impactDate) == 0) 
			validDate = true;
		else
			System.out.println("Impact date must be current date.");
		
		transaction.setPayerName(fieldSet.readString("payerName"));
		transaction.setPayerAccount(fieldSet.readString("payerAccount"));
		transaction.setPayeeName(fieldSet.readString("payeeName"));
		transaction.setPayeeAccount(fieldSet.readString("payeeAccount"));
		
		String amount = fieldSet.readString("amount");
		boolean validAmount = false;
		transaction.setAmount(Double.valueOf(amount));
		String digits[] = amount.split("\\.");
		if(digits.length==2 && digits[0].length()<=10 && digits[1].length()==2)
			validAmount = true;
		else
			System.out.println("Amount can have max 10 digits and must have 2 decimal places.");
		
		transaction.setScreen(null);
		
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	    Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
	    if(violations.isEmpty() && validDate && validAmount)
	    	transaction.setValidate("PASS");
	    else {
	    	System.out.println(violations);
	    	transaction.setValidate("FAIL");
	    }
	    return transaction;
	}
	
}
