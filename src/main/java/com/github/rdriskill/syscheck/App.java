package com.github.rdriskill.syscheck;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rdriskill.syscheck.model.CheckPoint;
import com.github.rdriskill.syscheck.service.CheckPointProcessor;

public class App {
	private static Logger log = LogManager.getLogger(App.class);
	
    @SuppressWarnings("resource")
	public static void main( String[] args) throws FileNotFoundException{
    	/* try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
    	 *     // do stuff and close spring context.
    	 * }
    	 */

		ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		BufferedReader reader;

		if (args.length == 0) {
			reader = new BufferedReader(new InputStreamReader(System.in));
		} else {
			reader = new BufferedReader(new FileReader(args[0]));
		}
		
		Collection<CheckPoint> checkPoints = parseFile(reader);
		CheckPointProcessor processor = context.getBean(CheckPointProcessor.class);
		processor.setCheckPoints(checkPoints);
    }
    
    public static Collection<CheckPoint> parseFile(BufferedReader reader) {
    	Set<CheckPoint> checkPoints = new HashSet<CheckPoint>();
    	
    	try {
    		checkPoints = new ObjectMapper().readValue(reader, new TypeReference<HashSet<CheckPoint>>(){});
		} catch (Exception ex) {
			log.error("Error loading JSON checkpoint file.", ex);
		}
    	
    	return checkPoints;
    }
}
