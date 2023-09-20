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

public class LoginForm {
    private JTextField usernameField;
    private JTextField passwordField;
    public JFrame frame;

    public LoginForm() {
        frame = new JFrame();
        frame.setResizable(false);

        frame.setTitle("Login Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
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

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 100, 100, 30);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        container.add(loginButton);

        frame.setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        JSONObject registrationObj = new JSONObject();

		registrationObj.put("username", username);
		registrationObj.put("password", password);
		
		try {
			File file = new File("src/resources/config.properties");
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			String baseUrlString = properties.getProperty("BASE_URL");
			URL url = new URL(baseUrlString + "/users/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStream os = conn.getOutputStream();
			os.write(registrationObj.toString().getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(null, "Login unsuccessful! (incorrect credentials/account not approved/account blocked) :(");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			os.close();
			conn.disconnect();
			
			JOptionPane.showMessageDialog(null, "Login successful! :)");
			
			new ClientHomePage().frmClientapp.setVisible(true);
	        frame.dispose();
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}

    }
}

