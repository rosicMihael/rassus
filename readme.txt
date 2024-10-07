Java Examples: Using Socket API and java.net

------------------------------------
1. UDPServer.java and UDPClient.java
------------------------------------
Simple examples of a UDP server and a matching UDP client. The server performs a single functionality:
it capitalizes received strings.
The UDP server receives datagram packets iteratively in an infinite loop. It reads the datagram,  
transfers it to a string, capitalizes the strings and sends the capitalized strings to the 
originating UDP address.
The UDP client is really simple: It connects to the UDP server, sends a single string to the
server and displays the received string.
--
First run the UDPServer and then UDPClient (even multiple times).


------------------------------------
2. TCPServer.java and TCPClient.java
------------------------------------
Simple examples of a single-threaded TCP server and a matching UDP client. The server performs 
a single functionality: it capitalizes received strings.
The TCP server runs in an infinite loop and will terminate when it receives a shutdown string from 
the client.
The TCP client sends 2 strings to the server: "Any string" and "shutdown". The second string will
shutdown the server.
--
First run the TCPServer and then TCPClient which will shutdown the server.
You can also use FlexibleTCPClient to send text to a running TCPServer. First start the TCPServer and then 
FlexibleTCPClient. Enter input text in client terminal, the connection to the server will terminate when
you send an empty string (just press Enter), but the server keeps running. If you type "shutdown" and 
send this string from the client to the server, the TCPServer will be shutdown.


-------------------------------------------------------------------
3. MultithreadedServer.java, Worker.java and FlexibleTCPClient.java
-------------------------------------------------------------------
This is an example of a multithreaded TCP server which uses a pull of worker threads for communication 
with clients. The server performs a single functionality: it capitalizes received strings.
The server first performs method startup() to create a server socket, and then enters its working mode in
method loop(). In this method it waits for incoming connection requests to create a new worker thread
associated with a new active socket for each new request. The new socket is used for all communication 
with a client.
A connection to a client is terminated when the client sends an empty string to the server.
The server initiates a shutdown procedure when it receives string "shutdown" from a client (this also
terminates the client's connection). However, the server will not initiate this procedure while there 
are other active connections open to the server and will wait until all clients disconnect to initiate
the shutdown procedure. It will also not accept any new client connections after receiving the shutdown
request.
--
First run the MultithreadedServer and then two instances of FlexibleTCPClient. Check the list of TCP
connections on your host (use command "netstat") to check which ports are used by the server.
Start typing strings on client terminals. Then type "shutdown" on one of the clients to initiate
the shutdown procedure. However, as long as the second client connection is active, the server will not
terminate. When you send an empty string from the second client, its connection to the server will
terminate and thus the server will perform the shutdown method and close the server socket and pool
of worker threads.
