package com.citi.screen.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citi.screen.model.Transaction;
import com.citi.screen.repository.TransactionsRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("citibridge")
public class ScreenController {

	@Autowired
	TransactionsRepository repository;
	
	private static final String FILE_PATH = "C:\\Users\\sejal\\Documents\\workspace-spring-tool-suite-4-4.14.0.RELEASE\\sanctions-screening\\input\\Sanctions.txt";
	
	@GetMapping("/all-transactions")
	public List<Transaction> getAllTransactions(){
		return repository.findAll();
	}
	
	@GetMapping("/validate-pass")
	public List<Transaction> getValidatePass(){
		return repository.findByValidate("PASS");
	}
	
	@GetMapping("/validate-fail")
	public List<Transaction> getValidateFail(){
		return repository.findByValidate("FAIL");
	}
	
	@GetMapping("/screen")
	public List<Transaction> screenTransactions(){
		System.out.println("Starting sanctions screening...");
		
		FileReader fr;
		try {
			File file = new File(FILE_PATH);
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);  
			String line;  
			repository.updateAllAsScreenPass("PASS", "PASS");
			while((line=br.readLine())!=null)  
			{  
				line = line.trim();
				System.out.println("Sanction keyword: "+line);
				repository.updateScreenFail("FAIL", "PASS", line);
			}  
			fr.close();   
		} catch (IOException e) {
			e.printStackTrace();
		}    
		return repository.findAll();
	}
	
	@GetMapping("/screen-pass")
	public List<Transaction> getScreenPass(){
		return repository.findByScreen("PASS");
	}
	
	@GetMapping("/screen-fail")
	public List<Transaction> getScreenFail(){
		return repository.findByScreen("FAIL");
	}
}
