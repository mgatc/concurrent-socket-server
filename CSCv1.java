/**
 * The CSCv1 class is a threaded socket server client that sends commands to a server,
 * tracks the request time, then prints the result.
 *
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

class ClientThread extends Thread {
    String hostname;
    int port;
    String command;
    long start = 0;
    long end = 0;

    ClientThread( String name, String hname, int portNumber, String c, long time ) {
            super( name );
            hostname = hname;
            port = portNumber;
            command = c;
            start = time;  // Record task's start time on thread creation
            this.start();  // Start thread immediately upon creation
    }
    public long getRuntime() {
        if( end != 0 ) // Double-check thread has finished
            return end - start; // Report task duration in ns
        else
            return -1;
    }
    public void run() {
        try( Socket socket = new Socket( hostname, port ) ) {
            // Open input stream and reader
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );
            // Open output stream and writer
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter( output, true );

            writer.println( command ); // send the command

            // Record output
            String s = reader.readLine();
            String result = "";
            
            while( s != null ) {
               result = result + "\n" + s;
               s = reader.readLine();
            }
            
            // Record task's ending time
            end = System.nanoTime();
            
        } catch(UnknownHostException ex ){
            System.out.println( "Server not found:" + ex.getMessage());
        } catch( IOException ex ) {
            System.out.println( "IOException in thread " + this.getName() + ": " + ex.getMessage() );
        }
    }
}
public class CSCv1 {
    public static void main( String[] args ) {
        Scanner keyboard = new Scanner( System.in );

        System.out.print( "Hostname: " );
        String hostname = keyboard.nextLine();

        System.out.print( "Port: " );
        int port = Integer.parseInt( keyboard.nextLine() );

        // Solicit command from user
        String command = "";

        while( !command.equals( "exit" ) ) {
                System.out.println( "Available commands: date uptime memory netstat users ps exit" );
                System.out.print( "Command to perform: " );
                command = keyboard.nextLine();

                if( ! command.equals( "exit" ) ) {
                        // Solicit num clients
                        System.out.print( "Number of simulated clients: " );
                        int clients = Integer.parseInt( keyboard.nextLine() );
                        ArrayList<ClientThread> client = new ArrayList<>();
                        
                        long runtime = -1 * System.nanoTime(); // take the inverse of the time before starting
                        long ttime = 0;

                        for(int i = 0; i < clients; i++){
                            // Create a new client
                            client.add( new ClientThread( "Client " + (i+1), hostname, port, command, System.nanoTime() ) );
                        }
                        for( int i = 0; i < client.size(); i++ ){
                            // wait until this client thread is finished
                            while( client.get(i).isAlive() ) {}

                            // get runtime of this client
                            long duration = client.get(i).getRuntime();
                            ttime += duration; // add this client's duration to the total thread time
                        }
                        runtime += System.nanoTime(); // add the end time to the inverse of the start time
                        
                        // Print data
                        System.out.println( command + " " + clients + " " + runtime/1000000 + " " + (ttime/1000000)/clients );
                        System.out.println();
                }
        }
    }
}