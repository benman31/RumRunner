package QuizQuestion;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Constructs a randomly generated multiple quiz question from a random category of the the jService API
 * The generateQuestion method is used to generate a new category and construct a MultipleChoiceQuestion object
 * from within that category. Access methods are provided - getCategory returns a String describing the category
 * of the current question, while getQuestion returns a MultipleChoiceQuestion object.
 * Additionally a main method has been included for demonstration and testing purposes.
 */
public class QuizGenerator {

    private String category;
    private MultipleChoiceQuestion question;

    /**
     * Constructs an empty QuizGenerator
     */
    public QuizGenerator(){
        category = "";
        question = null;
    }

    /**
     * Retrieves a String containing the Category of the Quiz question
     * @return a string describing the category of question
     */
    public String getCategory(){
        return category;
    }

    /**
     * Retrieves a MultipleChoiceQuestion object
     * @return a MultipleChoiceQuestion object
     */
    public MultipleChoiceQuestion getQuestion(){
        return question;
    }

    /**
     * Generates a random question from within a randomly selected category from the jService API.
     * After retrieving a category from the CategoryGenerator class in JSON format, this method uses the
     * JeopardyCategory class to parse the JSON file, store the category in an instance variable and storing
     * the associated questions and answers in local List<String> variables (which are index aligned).
     * Because in rare cases the jService API has categories with duplicate answers to questions,
     * The lists of questions and answers are filtered using streams and checked to ensure the number of
     * distinct questions and corresponding answers are the same. If they are not the same it indicates
     * there were some repetitions, so a new category is retrieved.
     * A question at a random index is then chosen and recorded in a the MultipleChoiceQuestion object of the class's
     * instance field (called question) along with the corresponding correct answer.
     * To ensure the correct answer is not added to the question object in duplicate, the entry is removed from the
     * local answers List object. Three incorrect answers are then selected at random and added to the question object.
     * Again, to avoid adding duplicates the incorrect answer is removed from the local answers List Object as it is added
     * to the question object.
     */
    public void generateQuestion(){

        List<String> questions = null;
        List<String> answers = null;

        boolean done = false;
        while(!done){
            CategoryGenerator catGen =  new CategoryGenerator();
            catGen.connect();

            JeopardyCategory jCat = new JeopardyCategory(catGen.getRawResponse());

            category = jCat.getName();
            questions = jCat.getQuestions().stream()
                    .distinct()
                    .collect(Collectors.toList());
            answers = jCat.getAnswers().stream()
                    .distinct()
                    .collect(Collectors.toList());

            if(questions.size() == answers.size()){
                done = true;
            }
        }

        question = new MultipleChoiceQuestion();
        Random r = new Random();

        int questionIndex = r.nextInt(questions.size());

        question.setQuestionText(questions.get(questionIndex));
        question.addChoice(answers.get(questionIndex), true);

        answers.remove(questionIndex);
        int count = 0;
        int incorrectAnswerIndex;

        while(count < 3) {
            incorrectAnswerIndex = r.nextInt(answers.size());
            question.addChoice(answers.get(incorrectAnswerIndex), false);
            answers.remove(incorrectAnswerIndex);
            count++;
        }
    }

    /**
     * This main method is provided for testing and demonstration purposes only, it generates and prints 10 quiz questions
     * @param args not used here
     */
    public static void main(String[] args) {
        QuizGenerator generator = new QuizGenerator();

        for(int i = 0; i < 10; i++){
            generator.generateQuestion();
            System.out.println("CATEGORY: " + generator.getCategory() + "\n" + generator.getQuestion().toString());
        }

    }
}
