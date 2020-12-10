package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server that runs quiz services in an infinite loop. While running the server persistently keeps track of
 * all user Streak scores with the LeaderBoard object.
 * A built-n main method is included to construct and launch the QuizServer.
 */
public class QuizServer {

    private final int QUIZ_PORT;
    private LeaderBoard leaderBoard;

    /**
     * Constructs QuizServer with default port set to 7777
     */
    public QuizServer(){
        QUIZ_PORT = 7777;
        leaderBoard =  new LeaderBoard();
    }

    /**
     * Constructs QuizServer with user specified port
     * @param port the port with which the server will wait for connections
     */
    public QuizServer(int port){
        QUIZ_PORT = port;
        leaderBoard = new LeaderBoard();
    }

    /**
     * Launches the server by creating a ServerSocket on a given port and starting a new thread running a QuizService
     * object for each new client connection. The server will continue to accept new client connections until manually terminated
     * @throws IOException if port does not exist
     */
    public void openServer() throws IOException {
        ServerSocket quizServer = new ServerSocket(QUIZ_PORT);
        System.out.println("Launching Quiz Server - Waiting for connection. on port: " + QUIZ_PORT);

        while(true){
            Socket s = quizServer.accept();
            System.out.println("Client has connected.");
            QuizService quizService = new QuizService(s, leaderBoard);
            Thread t = new Thread(quizService);
            t.start();
        }
    }

    /**
     * Constructs and launches a default QuizServer
     * @param args not used in this method
     * @throws IOException if port number does not exist
     */
    public static void main(String[] args) throws IOException {

        QuizServer server = new QuizServer();
        server.openServer();
    }
}
