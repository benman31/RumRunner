package QuizQuestion;

import java.util.Random;

/**
 * Represents a single multiple choice question with 4 possible answers of which only 1 is correct.
 * This class has a method to set question text, called setQuestionText, and a method for adding one of 4 choices
 * at a random index, called addChoice. The method called correct returns the String containing the correct answer.
 * The method called evaluate takes an answer as an argument and returns true if it is correct or false otherwise.
 * Finally, there is a method called toString which converts the question text and answers into a String formatted for printing.
 */
public class MultipleChoiceQuestion implements Quizable{

    private final String ROW_DIVIDER = "%%%%%%%%%%%%%%%%%%%%%%%%%\n";
    private final int MAX_ANSWERS = 4;

    private Random r;
    private String question;
    private String[] answers;
    private String correctAnswer;

    /**
     * Constructs an empty MultipleChoiceQuestion
     */
    public MultipleChoiceQuestion (){
        r = new Random();
        question = "";
        answers = new String[MAX_ANSWERS];
        correctAnswer = "";
    }

    /**
     * Set the question text
     * @param text the text of the question
     */
    public void setQuestionText(String text) {
        question = text;
    }

    /**
     * Add a choice for the multiple choice question at a random index
     * @param choice  the text of one choice, or potential answer to a given quiz question
     * @param correct true if the choice is the correct answer to the question, false if not
     */
    public void addChoice(String choice, boolean correct) {
        int randomIndex;
        boolean done = false;
        do{
            randomIndex = r.nextInt(MAX_ANSWERS);
            if(answers[randomIndex] == null){
                answers[randomIndex] = choice;
                if(correct){
                    correctAnswer = randomIndex + ": " + choice;
                }
                done = true;
            }
        }
        while(!done);
    }

    /**
     * Returns the text of the correct answer to the quiz question
     * @return the text of the correct answer
     */
    public String correct()
    {
        return correctAnswer;
    }

    /**
     * Determines if the given guess is correct or not, rejects any guesses that are out of bounds
     * @param guess the guess (made by a user) which is to be evaluated
     * @return true if the guess matches the solution or false otherwise
     */
    public boolean evaluate(int guess) {
        boolean result = false;
        if (guess < 0 || guess > 3){
            result = false;
        }
        else result = answers[guess].equals(correctAnswer.substring(3));
        return result;
    }

    /**
     * Converts the multiple choice question into a multi-line string containing the question and all possible answers
     * @return a mulit-line string of text representing a multiple choice question
     */
    public String toString(){
        String result = ROW_DIVIDER + question + "\n" + ROW_DIVIDER;

        for(int i = 0; i < MAX_ANSWERS; i++){
            result = result + i + ": " + answers[i] + "\n";
        }
        return result + ROW_DIVIDER;
    }
}
