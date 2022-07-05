package com.citi.screen.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.citi.screen.model.Transaction;

public interface TransactionsRepository extends JpaRepository<Transaction, String> {

	List<Transaction> findByValidate(String status);
	
	List<Transaction> findByScreen(String status);
	
	@Query("select t from Transaction t where t.validate = ?1 and (t.payerName = ?2 or t.payeeName = ?2)")
	List<Transaction> findScreenFails(String validate, String keyword);
	
	@Transactional
	@Modifying
	@Query("update Transaction t set t.screen = ?1 where t.validate = ?2 and (t.payerName = ?3 or t.payeeName = ?3)")
	void updateScreenFail(String status, String validate, String keyword);
	
	@Transactional
	@Modifying
	@Query("update Transaction t set t.screen = ?1 where t.validate = ?2")
	void updateAllAsScreenPass(String status, String validate);
	
	@Transactional
	@Modifying
	@Query("update Transaction t set t.screen = ?1 where t.transactionId = ?2")
	void setScreenById(String status, String transactionId);
}
