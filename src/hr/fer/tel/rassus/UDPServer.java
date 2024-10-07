/*
 * This code has been developed at the Department of Telecommunications,
 * Faculty of Electrical Engineering and Computing, University of Zagreb.
 */

package hr.fer.tel.rassus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author Ivana Podnar Žarko <ivana.podnar@fer.hr>
 */
public class UDPServer {

	private final static int PORT = 10001; // server port
	private static DatagramSocket socket;

	public static void main(final String[] args) throws IOException {

		final byte[] rcvBuf = new byte[256]; // received bytes
		byte[] sendBuf; // sent bytes
		String rcvStr;

		socket = new DatagramSocket(PORT);

		while (true) { //Infinite loop only for a simple single-threaded example
			// create a DatagramPacket for receiving packets
			final DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);

			// receive packet
			socket.receive(packet); //RECVFROM

			// construct a new String by decoding the specified subarray of
			// bytes using the platform's default charset
			rcvStr = new String(packet.getData(), packet.getOffset(),
					packet.getLength());
			System.out.println("UDPServer received: " + rcvStr);

			// encode a String into a sequence of bytes using the platform's
			// default charset
			sendBuf = rcvStr.toUpperCase().getBytes();
			System.out.println("UDPServer sends: " + rcvStr.toUpperCase());

			// create a DatagramPacket for sending packets
			final DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, packet.getAddress(), packet.getPort());

			// send packet
			socket.send(sendPacket); //SENDTO
		}
	}

}

