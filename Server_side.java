package ca_project_2;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
public class Server_side {
	// ObjectOutputStream is maintained for all clients to deliver the text message
   private static LinkedList<ObjectOutputStream> broadcast = new LinkedList<ObjectOutputStream>();
    public static void main(String[] args) throws Exception {
    	// Server Socket waits for a connection on a port and performs desired operation
        ServerSocket server_conn = new ServerSocket(1408,10);
        System.out.println("Server Running..");
        try {
            while (true) {
                new Handler(server_conn.accept()).start();
            }
        } finally {
            server_conn.close();
        }
    }
    // Create a handler thread for each individual client to broadcast his text message
    private static class Handler extends Thread {
        private Socket con2client;
        private ObjectOutputStream output;
    	private ObjectInputStream input;
    	// Socket corresponding to a particular client 
        public Handler(Socket con2client_l) {
            con2client = con2client_l;
        }
        public void run() {
            try {
            	output = new ObjectOutputStream(con2client.getOutputStream());
        		output.flush();
        		input = new ObjectInputStream(con2client.getInputStream());
        		broadcast.add(output);
                while (true) {
                	String message = (String) input.readObject();
                    for (ObjectOutputStream broadcast_l : broadcast){
                        broadcast_l.writeObject(message);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e) {
            	System.out.println(e);
			}
            finally {
                if (broadcast != null) {
                    broadcast.remove(output);
                }
                try {
                    con2client.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
