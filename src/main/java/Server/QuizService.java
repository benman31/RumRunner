package Server;

import QuizQuestion.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class is responsible for all Quiz related services and protocols. Protocol uses simple CODE~MESSSAGE~ pattern
 * where ~ acts as a delimiter between codes and messages. The run method is responsible for all communications
 * with client and execution of appropriate commands. A Socket connected to a client and a persistent record of
 * all users' scores is passed through constructor and are updated within the run method.
 * A helper method called sendMessage is used to construct message strings to be sent in the run method,
 * while playAgain method is used to evaluate whether the gameplay loop of the run method should continue or no not.
 * Protocol codes are as follows: MSG - send a message, NAME -  request a name, wait for response,
 * PLAYAGAIN - request response indicating whether player would like to continue, wait for response,
 * QUESTION - send a question, wait for answer to question
 */
public class QuizService implements Runnable {

    //Protocol codes
    private final String DELIMITER = "~";
    private final String CODE_MSG = "MSG";
    private final String CODE_NAME = "NAME";
    private final String CODE_PLAYAGAIN = "PLAYAGAIN";
    private final String CODE_QUESTION = "QUESTION";

    //Internal codes for use in sendMessage and playAgain methods
    private final String CORRECT = "CORRECT";
    private final String INCORRECT = "INCORRECT";
    private final String CONTINUE = "Y";
    private final String CATEGORY = "CATEGORY";
    private final String WAIT = "WAIT";
    private final String GOODBYE = "GOODBYE";
    private final String GREETING = "WELCOME TO...\n\n#################\n# ULTIMATE QUIZ #\n#################\n\n";

    private Socket socket;
    private LeaderBoard leaderBoard;

    /**
     * Constructs a socket using a provided Socket and LeaderBoard object
     * @param socket A Socket connected to a client
     * @param leaderBoard a LeaderBoard object containing score streak data for all clients
     */
    public QuizService(Socket socket, LeaderBoard leaderBoard){
        this.socket = socket;
        this.leaderBoard = leaderBoard;
    }

    /**
     * This method runs all communication protocol with client and execution of commands.
     * Runs initialization of Scanner and PrintWriter for communicating with client via InputStream and OutputStream.
     * Sends a request for player name (protocol code NAME), which is added to leaderBoard, followed by a request
     * asking the user if they would like to play again (protocol code PLAYAGAIN). If client responds with 'y' or 'Y'
     * The server enters the main gameplay loop cycling through the pattern: send question, wait for answer,
     * evaluate answer, record result, ask if the user would like to play again.
     * Gameplay repeats until the client responds to PLAYAGAIN with anything other than 'Y' or 'y'
     */
    public void run() {

        try {
            try {
                Scanner in = new Scanner(socket.getInputStream());
                in.useDelimiter(DELIMITER);
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                out.print(sendMessage(CODE_NAME));
                out.flush();

                String player = in.next();
                int score = 0;

                leaderBoard.update(player, score);

                out.print(sendMessage(CODE_PLAYAGAIN));
                out.flush();

                while (playAgain(in.next())) {
                    //send leaderboard and current score
                    out.print(CODE_MSG + DELIMITER + leaderBoard.prettyPrintTop3() +
                            "\nYour current Streak is: " + leaderBoard.get(player) + "\n" + DELIMITER);
                    out.flush();

                    out.print(sendMessage(WAIT));
                    out.flush();

                    QuizGenerator quizGen = new QuizGenerator();
                    quizGen.generateQuestion();
                    MultipleChoiceQuestion q = quizGen.getQuestion();

                    //Send quiz question
                    out.print(sendMessage(CATEGORY) + quizGen.getCategory() + "\n"
                            + q.toString() + DELIMITER);
                    out.flush();

                    //Check for valid input and evaluate
                    String questionResponse = in.next();
                    if (!questionResponse.matches("\\d+")){
                        out.print(sendMessage(INCORRECT) + q.correct() + "\n" + DELIMITER);
                        out.flush();
                        score = 0;
                        leaderBoard.update(player, score);
                    }
                    else if (q.evaluate(Integer.parseInt(questionResponse))) {
                        out.print(sendMessage(CORRECT));
                        out.flush();
                        score++;
                        leaderBoard.update(player, score);
                    } else {
                        out.print(sendMessage(INCORRECT) + q.correct() + "\n" + DELIMITER);
                        out.flush();
                        score = 0;
                        leaderBoard.update(player, score);
                    }
                    out.print(sendMessage(CODE_PLAYAGAIN));
                    out.flush();
                }
                //Send goodbye message and remove player from leaderboard
                out.print(sendMessage(GOODBYE));
                out.flush();
                leaderBoard.delete(player);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles construction of most standard messages which are sent to the client with the appropriate CODE~MESSAGE~
     * protocol pattern.
     * @param code the code corresponding to the requested message
     * @return A String message formatted in the CODE~MESSAGE~ protocol pattern
     */
    public String sendMessage(String code){
        String result = "";
        switch (code){
            case CODE_NAME:
                result = CODE_NAME + DELIMITER + GREETING + "Please enter your name:\n" + DELIMITER;
                break;
            case CODE_PLAYAGAIN:
                result = CODE_PLAYAGAIN + DELIMITER + "Are you ready for the next question? (Enter Y to continue or N to quit)" + DELIMITER;
                break;
            case WAIT:
                result = CODE_MSG + DELIMITER + "Please wait while we prepare your question...\n" + DELIMITER;
                break;
            case CORRECT:
                result = CODE_MSG + DELIMITER + "That's correct! Well done!\n" + DELIMITER;
                break;
            case INCORRECT:
                result = CODE_MSG + DELIMITER + "Sorry, that's not it! The correct answer is ";
                break;
            case CATEGORY:
                result = CODE_QUESTION + DELIMITER + "The category is: ";
                break;
            case GOODBYE:
                result = CODE_MSG + DELIMITER + "Thanks for playing, see you next time!" + DELIMITER;
                break;
        }
        return result;
    }

    /**
     * Evaluates a code indicating whether a player would like to continue playing, if code is 'y' or 'Y' method returns true
     * @param code a String indicating whether or not a player would like to continue playing the quiz game
     * @return true if code is 'y' or 'Y', or returns false otherwise
     */
    public boolean playAgain(String code){
        return code.toUpperCase().equals(CONTINUE);
    }
}
