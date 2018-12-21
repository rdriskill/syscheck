package com.github.rdriskill.syscheck;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author rdriskill
 */
@Component
public class SpringContextListener {
	private static Logger log = LogManager.getLogger(SpringContextListener.class);
	
    @EventListener
    @Order(value = Ordered.LOWEST_PRECEDENCE)
    public void handleContextRefreshed (ContextRefreshedEvent event) {
    	log.info("Spring context refreshed.");
    }

    @EventListener
    @Order(value = Ordered.LOWEST_PRECEDENCE)
    public void handleContextStarted (ContextStartedEvent event) {
    	log.info("Spring context started.");
    }

    @EventListener
    @Order(value = Ordered.LOWEST_PRECEDENCE)
    public void handleContextStopped (ContextStoppedEvent event) {
    	log.info("Spring context stopped.");
    }

    @EventListener
    @Order(value = Ordered.LOWEST_PRECEDENCE)
    public void handleContextClosed (ContextClosedEvent event) {
    	log.info("Spring context closed.");
    }
}
