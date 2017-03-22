package ca_project_2;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client_side_1 extends JFrame{
	private CountDownLatch latch_con = new CountDownLatch(1);
	private JButton connect = new JButton("CONNECT");
	private JButton disconnect = new JButton("DISCONNECT");
	private Socket con2serv;
	private String server_ip;
	private ObjectInputStream input;
    private ObjectOutputStream output;
    private JTextField chat_Text;
    private JTextArea chatWindow;
    public void setSize(int width, int height) {
    	super.setSize(350, 300);
    }
	public Client_side_1(String ip) throws InterruptedException{
		super("MESSENGER CLIENT 1");
		server_ip = ip;
		setLayout(new FlowLayout());
		chat_Text = new JTextField(30);
		chat_Text.setEditable(false); //Initially chat text area is uneditable
		add(chat_Text,BorderLayout.NORTH);
		chat_Text.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event) {
						try {
							// Write text message as object and pass it through stream
							output.writeObject("AASHIK : " + chat_Text.getText());
						} catch (IOException e) {
							e.printStackTrace();
						}
						chat_Text.setText("");}
				});
		chatWindow = new JTextArea(12,30);
		add(new JScrollPane(chatWindow),BorderLayout.CENTER);
		setSize(400,50);
		setVisible(true);
		add(connect,BorderLayout.SOUTH);
		connect.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							// When clicked establish a socket connection to server
							con2serv = new Socket(InetAddress.getByName(server_ip),1408);
							// Chat text is now editable
							chat_Text.setEditable(true);
							showActivities("You are connected to Server\n");
							latch_con.countDown();
						} catch (IOException e1) {
							showActivities("Connection Error\n");
						}
							}
				});
		add(disconnect,BorderLayout.SOUTH);
		disconnect.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						chat_Text.setEditable(false);
						try {
							output.writeObject("AASHIK has disconnected....");
						} catch (IOException e1) {
							System.out.println("You are no longer connected");
						}
						// Call this function which closes the input, output stream and socket
						closeChat();
						showActivities("\nYou are disconnected from Server\n");
					}});
		latch_con.await();
			}
	private void run() throws IOException, ClassNotFoundException {
		    output = new ObjectOutputStream(con2serv.getOutputStream());
			output.flush();
			input = new ObjectInputStream(con2serv.getInputStream());
	        while (true) {
	            String message = (String) input.readObject();
		       chatWindow.append(message + "\n");
	        }
	    }
	private void showActivities(final String info){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(info);}
				});
	}
	private void closeChat(){
		try{
			con2serv.close();
			output.close();
			input.close();
			}
			catch(IOException io){
				io.printStackTrace();
			}
	}
public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
			Client_side_1 client_p = new Client_side_1("127.0.0.1");
			client_p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			client_p.run();
			}
		}