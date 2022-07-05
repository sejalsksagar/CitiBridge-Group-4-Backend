package com.citi.screen.config;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;

public class FileVerificationSkipper implements SkipPolicy {

	@Override
	public boolean shouldSkip(Throwable exception, int skipCount) throws SkipLimitExceededException {
		
		if (exception instanceof FlatFileParseException && skipCount <= 5) {
            FlatFileParseException ffpe = (FlatFileParseException) exception;
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Parsing error - skipping record with incorrect field lengths" + ffpe.getLineNumber());
            errorMessage.append(ffpe.getInput() + "\n");
            System.out.println(errorMessage.toString());
            return true;
        } else {
            return false;
        }
	}

}
