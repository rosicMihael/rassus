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
 * @author Ivana Podnar žarko <ivana.podnar@fer.hr>
 */
public class TCPClient {
	final static int PORT = 10002; // server port
	final static String SERVER_NAME = "localhost"; // server name       

	public static void main(String args[]) {         

		// create a client socket and connect it to the name server on the specified port number
		try (Socket clientSocket = new Socket(SERVER_NAME, PORT);/*SOCKET->CONNECT*/) {

			String sendString1 = "Any string";
			String sendString2 = "shutdown";

			// get the socket's output stream and open a PrintWriter on it
			PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()), true);

			// get the socket's input stream and open a BufferedReader on it
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			// send a String then terminate the line and flush
			outToServer.println(sendString1);//WRITE
			System.out.println("TCPClient sent: " + sendString1);

			outToServer.println(sendString2);//WRITE
			System.out.println("TCPClient sent: " + sendString2);

			
			// read a line of text
			String rcvString = inFromServer.readLine();//READ
			System.out.println("TCPClient received: " + rcvString);
		} catch (IOException ex) {
			System.err.println("Exception caught when opening the socket or trying to read data: " + ex);
			System.exit(1);
		}//CLOSE
	}

}
