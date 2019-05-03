package cn.com.eship;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJms
public class TjspiderApplication {
	protected static Logger logger=LoggerFactory.getLogger(TjspiderApplication.class);
	@Bean
	public ActiveMQQueue queue() {
		return new ActiveMQQueue("test");
	}

	public static void main(String[] args) {
		SpringApplication.run(TjspiderApplication.class, args);
		logger.info("started success");
	}
}
