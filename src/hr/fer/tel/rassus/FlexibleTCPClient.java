/*
 * This code has been developed at the Department of Telecommunications,
 * Faculty of Electrical Engineering and Computing, University of Zagreb.
 */
package hr.fer.tel.rassus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
*
* @author Ivana Podnar Žarko <ivana.podnar@fer.hr>
*/
public class FlexibleTCPClient {

	final static int PORT = 10002; // server port
	final static String SERVER_NAME = "localhost"; // server name       

	public static void main(String args[]) {         

		// create a client socket and connect it to the name server on the specified port number
		try (Socket clientSocket = new Socket(SERVER_NAME, PORT);/*SOCKET->CONNECT*/) {

			// get the socket's output stream and open a PrintWriter on it
			PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()), true);

			// get the socket's input stream and open a BufferedReader on it
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			
		    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    String inputString;
		    System.out.println("Insert new line of text: ");

		    while ((inputString = in.readLine()) != null && inputString.length() != 0) {
      		    // send a String then terminate the line and flush
		    	outToServer.println(inputString);//WRITE
		    	System.out.println("TCPClient sent: " + inputString);
		    	// read a line of text received from server
				String rcvString = inFromServer.readLine();//READ
				System.out.println("TCPClient received: " + rcvString);
				if (inputString.equals("shutdown"))
					break;
				System.out.println("Insert new line of text: ");
		    }
		    clientSocket.close(); //CLOSE client socket
						
		} catch (IOException ex) {
			System.err.println("Exception caught when opening the socket or trying to read data: " + ex);
			System.exit(1);
		}//CLOSE
	}

	
}
