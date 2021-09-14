# Concurrent Socket Server

---------------
Purpose
---------------

The purpose of this project is to learn about concurrent servers and compare them to iterative servers. In order to learn about the concurrent servers, the team has implemented two programs, a client and a server.  Each program has different functions it needs to perform.  The server will send the correct output to the client based on the commands that are sent as input.  There are six operations that the server can do.  Knowing this, the server can list the current date and time from the server, server uptime since last started, current memory usage, current connections to the network, a list of users currently logged on, and a list of applications that are running on the server.  The server will also do maintenance and delete any unneeded instances.  As for the client program, it will send requests about the command to enter, the IP address and the port to connect to, and the amount of clients for the server to handle.  Also, it will get the turn-around time for every client request, the average turn-around time for all client requests, and the total turn-around time for all client requests. 


---------------
Client-Server Setup and Configuration
---------------

The project included multithreaded client and server programs. Each program was written in Java, using the Socket class for the client program and ServerSocket class for the server program for network endpoint communications. Each program prompts the user for necessary input at runtime.


---------------
Client Program
---------------

With one difference regarding time tracking, the client program in this project is the same as the client program from the Iterative Socket Server project. Upon execution, the client prompts the user for the hostname and port number of the server. Next, the client prompts the user for a command to send to the server, followed by a prompt for the number of identical requests to send. Sending simultaneous requests to the server allows for the measurement of average server response time, which is expected to change as the number of requests changes. For each request, the client program creates a new thread. This architecture supports multiple simultaneous requests that are not hindered by the potential delay of previous requests.

To implement multithreading, the Thread class was extended into a ClientThread class. The ClientThread class creates a new Socket endpoint to attempt connection with the server, then writes the user-input command to the Socket’s output stream and receives the server output from the Socket’s input stream. The last bit of functionality of the ClientThread class is request time tracking. When a Client object is instantiated, its constructor sets its start time field using the nanoTime() function. Then, after the server response is received, the Client object reports the requested output immediately and records the end time using nanoTime(). The nanoTime() function was chosen over comparable methods like currentTimeMillis() due to currentTimeMillis()’s reliance on system time, which can change during program execution. When the client program’s main thread is ready to report the various threads’ runtimes, it calls each Client thread’s getRuntime() function after ensuring that the thread has completed its assigned task. These runtimes can then be averaged to determine the average runtime per client.

Because the server program is multithreaded, the individual times from each thread cannot be summed to calculate the total runtime of the program. For example, if we have 5 ClientThreads that each take 10 ms to complete and they start in 1 ms increments: thread 1 would start at time 0 and run until 10 ms, thread 2 at time 1 until 11 ms, thread 3 at time 2 until 12 ms, thread 4 at time 3 until 13 ms, then thread 5 at time 4 until 14 ms. Thus, the total runtime would be 14ms. However, if the implementation sums each ClientThread’s individual runtime, the program would report a total runtime of 50 ms (10 + 10 + 10 + 10 + 10). To combat this, directly before the group of ClientThreads is instantiated, the program records the start time. Then, immediately following the closure of the final ClientThread, the program records the end time and displays the total elapsed time of all requests.

---------------
Server Program
---------------

Upon execution, the server prompts the user for the port number and tries to create a new ServerSocket instance using the given port number. If the chosen port number is already in use, the program will throw an IOException and terminate. A more robust program could prompt the user for an alternate port number or use another method to choose a different port number, but this was not necessary for this application. Once the ServerSocket is successfully instantiated, the program uses while(true) to purposely loop infinitely. During each loop, the program listens for incoming requests. When a request is received, it is accepted and sent to a new ServerThread instance. When the ServerThread runs, the command is processed from the input stream, where it is then handled by a switch statement.

The switch statement accepts the following commands: date, uptime, netstat, users, memory, and ps. These commands correspond to Linux commands that can be run in a separate process using Java’s Runtime’s exec(command) function. This architecture allows the program to retrieve meta-information about its own execution. Table 1 shows the commands the server accepts and the corresponding Runtime.exec() commands. The program reads the output of the commands from its input stream and send the output to the server’s output stream, sending the response to the client. After the response is sent to the client, the socket is closed. 
