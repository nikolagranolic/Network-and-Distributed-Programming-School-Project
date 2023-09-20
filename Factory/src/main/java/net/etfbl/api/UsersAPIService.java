package net.etfbl.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.etfbl.model.User;
import net.etfbl.model.UsersDataSource;
import net.etfbl.util.MyLogger;
import net.etfbl.util.ServerThread;

@Path("/users")
public class UsersAPIService {
	public static Thread secureSocketThread;
	public static Registry registry;
	static {
		secureSocketThread = new Thread(() -> {
			try {
				Properties properties = loadProperties();

				System.setProperty("javax.net.ssl.keyStore", properties.getProperty("KEY_STORE_PATH"));
				System.setProperty("javax.net.ssl.keyStorePassword", properties.getProperty("KEY_STORE_PASSWORD"));
				
				SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				ServerSocket ss = ssf.createServerSocket(Integer.parseInt(properties.getProperty("SECURE_SOCKET_PORT")));
				while (true) {
					SSLSocket s = (SSLSocket) ss.accept();
					new ServerThread(s).start();
				}
			} catch (Exception e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
			}
		});
		
		Properties properties;
		properties = loadProperties();
		try {
			registry = LocateRegistry.createRegistry(Integer.parseInt(properties.getProperty("RMI_REGISTRY_PORT")));
		} catch (RemoteException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
	
	UserService userService;
	
	public UsersAPIService() {
		userService = new UserService(new UsersDataSource());
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getAll() {
		return userService.getAll();
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		if (userService.add(user)) {
			MyLogger.LOGGER.log(Level.INFO, "User " + user.getName() + " registered successfully.");
			return Response.status(200).entity(user).build();
		}
		else {
			return Response.status(400).entity("Invalid argument (invalid request payload)").build();
		}
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		if (userService.login(user)) {
			MyLogger.LOGGER.log(Level.INFO, "User " + user.getUsername() + " logged in successfully.");
			return Response.status(200).entity(true).build();
		} else {
			return Response.status(401).entity("Unauthorized").build();
		}
	}
	
	
	private static Properties loadProperties() {
		File file = new File("../../ProjektniZadatak/Factory/src/main/java/resources/config.properties");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		return properties;
	}
}
