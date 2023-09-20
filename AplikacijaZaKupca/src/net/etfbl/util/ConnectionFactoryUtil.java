package net.etfbl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionFactoryUtil {

	public static Connection createConnection() throws IOException, TimeoutException {
		File file = new File("src/resources/config.properties");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(properties.getProperty("MQ_HOST"));
		factory.setUsername(properties.getProperty("MQ_USERNAME"));
		factory.setPassword(properties.getProperty("MQ_PASSWORD"));
		return factory.newConnection();
	}
}
