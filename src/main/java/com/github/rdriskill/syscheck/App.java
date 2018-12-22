package com.github.rdriskill.syscheck;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.rdriskill.syscheck.service.CheckPointProcessor;

public class App {
	private static Logger log = LogManager.getLogger(App.class);
	
	public static void main( String[] args) throws FileNotFoundException{
		ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		String checkpointFilePath = null;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			context.stop();
			context.close();
		}));

		if (args.length != 0) {
			checkpointFilePath = args[0].trim();
		}
		
		if (checkpointFilePath == null || checkpointFilePath.isEmpty()) {
			log.error("No checkpoint file path was specified.");
			System.exit(0);
		} else {
			log.info("Using checkpoint file {}", checkpointFilePath);
			CheckPointProcessor processor = context.getBean(CheckPointProcessor.class);
			processor.setCheckpointFilePath(checkpointFilePath);
		}
    }
}
