package net.etfbl.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import net.etfbl.helper.HashMapCustom;
import net.etfbl.helper.PromotionalMessageThread;
import net.etfbl.model.Order;
import net.etfbl.model.Product;
import net.etfbl.util.ConnectionFactoryUtil;
import net.etfbl.util.MyLogger;
import net.etfbl.util.ProductDataTableModel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;

public class ClientHomePage {
	public JFrame frmClientapp;
	private JScrollPane jScrollPane1;
	private JTable tblProducts;
	private JTextField productIdTextField;
	private JTextField productQuantityTextField;
	private JTextField emailTextField;
	private ArrayList<Product> products;
	private HashMap<Product, Integer> hashMap = new HashMap<Product, Integer>();
	private HashMapCustom hmc = new HashMapCustom(hashMap);
	private JTextArea orderContent;
	
	public ClientHomePage() {
		products = getProducts();
		initialize();
	}

	private void initialize() {
		frmClientapp = new JFrame();
		frmClientapp.setTitle("ClientApp");
		frmClientapp.setResizable(false);
		frmClientapp.setBounds(100, 100, 659, 420);
		frmClientapp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClientapp.getContentPane().setLayout(null);
		
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(10, 36, 210, 271);
		tblProducts = new JTable();
		
		ProductDataTableModel productsTableModel = new ProductDataTableModel(products);
		tblProducts.setModel(productsTableModel);
		
		jScrollPane1.setViewportView(tblProducts);
		frmClientapp.getContentPane().add(jScrollPane1);
		
		JLabel lblNewLabel = new JLabel("Products:");
		lblNewLabel.setBounds(10, 11, 118, 14);
		frmClientapp.getContentPane().add(lblNewLabel);

		
		JLabel promotionalMessageLabel = new JLabel("");
		promotionalMessageLabel.setBounds(10, 343, 627, 31);
		frmClientapp.getContentPane().add(promotionalMessageLabel);
		
		PromotionalMessageThread promotionalMessageThread = new PromotionalMessageThread(promotionalMessageLabel);
		
		JLabel lblNewLabel1 = new JLabel("Promotional message:");
		lblNewLabel1.setBounds(10, 318, 153, 14);
		frmClientapp.getContentPane().add(lblNewLabel1);
		
		JLabel lblNewLabel_1 = new JLabel("Order content:");
		lblNewLabel_1.setBounds(245, 76, 109, 14);
		frmClientapp.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Product ID:");
		lblNewLabel_2.setBounds(245, 40, 73, 14);
		frmClientapp.getContentPane().add(lblNewLabel_2);
		
		productIdTextField = new JTextField();
		productIdTextField.setBounds(313, 37, 96, 20);
		frmClientapp.getContentPane().add(productIdTextField);
		productIdTextField.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Quantity:");
		lblNewLabel_3.setBounds(439, 40, 60, 14);
		frmClientapp.getContentPane().add(lblNewLabel_3);
		
		productQuantityTextField = new JTextField();
		productQuantityTextField.setBounds(496, 37, 49, 20);
		frmClientapp.getContentPane().add(productQuantityTextField);
		productQuantityTextField.setColumns(10);
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addProducts();
			}
		});
		addButton.setBounds(564, 36, 73, 23);
		frmClientapp.getContentPane().add(addButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(245, 101, 392, 159);
		frmClientapp.getContentPane().add(scrollPane);
		
		orderContent = new JTextArea();
		orderContent.setLineWrap(true);
		orderContent.setEditable(false);
		scrollPane.setViewportView(orderContent);
		
		JLabel Contan = new JLabel("Email:");
		Contan.setBounds(245, 285, 73, 14);
		frmClientapp.getContentPane().add(Contan);
		
		emailTextField = new JTextField();
		emailTextField.setBounds(328, 282, 145, 20);
		frmClientapp.getContentPane().add(emailTextField);
		emailTextField.setColumns(10);
		
		JButton btnNewButton = new JButton("Create order");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createOrder();
			}
		});
		btnNewButton.setBounds(507, 281, 130, 23);
		frmClientapp.getContentPane().add(btnNewButton);
		promotionalMessageThread.setDaemon(true);
		promotionalMessageThread.start();
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
	
	private void addProducts() {
		int index = products.indexOf(new Product(Integer.parseInt(productIdTextField.getText()), ""));
		
		Product selectedProduct = products.get(index);
		
		if (hashMap.containsKey(selectedProduct)) {
			Integer prevCount = hashMap.get(selectedProduct);
			hashMap.put(selectedProduct, prevCount + Integer.parseInt(productQuantityTextField.getText()));
		}
		else {
			hashMap.put(selectedProduct, Integer.parseInt(productQuantityTextField.getText()));
		}
		
		orderContent.setText(hmc.toString());
	}
	
	private void createOrder() {
		try {
			Connection connection = ConnectionFactoryUtil.createConnection();
			Channel channel = connection.createChannel();
			String queueName = loadProperties().getProperty("MQ_QUEUE_NAME");
			channel.queueDeclare(queueName, false, false, false, null);		
			
			XmlMapper xmlMapper = new XmlMapper();
			xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		    String xml = xmlMapper.writeValueAsString(new Order(emailTextField.getText(), hashMap));
		    

			emailTextField.setText("");
			productIdTextField.setText("");
			productQuantityTextField.setText("");
			orderContent.setText("");
			hashMap.clear();
			
			channel.basicPublish("", queueName, null, xml.getBytes("UTF-8"));
			channel.close();
			connection.close();
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
