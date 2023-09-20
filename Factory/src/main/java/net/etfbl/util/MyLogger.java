package net.etfbl.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
	public static Logger LOGGER =  Logger.getLogger(MyLogger.class.getName());
	
	static {
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler("log.txt");
		} catch (SecurityException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
        LOGGER.addHandler(fileHandler);

        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
	}
}
