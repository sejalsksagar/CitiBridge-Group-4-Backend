package com.citi.screen;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SanctionsScreeningApplication {

	@Autowired
    JobLauncher jobLauncher;
      
    @Autowired
    Job job;
      
    public static void main(String[] args)
    {
        SpringApplication.run(SanctionsScreeningApplication.class, args);
    }

}
