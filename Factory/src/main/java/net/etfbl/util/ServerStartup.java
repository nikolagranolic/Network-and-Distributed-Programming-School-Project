package net.etfbl.util;

import java.util.logging.Level;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.etfbl.api.UsersAPIService;

public class ServerStartup implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		UsersAPIService.secureSocketThread.start();
		MyLogger.LOGGER.log(Level.INFO, "SecureSocketServer started.");
	}

}
