package net.etfbl.helper;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JLabel;

import net.etfbl.util.MyLogger;

public class PromotionalMessageThread extends Thread {
	private JLabel promotionalMessageLabel;
	
	public PromotionalMessageThread(JLabel label) {
		promotionalMessageLabel = label;
	}
	public void run() {
		try {
			Properties properties = new Properties();
			File file = new File("src/resources/config.properties");
			properties.load(new FileInputStream(file));
			
			InetAddress address = InetAddress.getByName(properties.getProperty("MULTICAST_HOST"));
			int port = Integer.parseInt(properties.getProperty("MULTICAST_PORT"));
			MulticastSocket socket = new MulticastSocket(port);
			socket.joinGroup(address);	
			MyLogger.LOGGER.log(Level.INFO, "Joined multicast group" + properties.getProperty("MULTICAST_HOST"));
			byte[] buffer = new byte[512];
			
			while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                
                MyLogger.LOGGER.log(Level.INFO, "New promotional message received.");
                
                promotionalMessageLabel.setText(received);
            }
		} catch (Exception e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
