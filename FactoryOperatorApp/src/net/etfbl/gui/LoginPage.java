package net.etfbl.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.etfbl.util.MyLogger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.Properties;
import java.util.logging.Level;
import java.awt.event.ActionEvent;

public class LoginPage {

	public JFrame frmLogin;
	private JTextField textField;

	public LoginPage() {
		initialize();
	}

	private void initialize() {
		frmLogin = new JFrame();
		frmLogin.setTitle("Login");
		frmLogin.setResizable(false);
		frmLogin.setBounds(100, 100, 391, 152);
		frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogin.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Enter username:");
		lblNewLabel.setBounds(79, 39, 94, 14);
		frmLogin.getContentPane().add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(193, 36, 109, 20);
		frmLogin.getContentPane().add(textField);
		textField.setColumns(10);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		loginButton.setBounds(143, 64, 89, 23);
		frmLogin.getContentPane().add(loginButton);
	}

	private void login() {
		try {
			File file = new File("src/resources/config.properties");
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));

			String trustStore = properties.getProperty("KEY_STORE_PATH");
			String password = properties.getProperty("KEY_STORE_PASSWORD");
			
			TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(new FileInputStream(trustStore), password.toCharArray());
			factory.init(ks);
			SSLContext sslcontex = SSLContext.getInstance("TLS");
			sslcontex.init(null, factory.getTrustManagers(), null);
			
			SSLSocketFactory sslSocketF = sslcontex.getSocketFactory();
			SSLSocket s = (SSLSocket) sslSocketF.createSocket(properties.getProperty("HOST"),
							Integer.parseInt(properties.getProperty("PORT")));
			
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

			out.println("LOGIN");
			
			if (!"READY".equals(in.readLine()))
				return;
			
			out.println(textField.getText());
			
			String response = in.readLine();
			
			in.close();
			out.close();
			s.close();
			
			if (response.equals("OK")) {
				JOptionPane.showMessageDialog(null, "Login successful! :)");
				new HomePage().frmOperatorapp.setVisible(true);
				frmLogin.dispose();
			} else if (response.equals("NOT OK")) {
				JOptionPane.showMessageDialog(null, "Login unsuccessful! Username does not exist! :(");
			}
			else throw new Exception();
			
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}		
	}
}
