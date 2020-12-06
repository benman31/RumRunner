import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A client designed to connect to a QuizServer using a Socket and communicate using a simple protocol with the pattern:
 * CODE~MESSAGE~ where the tilde '~' acts as a separator between successive codes and messages sent from the server.
 * The codes are MSG, NAME, PLAYAGAIN and QUESTION, each of which are followed by one or more lines of text in the
 * MESSAGE field. The client is responsible for interpreting these codes by printing their messages to the console and
 * sending user input back to the server where applicable.
 */
public class QuizClient {

    final String DELIMITER = "~";
    final String CODE_MSG = "MSG";
    final String CODE_NAME = "NAME";
    final String CODE_PLAYAGAIN = "PLAYAGAIN";
    final String CODE_QUESTION = "QUESTION";

    /**
     * Establishes a connection to QuizClient through a Socket.
     * The method then receives code by passing the Socket InputStream object to a Scanner with delimiter set to '~'
     * which allows codes and their messages to be parsed one by one. Codes NAME, PLAYAGAIN, and QUESTION require
     * user responses submitted back to the QuizServer. This is done via Socket outputStream passed through
     * a printWriter
     * @param addr the IP adress of the QuizServer
     * @param port the port number of the QuizServer
     * @throws IOException if IP address or port number are invalid or do not exist
     */
     public void openSocket(String addr, int port) throws IOException {
         try(Socket s = new Socket(addr, port)){

             PrintWriter out = new PrintWriter(s.getOutputStream());
             Scanner socketScan = new Scanner(s.getInputStream());
             socketScan.useDelimiter(DELIMITER);

             while(socketScan.hasNext()){
                 String serverCode = socketScan.next().toUpperCase();
                 switch (serverCode){
                     case CODE_MSG:
                         System.out.println(socketScan.next());
                         break;
                     case CODE_NAME:
                         System.out.println(socketScan.next());
                         out.print(getInput() + DELIMITER);
                         out.flush();
                         break;
                     case CODE_PLAYAGAIN:
                         System.out.println(socketScan.next());
                         String input = getInput().toUpperCase();
                         out.print(input + DELIMITER);
                         out.flush();
                         break;
                     case CODE_QUESTION:
                         System.out.println(socketScan.next());
                         out.print(getInput() + DELIMITER);
                         out.flush();
                         break;
                 }
             }
         }
     }

    /**
     * gets user input from console in the form of a String
     * @return a String containing user input
     */
     public String getInput(){
         Scanner consoleScan = new Scanner(System.in);
         String input = consoleScan.next();
         consoleScan.nextLine();
         return input;
     }

    /**
     * Checks if a String from user input via console is an integer, and if so converts it to type int
     * so that it may be passed to Socket constructor as the port argument
     * @return the port number as type int
     */
     public int parsePort(){
         String input = getInput();
         while(!input.matches("\\d+")){
             System.out.println("Invalid input. Please enter valid a port number (an integer between 0 and 65535");
             input = getInput();
         }
         return Integer.parseInt(input);
     }

    /**
     * A simple driver method that constructs a QuizClient object and requests user input via console to obtain
     * a valid IP address and port number of a QuizServer with which to connect;
     * @param args Command line arguments are not used for this method
     * @throws IOException if IP address or Port number do not exist or are invalid
     */
    public static void main(String[] args) throws IOException {

        String addr;
        int port;
        QuizClient quizClient = new QuizClient();

        System.out.println("Please enter the IP Address of the Quiz Server");
        addr = quizClient.getInput();
        System.out.println("Please enter the port for the Quiz Server");
        port = quizClient.parsePort();
        quizClient.openSocket(addr, port);

    }
}
