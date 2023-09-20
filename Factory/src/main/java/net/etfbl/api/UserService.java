package net.etfbl.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.json.JSONObject;
import net.etfbl.model.User;
import net.etfbl.model.UsersDataSource;
import net.etfbl.util.MyLogger;

public class UserService {
	private UsersDataSource data;

	public UserService(UsersDataSource uds) {
		data = uds;
	}

	public ArrayList<User> getAll() {
		data = new UsersDataSource();
		ArrayList<User> usersWithoutPasswords = new ArrayList<User>();
		for (User u : data.users) {
			usersWithoutPasswords.add(new User(u.getUsername(), null, u.getName(), u.getAddress(), u.getPhone(),
					u.isApproved(), u.isBlocked()));
		}
		return usersWithoutPasswords;
	}

	public boolean add(User user) {
		int index = data.users.indexOf(user);
		if (index == -1) {
			boolean success = data.users.add(user);
			JSONObject usersObj = new JSONObject();
			usersObj.put("users", data.users);
			String serializedString = usersObj.toString();

			Properties properties = new Properties();
			File file = new File("../../ProjektniZadatak/Factory/src/main/java/resources/config.properties");

			try {
				properties.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
			} catch (IOException e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
			}
			String usersFilePath = properties.getProperty("USERS_FILE");
			try (PrintWriter printWriter = new PrintWriter(new FileWriter(usersFilePath))) {
				printWriter.print(serializedString);
			} catch (IOException e) {
				MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
			}
			return success;
		} else
			return false;
	}

	public boolean approve(User user) {
		int index = data.users.indexOf(user);
		User selectedUser = data.users.get(index);
		if (index >= 0) {
			selectedUser.setApproved(true);
			data.users.set(index, selectedUser);
			return true;
		} else {
			return false;
		}
	}

	public boolean unapprove(User user) {
		int index = data.users.indexOf(user);
		User selectedUser = data.users.get(index);
		if (index >= 0) {
			selectedUser.setApproved(false);
			data.users.set(index, selectedUser);
			return true;
		} else {
			return false;
		}
	}

	public boolean block(User user) {
		int index = data.users.indexOf(user);
		User selectedUser = data.users.get(index);
		if (index >= 0) {
			selectedUser.setBlocked(true);
			data.users.set(index, selectedUser);
			return true;
		} else {
			return false;
		}
	}

	public boolean unblock(User user) {
		int index = data.users.indexOf(user);
		User selectedUser = data.users.get(index);
		if (index >= 0) {
			selectedUser.setBlocked(false);
			data.users.set(index, selectedUser);
			return true;
		} else {
			return false;
		}
	}

	public boolean login(User user) {
		data = new UsersDataSource();
		int index = data.users.indexOf(user);
		if (index != -1) {
			User tmp = data.users.get(index);

			if (tmp.getPassword().equals(user.getPassword()) && tmp.isApproved() && !tmp.isBlocked())
				return true;
			else
				return false;
		} else
			return false;
	}

	public boolean remove(User user) {
		int index = data.users.indexOf(user);
		if (index >= 0) {
			data.users.remove(index);
			return true;
		} else {
			return false;
		}
	}
}
