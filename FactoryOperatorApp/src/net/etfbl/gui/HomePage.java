package net.etfbl.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import net.etfbl.util.ConnectionFactoryUtil;
import net.etfbl.util.MyLogger;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.awt.event.ActionEvent;

public class HomePage {

	public JFrame frmOperatorapp;
	private String queueName;
	
	public HomePage() {
		File file = new File("src/resources/config.properties");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		queueName = properties.getProperty("MQ_QUEUE_NAME");
		initialize();
	}

	private void initialize() {
		frmOperatorapp = new JFrame();
		frmOperatorapp.setResizable(false);
		frmOperatorapp.setTitle("OperatorApp");
		frmOperatorapp.setBounds(100, 100, 450, 148);
		frmOperatorapp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOperatorapp.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Process an order");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processAnOrder();
			}
		});
		btnNewButton.setBounds(111, 46, 233, 23);
		frmOperatorapp.getContentPane().add(btnNewButton);
	}
	
	private void processAnOrder() {
		try {
			Connection connection = ConnectionFactoryUtil.createConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(queueName, false, false, false, null);
			
			GetResponse response = channel.basicGet(queueName, true);
	        if (response != null) {
	            byte[] body = response.getBody();
	            String message = new String(body, "UTF-8");
	            new OrderProcessPage(message).frame.setVisible(true);
	        } else {
	        	JOptionPane.showMessageDialog(null, "There are no unprocessed orders. Try again later.");
	        }
			
			channel.close();
			connection.close();
		} catch(Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
