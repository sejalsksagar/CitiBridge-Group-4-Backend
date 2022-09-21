package com.citi.screen.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;



import lombok.Data;

@Entity
@Table(name="transaction")
@Data
public class Transaction implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Size(min=12, max=12, message = "Transaction Id must be of 12 characters")
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String transactionId;
	
	@NotNull
	private LocalDate impactDate;
	
	@NotNull
	@Size(max=35, message = "Name must be max of 35 characters")
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String payerName;
	
	@NotNull
	@Size(min=12, max=12, message = "Account Number must be of 12 characters")
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String payerAccount;
	
	@NotNull
	@Size(max=35, message = "Name must be max of 35 characters")
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String payeeName;
	
	@NotNull
	@Size(min=12, max=12, message = "Account Number must be of 12 characters")
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String payeeAccount;
	
	@NotNull
	@PositiveOrZero(message = "Amount must be positive")
	private Double amount;
	
	private String validate;
	
	private String screen;
}
