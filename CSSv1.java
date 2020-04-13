/**
 * The CSSv1 class is a threaded socket server that accepts commands from a client,
 * runs the appropriate process, then returns the result to the client.
 * The processes are Linux commands that the server executes on its underlying OS.
 *
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

class ServerThread extends Thread {
    Socket socket;
    InputStream input;   // get the raw input stream
    OutputStream output; // get the raw output stream

    BufferedReader reader; // where to receive input
    PrintWriter writer;    // where to write output
    
    public ServerThread( Socket s ) {
        socket = s;
        try {
            input = socket.getInputStream();   // get the raw input stream
            output = socket.getOutputStream(); // get the raw output stream
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        reader = new BufferedReader(new InputStreamReader(input)); // where to receive input
        writer = new PrintWriter(output, true); // where to write output
    }
    
    @Override
    public void run() {
        String cmd = "";     // the command received from client
        String result = "";  // the output of the server

        System.out.println( "New client connected" ); // success message
        
        try {
            cmd = reader.readLine(); // get a line of input

            /* The iterative (single-threaded) server must support the following client requests:
                 - Date and Time - the date and time on the server
                 - Uptime - how long the server has been running since last boot-up
                 - Memory Use - the current memory usage on the server
                 - Netstat - lists network connections on the server
                 - Current Users - list of users currently connected to the server
                 - Running Processes - list of programs currently running on the server        
            */
            Process p;

            switch( cmd ) {         // switch on client command
                // cmd stays the same
                case "date":            
                case "uptime":
                case "netstat": 
                case "users":
                    break;
                    
                // cmd needs to change
                case "memory":          
                    cmd = "free"; 
                    break;
                case "ps":
                    cmd = "ps -a"; 
                    break;
                    
                // invalid cmd
                default:                
                    writer.println( "Error: invalid command" );
            }
            p = Runtime.getRuntime().exec(cmd);

            // reads the input stream from the external process
            BufferedReader stdInput = new BufferedReader(
                new InputStreamReader(
                    p.getInputStream()
                )
            );

            // append 
            String s = stdInput.readLine();

            while( s != null ) {
               if( ! result.equals( "" ) ) { 
                   result += '\n';
               }
               result += s;
               s = stdInput.readLine();
            }

            System.out.println( cmd );
            System.out.println( result );
            
            writer.println( result ); // write line to output stream

            socket.close();
        } catch( IOException ex ) {
            System.out.println( "Server exception: " + ex.getMessage() );
            ex.printStackTrace();
        }
    }
}

public class CSSv1 {
    
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        System.out.print( "Port: " );
        int port = Integer.parseInt( keyboard.nextLine() );

        try( ServerSocket serverSocket = new ServerSocket( port ) ) {

            System.out.println("Server is listening on port " + port);
            
            // Repeat indefinitely...
            while( true ) {
                
                Socket socket = serverSocket.accept(); // accept the request from client
                new ServerThread( socket ).start();
            }
            
        } catch( IOException ex ) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
