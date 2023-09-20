package net.etfbl.gui;

import javax.swing.JFrame;
import javax.swing.JTextField;

import net.etfbl.util.MyLogger;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.awt.event.ActionEvent;

public class PromotionalMessageForm {

	public JFrame frmPromotionalMessage;
	private JTextField textField;
	private int port;
	private InetAddress address;
	private MulticastSocket multicastSocket;
	
	public PromotionalMessageForm(MulticastSocket multicastSocket, int port, InetAddress address) {
		this.port = port;
		this.address = address;
		this.multicastSocket = multicastSocket;
		initialize();
	}

	private void initialize() {
		frmPromotionalMessage = new JFrame();
		frmPromotionalMessage.setTitle("Promotional message");
		frmPromotionalMessage.setResizable(false);
		frmPromotionalMessage.setBounds(100, 100, 450, 150);
		frmPromotionalMessage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmPromotionalMessage.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 30, 418, 41);
		frmPromotionalMessage.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendPromotionalMessage();
			}
		});
		btnNewButton.setBounds(172, 82, 89, 23);
		frmPromotionalMessage.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Promotional message text:");
		lblNewLabel.setBounds(10, 11, 167, 14);
		frmPromotionalMessage.getContentPane().add(lblNewLabel);
	}
	
	private void sendPromotionalMessage() {
		try {
			String msg = textField.getText();
			byte[] buf = new byte[msg.getBytes().length];
			buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			multicastSocket.send(packet);
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
