package net.etfbl.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;
import net.etfbl.util.JSONUtil;
import net.etfbl.util.MyLogger;

public class UsersDataSource {
	public ArrayList<User> users = new ArrayList<>();
	
	public UsersDataSource() {
		Properties properties = new Properties();
        try {        	
    		File file = new File("../../ProjektniZadatak/Factory/src/main/java/resources/config.properties");
    		
			properties.load(new FileInputStream(file));
			String usersFilePath = properties.getProperty("USERS_FILE");
			Reader reader = new FileReader(usersFilePath);
			
			String jsonText = JSONUtil.readAll(reader);
			JSONObject usersObj = new JSONObject(jsonText);
			JSONArray usersArray = (JSONArray)usersObj.get("users");
			
			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject obj = usersArray.getJSONObject(i);
				users.add(new User((String)obj.get("username"), (String)obj.get("password"), (String)obj.get("name"), (String)obj.get("address"), (String)obj.get("phone"), (boolean)obj.get("approved"), (boolean)obj.get("blocked")));
			}
			
		} catch (FileNotFoundException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
