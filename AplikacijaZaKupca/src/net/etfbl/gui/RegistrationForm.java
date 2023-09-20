package net.etfbl.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.json.JSONObject;

import net.etfbl.util.MyLogger;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

public class RegistrationForm {
	private JTextField usernameField;
	private JTextField passwordField;
	private JTextField nameField;
	private JTextField addressField;
	private JTextField phoneField;
	public JFrame frame;
	private JFrame startPage;
	private JButton goBackButton;
	private JTextField confirmPasswordTextField;
	private JLabel lblConfirmPassword;

	public RegistrationForm(JFrame startPage) {
		this.startPage = startPage;
		frame = new JFrame();
		frame.setResizable(false);

		frame.setTitle("Registration Form");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(401, 377);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		Container container = frame.getContentPane();

		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setBounds(20, 20, 100, 30);
		container.add(usernameLabel);

		usernameField = new JTextField();
		usernameField.setBounds(130, 20, 200, 30);
		container.add(usernameField);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(20, 60, 100, 30);
		container.add(passwordLabel);

		passwordField = new JTextField();
		passwordField.setBounds(130, 60, 200, 30);
		container.add(passwordField);

		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(20, 140, 100, 30);
		container.add(nameLabel);

		nameField = new JTextField();
		nameField.setBounds(130, 140, 200, 30);
		container.add(nameField);

		JLabel addressLabel = new JLabel("Address:");
		addressLabel.setBounds(20, 180, 100, 30);
		container.add(addressLabel);

		addressField = new JTextField();
		addressField.setBounds(130, 180, 200, 30);
		container.add(addressField);

		JLabel phoneLabel = new JLabel("Phone:");
		phoneLabel.setBounds(20, 220, 100, 30);
		container.add(phoneLabel);

		phoneField = new JTextField();
		phoneField.setBounds(130, 220, 200, 30);
		container.add(phoneField);

		JButton registerButton = new JButton("Register");
		registerButton.setBounds(97, 261, 100, 30);
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				register();
			}
		});
		container.add(registerButton);
		
		goBackButton = new JButton("Go back");
		goBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				startPage.setVisible(true);
			}
		});
		goBackButton.setBounds(207, 261, 100, 30);
		frame.getContentPane().add(goBackButton);
		
		confirmPasswordTextField = new JTextField();
		confirmPasswordTextField.setBounds(130, 101, 200, 30);
		frame.getContentPane().add(confirmPasswordTextField);
		
		lblConfirmPassword = new JLabel("Confirm password:");
		lblConfirmPassword.setBounds(20, 101, 100, 30);
		frame.getContentPane().add(lblConfirmPassword);

		frame.setVisible(true);
	}

	private void register() {
		String username = usernameField.getText();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordTextField.getText();
		String name = nameField.getText();
		String address = addressField.getText();
		String phone = phoneField.getText();

		if (!password.equals(confirmPassword)) {
			JOptionPane.showMessageDialog(null, "Passwords in Password and Confirm password textfields must match!");
			return;
		}
		
		JSONObject registrationObj = new JSONObject();

		registrationObj.put("username", username);
		registrationObj.put("password", password);
		registrationObj.put("name", name);
		registrationObj.put("address", address);
		registrationObj.put("phone", phone);
		registrationObj.put("approved", false);
		registrationObj.put("blocked", false);

		try {
			File file = new File("src/resources/config.properties");
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			String baseUrlString = properties.getProperty("BASE_URL");
			URL url = new URL(baseUrlString + "/users/register");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStream os = conn.getOutputStream();
			os.write(registrationObj.toString().getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(null, "Registration unsuccessful! Chosen username might be taken. :(");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			os.close();
			conn.disconnect();
			
			JOptionPane.showMessageDialog(null, "Registration successful! :)");
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		
		frame.dispose();
		startPage.setVisible(true);
	}
}
