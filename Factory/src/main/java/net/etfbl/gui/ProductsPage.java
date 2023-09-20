package net.etfbl.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.json.JSONArray;
import org.json.JSONObject;
import net.etfbl.model.Product;
import net.etfbl.util.MyLogger;
import net.etfbl.util.ProductDataTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProductsPage {
	public JFrame frmProducts;
	private JScrollPane jScrollPane1;
	private JTable tblProducts;
	private JTextField productIdTextField;
	private JTextField productNameTextField;
	
	public ProductsPage() {
		initialize();
	}

	private void initialize() {
		frmProducts = new JFrame();
		frmProducts.setTitle("Products");
		frmProducts.setResizable(false);
		frmProducts.setBounds(100, 100, 450, 275);
		frmProducts.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmProducts.getContentPane().setLayout(null);
		
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(10, 36, 207, 197);
		tblProducts = new JTable();
		
		ProductDataTableModel productsTableModel = new ProductDataTableModel(getProducts());
		tblProducts.setModel(productsTableModel);
		
		jScrollPane1.setViewportView(tblProducts);
		frmProducts.getContentPane().add(jScrollPane1);
		
		JLabel lblNewLabel = new JLabel("Products:");
		lblNewLabel.setBounds(10, 11, 118, 14);
		frmProducts.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("ID:");
		lblNewLabel_1.setBounds(242, 43, 125, 14);
		frmProducts.getContentPane().add(lblNewLabel_1);
		
		productIdTextField = new JTextField();
		productIdTextField.setBounds(296, 40, 118, 20);
		frmProducts.getContentPane().add(productIdTextField);
		productIdTextField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Name:");
		lblNewLabel_2.setBounds(242, 68, 71, 14);
		frmProducts.getContentPane().add(lblNewLabel_2);
		
		productNameTextField = new JTextField();
		productNameTextField.setBounds(296, 65, 118, 20);
		frmProducts.getContentPane().add(productNameTextField);
		productNameTextField.setColumns(10);
		
		JButton addButton = new JButton("Create");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProduct();
			}
		});
		addButton.setBounds(282, 100, 89, 23);
		frmProducts.getContentPane().add(addButton);
		
		JButton updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateProduct();
			}
		});
		updateButton.setBounds(282, 134, 89, 23);
		frmProducts.getContentPane().add(updateButton);
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteProduct();
			}
		});
		deleteButton.setBounds(282, 168, 89, 23);
		frmProducts.getContentPane().add(deleteButton);
	}
	
	private void refresh() {
		ProductDataTableModel productsTableModel = new ProductDataTableModel(getProducts());
		tblProducts.setModel(productsTableModel);
	}
	
	private Properties loadProperties() {
		File file = new File("src/main/java/resources/config.properties");
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
	
	private ArrayList<Product> getProducts() {
		ArrayList<Product> products = new ArrayList<Product>();
		try {
			String baseUrlString = loadProperties().getProperty("BASE_URL");
			URL url = new URL(baseUrlString + "/products");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        StringBuilder response = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null) {
	            response.append(line);
	        }
	        br.close();
	        conn.disconnect();

	        JSONArray productsArray = new JSONArray(response.toString());
	        
	        for (int i = 0; i < productsArray.length(); i++) {
	            JSONObject productObject = productsArray.getJSONObject(i);
	            products.add(new Product(productObject.getInt("productId"), productObject.getString("productName")));
	        }
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		return products;
	}
	
	private void createProduct() {
		int productId = Integer.parseInt(productIdTextField.getText());
        String productName = productNameTextField.getText();

        JSONObject registrationObj = new JSONObject();

		registrationObj.put("productId", productId);
		registrationObj.put("productName", productName);
		
		try {
			String baseUrlString = loadProperties().getProperty("BASE_URL");
			URL url = new URL(baseUrlString + "/products");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStream os = conn.getOutputStream();
			os.write(registrationObj.toString().getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(null, "Product with such Product ID already exists!");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			os.close();
			conn.disconnect();

			refresh();
			JOptionPane.showMessageDialog(null, "Creation successful! :)");
			
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
	
	private void deleteProduct() {
		String productId = productIdTextField.getText();
		
		try {
			String baseUrlString = loadProperties().getProperty("BASE_URL");
			URL url = new URL(baseUrlString + "/products/" + productId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/json");
			
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(null, "Product with such Product ID doesn't exist!");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			conn.disconnect();

			refresh();
			JOptionPane.showMessageDialog(null, "Delete successful! :)");
			
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
	
	private void updateProduct() {
		int productId = Integer.parseInt(productIdTextField.getText());
        String productName = productNameTextField.getText();

        JSONObject registrationObj = new JSONObject();

		registrationObj.put("productId", productId);
		registrationObj.put("productName", productName);
		
		try {
			String baseUrlString = loadProperties().getProperty("BASE_URL");
			URL url = new URL(baseUrlString + "/products");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStream os = conn.getOutputStream();
			os.write(registrationObj.toString().getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(null, "Product with such Product ID doesn't exist!");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			os.close();
			conn.disconnect();

			refresh();
			JOptionPane.showMessageDialog(null, "Update successful! :)");
			
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
