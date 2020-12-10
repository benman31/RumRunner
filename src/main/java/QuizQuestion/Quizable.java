package QuizQuestion;

/**
 * An interface for a quiz question
 */
public interface Quizable {

    /**
     * Set the question text
     * @param text the text of the question
     */
    void setQuestionText(String text);

    /**
     * Add a choice for the multiple choice question
     * @param choice the text of one choice, or potential answer to a given quiz question
     * @param correct true if the choice is the correct answer to the question, false if not
     */
    void addChoice(String choice, boolean correct);

    /**
     * Returns the text of the correct answer to the quiz question
     * @return the text of the correct answer
     */
    String correct();

    /**
     * Determines if the given guess is correct or not
     * @param guess the guess (made by a user) which is to be evaluated
     * @return true if the guess matches the solution or false otherwise
     */
    boolean evaluate(int guess);

    /**
     * Converts the multiple choice question into a multi-line string containing the question and all possible answers
     * @return a mulit-line string of text representing a multiple choice question
     */
    String toString();
}
