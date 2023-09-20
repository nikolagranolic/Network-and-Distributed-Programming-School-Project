package net.etfbl.gui;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.etfbl.api.UserService;
import net.etfbl.model.User;
import net.etfbl.model.UsersDataSource;
import net.etfbl.util.MyLogger;
import net.etfbl.util.UserDataTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.json.JSONObject;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.event.ActionEvent;

public class UsersPage {
	public JFrame frame;
	private JFrame parentFrame;
	private JScrollPane jScrollPane1;
	private JTable tblActivatedUsers;
	private JTable tblUnactivatedUsers;
	private ArrayList<User> users;
	private JLabel lblNewLabel_2;
	private JTextField textField;
	private UserService userService;

	public UsersPage(JFrame parentFrame) {
		UsersDataSource uds = new UsersDataSource();
		this.parentFrame = parentFrame;
		this.userService = new UserService(uds);
		this.users = uds.users;
		initialize();
	}
	
	public void refresh() {
		UserDataTableModel activatedTableModel = new UserDataTableModel(
				users.stream().filter(user -> user.isApproved()).collect(Collectors.toCollection(ArrayList::new)));
		tblActivatedUsers.setModel(activatedTableModel);
		
		UserDataTableModel unactivatedTableModel = new UserDataTableModel(
				users.stream().filter(user -> !user.isApproved()).collect(Collectors.toCollection(ArrayList::new)));
		tblUnactivatedUsers.setModel(unactivatedTableModel);	
	}

	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 697, 461);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(24, 45, 427, 151);
		tblActivatedUsers = new JTable();
		tblUnactivatedUsers = new JTable();
		UserDataTableModel activatedTableModel = new UserDataTableModel(
				users.stream().filter(user -> user.isApproved()).collect(Collectors.toCollection(ArrayList::new)));
		frame.getContentPane().setLayout(null);
		tblActivatedUsers.setModel(activatedTableModel);

		jScrollPane1.setViewportView(tblActivatedUsers);
		frame.getContentPane().add(jScrollPane1);

		JLabel lblNewLabel = new JLabel("Approved accounts");
		lblNewLabel.setBounds(24, 20, 122, 14);
		frame.getContentPane().add(lblNewLabel);

		JScrollPane jScrollPane2 = new JScrollPane();
		jScrollPane2.setBounds(24, 246, 427, 151);
		frame.getContentPane().add(jScrollPane2);

		JLabel lblNewLabel_1 = new JLabel("Unapproved accounts");
		lblNewLabel_1.setBounds(24, 221, 177, 14);
		frame.getContentPane().add(lblNewLabel_1);

		UserDataTableModel unactivatedTableModel = new UserDataTableModel(
				users.stream().filter(user -> !user.isApproved()).collect(Collectors.toCollection(ArrayList::new)));
		tblUnactivatedUsers.setModel(unactivatedTableModel);

		jScrollPane2.setViewportView(tblUnactivatedUsers);
		frame.getContentPane().add(jScrollPane1);
		
		lblNewLabel_2 = new JLabel("Enter username:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_2.setBounds(531, 142, 154, 20);
		frame.getContentPane().add(lblNewLabel_2);
		
		textField = new JTextField();
		textField.setBounds(521, 163, 96, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton deleteButton = new JButton("Delete account");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteUser();
			}
		});
		deleteButton.setBounds(477, 212, 187, 23);
		frame.getContentPane().add(deleteButton);
		
		JLabel lblNewLabel_2_1 = new JLabel("Choose option:");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_2_1.setBounds(531, 187, 154, 20);
		frame.getContentPane().add(lblNewLabel_2_1);
		
		JButton btnChangeApprovationStatus = new JButton("Approve/Unapprove");
		btnChangeApprovationStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeApprovationStatus();
			}
		});
		btnChangeApprovationStatus.setBounds(477, 249, 187, 23);
		frame.getContentPane().add(btnChangeApprovationStatus);
		
		JButton btnChangeApprovationStatus_1 = new JButton("Block/Unblock");
		btnChangeApprovationStatus_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeBlockStatus();
			}
		});
		btnChangeApprovationStatus_1.setBounds(477, 283, 187, 23);
		frame.getContentPane().add(btnChangeApprovationStatus_1);
	}
	
	private void deleteUser() {
		String usernameInput = textField.getText();
		
		userService.remove(new User(usernameInput, ""));
		updateUsersJsonFile();

		refresh();
		JOptionPane.showMessageDialog(null, "Acount deleted successfully! :)");
	}
	
	private void changeApprovationStatus() {
		String usernameInput = textField.getText();
		int index = users.indexOf(new User(usernameInput, ""));
		User selectedUser = users.get(index);
		
		if (selectedUser.isApproved())
			userService.unapprove(selectedUser);
		else
			userService.approve(selectedUser);
		
		updateUsersJsonFile();
		
		refresh();
		JOptionPane.showMessageDialog(null, "Approvation status changed successfully! :)");
	}
	
	private void changeBlockStatus() {
		String usernameInput = textField.getText();
		int index = users.indexOf(new User(usernameInput, ""));
		User selectedUser = users.get(index);
		
		if (selectedUser.isBlocked())
			userService.unblock(selectedUser);
		else
			userService.block(selectedUser);
		
		updateUsersJsonFile();
		
		refresh();
		JOptionPane.showMessageDialog(null, "Block status changed successfully! :)");
	}
	
	private void updateUsersJsonFile() {
		JSONObject usersObj = new JSONObject();
		usersObj.put("users", users);
		String serializedString = usersObj.toString();

		Properties properties = new Properties();
		File file = new File("src/main/java/resources/config.properties");

		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		String usersFilePath = properties.getProperty("USERS_FILE_GUI_APP");
		try (PrintWriter printWriter = new PrintWriter(new FileWriter(usersFilePath))) {
			printWriter.print(serializedString);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
