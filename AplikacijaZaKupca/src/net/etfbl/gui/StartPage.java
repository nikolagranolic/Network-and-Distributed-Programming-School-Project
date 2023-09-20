package net.etfbl.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StartPage {
	public JFrame frame;
	private JButton loginButton;
	private JButton registerButton;
	
	public StartPage() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setResizable(false);
        frame.setTitle("AplikacijaZaKupca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 134);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		new LoginForm().frame.setVisible(true);
        		frame.setVisible(false);
        	}
        });
        loginButton.setBounds(48, 39, 90, 23);

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		new RegistrationForm(frame).frame.setVisible(true);
        		frame.setVisible(false);
        	}
        });
        registerButton.setBounds(148, 39, 90, 23);
        panel.setLayout(null);

        panel.add(loginButton);
        panel.add(registerButton);

        frame.getContentPane().add(panel);
	}
}
