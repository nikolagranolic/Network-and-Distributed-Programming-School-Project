package net.etfbl.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JTextField;
import net.etfbl.rmi.DistributorInterface;
import net.etfbl.util.MyLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class BuyingPage {
	private Registry registry;
	public JFrame frmPurchaseFromDistributor;
	private JTextField quantityTextField;
	private String companyName;
	private JComboBox<String> distributorsComboBox;
	private JTextArea textArea;
	private JTextField productTextField;
	private DistributorInterface distributor;

	public BuyingPage() {
		try {
			registry = LocateRegistry.getRegistry(1099);
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		initialize();
	}

	private void initialize() {
		frmPurchaseFromDistributor = new JFrame();
		frmPurchaseFromDistributor.setTitle("Purchase from distributor");
		frmPurchaseFromDistributor.setResizable(false);
		frmPurchaseFromDistributor.setBounds(100, 100, 450, 194);
		frmPurchaseFromDistributor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmPurchaseFromDistributor.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Choose distributor:");
		lblNewLabel.setBounds(21, 15, 138, 14);
		frmPurchaseFromDistributor.getContentPane().add(lblNewLabel);
		
		distributorsComboBox = null;
		try {
			distributorsComboBox = new JComboBox<String>(registry.list());
		} catch (AccessException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (RemoteException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		distributorsComboBox.setBounds(156, 11, 107, 22);
		frmPurchaseFromDistributor.getContentPane().add(distributorsComboBox);
		
		JButton getProductsButton = new JButton("Get products");
		getProductsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshProducts();
			}
		});
		getProductsButton.setBounds(309, 11, 119, 23);
		frmPurchaseFromDistributor.getContentPane().add(getProductsButton);
		
		JLabel lblNewLabel_1 = new JLabel("Quantity:");
		lblNewLabel_1.setBounds(290, 98, 62, 14);
		frmPurchaseFromDistributor.getContentPane().add(lblNewLabel_1);
		
		quantityTextField = new JTextField();
		quantityTextField.setBounds(348, 95, 80, 20);
		frmPurchaseFromDistributor.getContentPane().add(quantityTextField);
		quantityTextField.setColumns(10);
		
		JButton btnNewButton = new JButton("Purchase");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (distributor.purchase(productTextField.getText(), Integer.parseInt(quantityTextField.getText()))) {
						JOptionPane.showMessageDialog(null, "Purchase successful! :)");
					}
					else {
						JOptionPane.showMessageDialog(null, "Purchase unsuccessful! :( Product does not exist at selected distributor!");
					}
				} catch (NumberFormatException e1) {
					MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e1);
				} catch (RemoteException e1) {
					MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e1);
				}
			}
		});
		btnNewButton.setBounds(124, 125, 165, 23);
		frmPurchaseFromDistributor.getContentPane().add(btnNewButton);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBounds(23, 61, 247, 53);
		frmPurchaseFromDistributor.getContentPane().add(textArea);
		
		JLabel lblNewLabel_2 = new JLabel("Available products");
		lblNewLabel_2.setBounds(21, 40, 138, 14);
		frmPurchaseFromDistributor.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Product:");
		lblNewLabel_3.setBounds(290, 64, 62, 14);
		frmPurchaseFromDistributor.getContentPane().add(lblNewLabel_3);
		
		productTextField = new JTextField();
		productTextField.setColumns(10);
		productTextField.setBounds(348, 61, 80, 20);
		frmPurchaseFromDistributor.getContentPane().add(productTextField);
	}
	
	private void refreshProducts() {
		try {
			companyName = (String) distributorsComboBox.getSelectedItem();
			distributor = (DistributorInterface) registry.lookup(companyName);
			textArea.setText(String.join(", ", distributor.getProducts()));
		} catch (Exception ex) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", ex);
		}
	}
}
