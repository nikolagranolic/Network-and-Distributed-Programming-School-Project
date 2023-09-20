package net.etfbl.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

import net.etfbl.util.MyLogger;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OrderProcessPage {
	public JFrame frame;
	private String orderString;
	private JTextArea textArea;
	private String emailAddress;
	private String senderUsername;
	private String senderPassword;

	public OrderProcessPage(String orderString) {
		this.orderString = orderString;
		initialize();
	}

	private void initialize() {
		boolean orderValid = validate();
		frame = new JFrame();

		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JLabel lblNewLabel = new JLabel("Order:");
		lblNewLabel.setBounds(10, 11, 81, 14);
		frame.getContentPane().add(lblNewLabel);

		JButton acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processOrder(true);
			}
		});
		acceptButton.setBounds(90, 231, 89, 23);
		frame.getContentPane().add(acceptButton);

		JButton rejectButton = new JButton("Reject");
		rejectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processOrder(false);
			}
		});
		rejectButton.setBounds(244, 231, 89, 23);
		frame.getContentPane().add(rejectButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 418, 184);
		frame.getContentPane().add(scrollPane);
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		textArea.setText(formatXML(orderString));
		textArea.setEditable(false);

		if (orderValid) {
			textArea.setText("Order not valid!");
			return;
		}
		
		emailAddress = getEmailAddress(orderString);
	}

	private boolean validate() {
		Properties properties = loadProperties();

		String schemaFilePath = properties.getProperty("ORDER_SCHEMA_PATH");
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(new StreamSource(schemaFilePath));
		} catch (SAXException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}

		Validator validator = schema.newValidator();

		Source xmlSource = new StreamSource(new StringReader(orderString));

		try {
			validator.validate(xmlSource);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String formatXML(String input) {
		StringBuilder output = new StringBuilder();
		int indentLevel = 0;
		boolean insideTag = false;

		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);

			if (currentChar == '<') {
				if (input.charAt(i + 1) != '/' || input.charAt(i + 1) == '/') {
					indentLevel--;
					output.append("\n");
					for (int j = 0; j < indentLevel; j++) {
						output.append("\t");
					}
				} else if (insideTag) {
					output.append("\n");
					indentLevel++;
					for (int j = 0; j < indentLevel; j++) {
						output.append("\t");
					}
				}

				insideTag = true;
			}

			output.append(currentChar);

			if (currentChar == '>') {
				insideTag = false;
				output.append("\n");
				output.append("\t");
			}

			if (!insideTag && currentChar == '/') {
				output.append("\n");
				indentLevel--;
				for (int j = 0; j < indentLevel; j++) {
					output.append("\t");
				}
			}
		}

		return output.toString();
	}

	private String getEmailAddress(String input) {
		int startIndex = input.indexOf("<emailAddress>") + "<emailAddress>".length();
		int endIndex = input.indexOf("</emailAddress>");
		if (startIndex >= 0 && endIndex >= 0 && startIndex < endIndex) {
			return input.substring(startIndex, endIndex).trim();
		} else {
			return null;
		}
	}
	
	private void processOrder(boolean isAccepted) {
		try {
			Properties properties = loadProperties();

			System.setProperty("javax.net.ssl.trustStore", properties.getProperty("KEY_STORE_PATH"));
			System.setProperty("javax.net.ssl.trustStorePassword", properties.getProperty("KEY_STORE_PASSWORD"));

			SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket s = (SSLSocket) sf.createSocket(properties.getProperty("HOST"),
					Integer.parseInt(properties.getProperty("PORT")));
			
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
			
			out.println("SAVE_ORDER");
			
			if (!"READY".equals(in.readLine()))
				return;
			
			String orderSummary = createOrderSummary(isAccepted);
			
			out.println(orderSummary);
			
			in.close();
			out.close();
			s.close();
			
//			sendMail(emailAddress, "Order status", orderSummary);
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		
		JOptionPane.showMessageDialog(null, "Order " + (isAccepted ? "accepted!" : "rejected!"));
		frame.dispose();
	}
	
	private String createOrderSummary(boolean isAccepted) {
		String result = "";
		result += isAccepted ? "ORDER ACCEPTED\n" : "ORDER REJECTED\n";
		result += "Email address of client:";
		result += emailAddress;
		result += "\n";
		result += formatXML(orderString);
		return result;
	}
	
	private Properties loadMailConfig() throws FileNotFoundException, IOException {
		Properties mailProp = new Properties();
		mailProp.load(new FileInputStream(new File("src/resources/gmail.properties")));

		senderUsername = mailProp.getProperty("username");
		senderPassword = mailProp.getProperty("password");
		
		return mailProp;
	}
	
	private void sendMail(String to, String title, String body) {
	    try {
	    	Properties properties = loadMailConfig();
			
	        //prva verzija
			Session session=Session.getDefaultInstance(properties, new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(senderUsername, senderPassword);
	            }
	        });
	        Message message=new MimeMessage(session);
	        message.setFrom(new InternetAddress(senderUsername, "Factory Operator"));
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
	        message.setSubject(title);
	        message.setText(body);
	        Transport.send(message);
	    } catch (Exception e) {
	    	MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
	    }
	}
	
	private Properties loadProperties() {
		File file = new File("src/resources/config.properties");
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
