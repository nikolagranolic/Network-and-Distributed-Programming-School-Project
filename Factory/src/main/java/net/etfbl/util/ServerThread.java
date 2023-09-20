package net.etfbl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerThread extends Thread {
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;

	public ServerThread(Socket socket) {
		super();
		this.socket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}

	@Override
	public void run() {
		ArrayList<String> factoryUsers = new ArrayList<String>();
		String option = "";
		try {
			option = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			option = "";
		}
		if ("LOGIN".equals(option)) {
			out.println("READY");
			try {
				Properties properties = loadProperties();
				String usersFilePath = properties.getProperty("FACTORY_USERS_FILE");
				Reader reader = new FileReader(usersFilePath);

				String jsonText = JSONUtil.readAll(reader);
				JSONObject usersObj = new JSONObject(jsonText);
				JSONArray usersArray = (JSONArray) usersObj.get("users");

				for (int i = 0; i < usersArray.length(); i++) {
					JSONObject obj = usersArray.getJSONObject(i);
					factoryUsers.add((String) obj.get("username"));
				}
			} catch (Exception e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);;
			}
			String username = "";
			try {
				username = in.readLine();
			} catch (IOException e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
				username = "";
			}

			out.println(factoryUsers.contains(username) ? "OK" : "NOT OK");
			
			MyLogger.LOGGER.log(Level.INFO, "Operator " + username + " logged in successfully.");
		} else if ("SAVE_ORDER".equals(option)) {
			try {
				out.println("READY");

				String fileContent = "";
				String line = "";
				while ((line = in.readLine()) != null) {
					fileContent += (line + "\n");
				}

				Properties properties = loadProperties();
				
				String usersFilePath = properties.getProperty("ORDER_FOLDER");

				LocalDateTime timestamp = LocalDateTime.now();

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
				String filename = timestamp.format(formatter) + ".txt";

				Path filePath = Path.of(usersFilePath + filename);
				Files.writeString(filePath, fileContent, StandardOpenOption.CREATE);

				MyLogger.LOGGER.log(Level.INFO, "File " + filePath + " created successfully.");
			} catch (Exception e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
			}
		}

		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
	
	private Properties loadProperties() {
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
