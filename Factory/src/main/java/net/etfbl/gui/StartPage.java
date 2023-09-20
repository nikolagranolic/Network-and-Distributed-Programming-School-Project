package net.etfbl.gui;

import javax.swing.JFrame;

import net.etfbl.util.MyLogger;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;
import java.util.logging.Level;
import java.awt.event.ActionEvent;

public class StartPage {
	public JFrame frmFactoryApp;
	private MulticastSocket multicastSocket = null;
	private InetAddress multicastAddress;
	private int multicastPort;
	public StartPage() {
		initialize();
		initializeMulticastSocket();
	}

	private void initialize() {
		frmFactoryApp = new JFrame();
		frmFactoryApp.setResizable(false);
		frmFactoryApp.setTitle("Factory App");
		frmFactoryApp.setBounds(100, 100, 450, 317);
		frmFactoryApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFactoryApp.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Manage client accounts");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UsersPage window = new UsersPage(frmFactoryApp);
				window.frame.setVisible(true);
			}
		});
		btnNewButton.setBounds(123, 30, 184, 47);
		frmFactoryApp.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Promotional message");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PromotionalMessageForm window = new PromotionalMessageForm(multicastSocket, multicastPort, multicastAddress);
				window.frmPromotionalMessage.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(123, 146, 184, 47);
		frmFactoryApp.getContentPane().add(btnNewButton_1);
		
		JButton btnProductsManager = new JButton("Manage products");
		btnProductsManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProductsPage window = new ProductsPage();
				window.frmProducts.setVisible(true);
			}
		});
		btnProductsManager.setBounds(123, 88, 184, 47);
		frmFactoryApp.getContentPane().add(btnProductsManager);
		
		JButton btnNewButton_1_1 = new JButton("Buy from distributors");
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BuyingPage().frmPurchaseFromDistributor.setVisible(true);
			}
		});
		btnNewButton_1_1.setBounds(123, 207, 184, 47);
		frmFactoryApp.getContentPane().add(btnNewButton_1_1);
	}
	
	private void initializeMulticastSocket() {
		try {
			Properties properties = new Properties();
			File file = new File("src/main/java/resources/config.properties");
			properties.load(new FileInputStream(file));
			
			multicastAddress = InetAddress.getByName(properties.getProperty("MULTICAST_HOST"));
			multicastPort = Integer.parseInt(properties.getProperty("MULTICAST_PORT"));
			multicastSocket = new MulticastSocket();
			multicastSocket.joinGroup(multicastAddress);
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
