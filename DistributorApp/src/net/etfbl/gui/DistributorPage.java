package net.etfbl.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.etfbl.rmi.DistributorInterface;
import net.etfbl.rmi.DistributorObject;
import net.etfbl.util.MyLogger;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.awt.Color;

public class DistributorPage {
	public JFrame frmDistributorapp;
	private JTextField companyNameTextField;
	private JTextArea textArea;
	private JButton btnNewButton;

	public DistributorPage() {
		initialize();
	}

	private void initialize() {
		frmDistributorapp = new JFrame();
		frmDistributorapp.setResizable(false);
		frmDistributorapp.setTitle("DistributorApp");
		frmDistributorapp.setBounds(100, 100, 450, 231);
		frmDistributorapp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDistributorapp.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter company name:");
		lblNewLabel.setBounds(10, 11, 145, 14);
		frmDistributorapp.getContentPane().add(lblNewLabel);
		
		companyNameTextField = new JTextField();
		companyNameTextField.setBounds(165, 8, 263, 20);
		frmDistributorapp.getContentPane().add(companyNameTextField);
		companyNameTextField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Enter products (separated by commas):");
		lblNewLabel_1.setBounds(10, 39, 254, 14);
		frmDistributorapp.getContentPane().add(lblNewLabel_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 64, 418, 89);
		frmDistributorapp.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		
		btnNewButton = new JButton("Publish");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);
				btnNewButton.setBackground(Color.GREEN);
				btnNewButton.setText("Published");
				publishDistributorObject();
			}
		});
		btnNewButton.setBounds(165, 164, 100, 23);
		frmDistributorapp.getContentPane().add(btnNewButton);
	}
	
	private void publishDistributorObject() {
		System.setProperty("java.security.policy", "src/resources/server_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			DistributorObject server = new DistributorObject(parseProducts(textArea.getText()));
			DistributorInterface stub = (DistributorInterface) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.getRegistry(1099);
			registry.rebind(companyNameTextField.getText(), stub);
			
			MyLogger.LOGGER.log(Level.INFO, "Distributor " + companyNameTextField.getText() + " registered.");
		} catch (Exception ex) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", ex);
		}
	}
	
	private ArrayList<String> parseProducts(String productString) {
		return new ArrayList<>(Arrays.asList(productString.split(",")));
	}
}
