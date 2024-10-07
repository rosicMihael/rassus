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
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Ivana Podnar Žarko <ivana.podnar@fer.hr>
 */
public class TCPServer {

	private static final int PORT = 10002; // server port
	private static Boolean runningFlag = false; 

	public static void main(String args[]) {
		// create a server socket and bind it to the specified port on the local host
		try (ServerSocket serverSocket = new ServerSocket(PORT);/*SOCKET->BIND->LISTEN*/) {

			System.out.println("TCPServer: Waiting for clients");

			while (runningFlag = true) { //infinite loop only for a simple single-threaded example
				try (// create a new socket, accept and listen for a connection to be
						// made to this socket
						Socket clientSocket = serverSocket.accept();//ACCEPT
						// create a new BufferedReader from an existing InputStream
						BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
								clientSocket.getInputStream()));
						// create a PrintWriter from an existing OutputStream
						PrintWriter outToClient = new PrintWriter(new OutputStreamWriter(
								clientSocket.getOutputStream()), true);) {

					String rcvString;

					// read a few lines of text
					while ((rcvString = inFromClient.readLine()) != null/*READ*/) {
						System.out.println("TCPServer received: " + rcvString);

						//shutdown the server if requested
						if (rcvString.contains("shutdown")) {
							return;
						}

						String sndString = rcvString.toUpperCase();

						// send a String then terminate the line and flush
						outToClient.println(sndString);//WRITE
						System.out.println("TCPServer sent: " + sndString);
					}
				} catch (IOException ex) {
					System.err.println("Exception caught when trying to read or send data: " + ex);
				}//clientSocket CLOSE
			}
		} catch (IOException ex) {
			System.err.println("Exception caught when opening the socket or waiting for a connection: " + ex);
		} //CLOSE
	}

}
