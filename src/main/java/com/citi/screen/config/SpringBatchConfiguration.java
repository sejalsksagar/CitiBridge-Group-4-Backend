package com.citi.screen.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.citi.screen.mapper.TransactionFileRowMapper;
import com.citi.screen.model.Transaction;

@Configuration
public class SpringBatchConfiguration {

	  @Autowired
	  private JobBuilderFactory jobBuilderFactory;
	   
	  @Autowired
	  private StepBuilderFactory stepBuilderFactory;
	  
	  @Autowired
	  private DataSource dataSource;
	  
	  public static String fileName = null;
	  
	  private static final String DIR_PATH = "..\\sanctions-screening\\input";
	  private static final String OUT_PATH = "..\\sanctions-screening\\archive";
	 
	  @Bean
	  public Job readValidFilesJob() {
		
		File directory = new File(DIR_PATH);
		
		for(String name: directory.list())
			if(name.endsWith("_TTS.txt"))
				fileName = name;
				
		if(fileName == null) {
			System.out.println("No file with valid file naming convention found.");
			System.exit(0);
		}
		
		return jobBuilderFactory
	        .get("readValidFilesJob")
	        .incrementer(new RunIdIncrementer())
	        .listener(listener())
	        .start(step1())
	        .build();
	  }
	  
	@Bean
	public JobListener listener() {
		return new JobListener();
	}
	
	@Bean
	public SkipPolicy fileVerificationSkipper() {
	    return new FileVerificationSkipper();
	}

	private Step step1()  {
	        return stepBuilderFactory.get("step1")
	            .<Transaction,Transaction>chunk(1)
	            .reader(reader()).faultTolerant().skipPolicy(fileVerificationSkipper())
	            .writer(writer())
	            .build();
	    }
	  
	  @Bean
	  public FlatFileItemReader<Transaction> reader(){
		  System.out.println("Reading file..."+fileName);
		  FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
		  reader.setResource(new FileSystemResource(DIR_PATH+"\\"+fileName));
		  
			  reader.setLineMapper(new DefaultLineMapper<Transaction>() {{
				   setLineTokenizer(new FixedLengthTokenizer() {{
						   setNames("transactionId", "impactDate", "payerName", "payerAccount", "payeeName", "payeeAccount", "amount");
						   setColumns(new Range[] {
								   new Range(1,12), new Range(13,20), new Range(21,55), new Range(56,67), new Range(68,102), new Range(103,114), new Range(115,127)
						   });
					}});
				   setFieldSetMapper(new TransactionFileRowMapper());
			   }});
		  return reader;
	   }
	  
	  @Bean
	  public JdbcBatchItemWriter<Transaction> writer() {
		  System.out.println("Writing records into database...");
		  JdbcBatchItemWriter<Transaction> writer = new JdbcBatchItemWriter<Transaction>();
		  
	    	  writer.setDataSource(dataSource);
		      writer.setAssertUpdates(true);
	      return new JdbcBatchItemWriterBuilder<Transaction>()
    		      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
    		      .sql("INSERT INTO transaction (transaction_id, impact_date, payer_name, payer_account, payee_name, payee_account, amount, validate, screen) VALUES (:transactionId, :impactDate, :payerName, :payerAccount, :payeeName, :payeeAccount, :amount, :validate, :screen) ON DUPLICATE KEY UPDATE transaction_id = :transactionId")
    		      .dataSource(dataSource)
    		      .build();
	  }
	
	  public static void moveToArchive()
	  {
		  System.out.println("Moving file to archive folder...");
		  Path temp = null;
		try {
			temp = Files.move(Paths.get(DIR_PATH+"\\"+fileName), Paths.get(OUT_PATH+"\\"+fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		  if(temp != null)
			  System.out.println("File moved to archive");
		  else
			  System.out.println("Failed to move the file");
	  }
	  
}
