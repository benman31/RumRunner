import QuizQuestion.MultipleChoiceQuestion;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuizQuestion.MultipleChoiceQuestion class
 */
public class MultipleChoiceQuestionTests {

    private final String ROW_DIVIDER = "%%%%%%%%%%%%%%%%%%%%%%%%%\n";
    private final String DEFAULT_QUESTION = "This is a test question";
    private final int MAX_CHOICES = 4;
    private final String EMPTY_CHOICES = "0: null\n1: null\n2: null\n3: null\n";
    private final int DEFAULT_CORRECT = 0;
    private final int DEFAULT_INCORRECT = 10;
    private MultipleChoiceQuestion multiQ;

    @BeforeAll
    public static void init(){
        System.out.println("BeforeAll init() method called");
    }

    @BeforeEach
    public void constructQuestion(){
        multiQ = new MultipleChoiceQuestion();
    }

    @Test
    @DisplayName("Testing toString method with empty QuizQuestion.MultipleChoiceQuestion object")
    public void printTest(){
        assertEquals(ROW_DIVIDER + "\n" + ROW_DIVIDER + EMPTY_CHOICES + ROW_DIVIDER, multiQ.toString());
    }

    @Test
    @DisplayName("Testing setQuestionText")
    public void setQtest(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        assertEquals(ROW_DIVIDER + DEFAULT_QUESTION + "\n" + ROW_DIVIDER + EMPTY_CHOICES + ROW_DIVIDER, multiQ.toString());
    }

    @Test
    @DisplayName("Testing addChoice with 1 correct choice")
    public void addQchoices(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        multiQ.addChoice("" + DEFAULT_CORRECT , true);

        assertEquals("" + DEFAULT_CORRECT, multiQ.correct().substring(3));
    }

    @Test
    @DisplayName("Testing for randomized answers - should pass roughly 3 out of 4 times")
    public void testRandomizedAnswers(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        for(int i = 0; i < MAX_CHOICES; i++) {
            if (i == DEFAULT_CORRECT){
                multiQ.addChoice("" + i, true);
            }
            else{
                multiQ.addChoice("" + i , false);
            }
        }
        assertFalse(multiQ.evaluate(0));
    }

    @Test
    @DisplayName("Testing the method correct, with no correct answers")
    public void testNoCorrect(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        for(int i = 0; i < MAX_CHOICES; i++) {
            multiQ.addChoice("" + i , false);
        }
        assertEquals("", multiQ.correct());
    }

    @Test
    @DisplayName("Testing the method correct")
    public void testCorrect(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        for(int i = 0; i < MAX_CHOICES; i++) {
            if (i == DEFAULT_CORRECT){
                multiQ.addChoice("" + i, true);
            }
            else{
                multiQ.addChoice("" + i , false);
            }
        }
        assertEquals("" + DEFAULT_CORRECT, multiQ.correct().substring(3));
    }

    @Test
    @DisplayName("Testing correct guess with evaluate method, with first answer set to correct")
    public void testEvaluateCorrect(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        for(int i = 0; i < MAX_CHOICES; i++) {
            if (i == DEFAULT_CORRECT) {
                multiQ.addChoice("" + i, true);
            } else {
                multiQ.addChoice("" + i, false);
            }
        }
        //get index of correct answer in multiQ
        int correct = Integer.parseInt(multiQ.correct().substring(0,1));
        assertTrue(multiQ.evaluate(correct));
    }

    @Test
    @DisplayName("Testing incorrect guess with evaluate method")
    public void testEvaluateIncorrect(){
        multiQ.setQuestionText(DEFAULT_QUESTION);
        for(int i = 0; i < MAX_CHOICES; i++) {
            if (i == DEFAULT_CORRECT) {
                multiQ.addChoice("" + i, true);
            } else {
                multiQ.addChoice("" + i, false);
            }
        }
        //get index of correct answer in multiQ
        int correct = Integer.parseInt(multiQ.correct().substring(0,1));
        //get a guaranteed incorrect answer within bounds of possible answers
        int incorrect = (correct + 1) % 4;
        assertFalse(multiQ.evaluate(incorrect));
    }

    @Test
    @DisplayName("Testing evaluate method with numeric answer outside of answer bounds")
    public void testEvaluateOutofBoundsAnswer() {
        multiQ.setQuestionText(DEFAULT_QUESTION);
        for (int i = 0; i < MAX_CHOICES; i++) {
            if (i == DEFAULT_CORRECT) {
                multiQ.addChoice("" + i, true);
            } else {
                multiQ.addChoice("" + i, false);
            }
            assertFalse(multiQ.evaluate(DEFAULT_INCORRECT));
        }
    }

}
