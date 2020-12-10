package QuizQuestion;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Produces a raw JSON response String associated with a Jeopardy category
 * by connecting to a random category from the jService API and saving the raw JSON response in a string
 * this can then be accessed using the method getRawResponse so that other classes such as JeopardyCategory may process it.
 */
public class CategoryGenerator {

    private final String CATEGORY_URI = "http://jservice.io/api/category?id=";
    private final int CATEGORY_BOUND = 18410;

    private Random r;
    private String rawResponse;
    private int categoryID;

    /**
     * Constructs an empty CategoryGenerator with instance fields initialized
     */
    public CategoryGenerator(){
        rawResponse = "";
        r = new Random();
        categoryID = 0;
    }

    /**
     * Establishes a connection to a random jService category using HttpClient API
     * HttpRequest sends request to a URI associated with a random category within the jService API.
     * This is achieved by generating a random int within the bounds of jService's category ID numbers (from 1 to 18410 inclusive)
     * and appending this random ID to the base URI stored in the final int CATEGORY_URI.
     * The response will be in JSON format and is stored in the rawResponse instance field
     */
    public void connect(){
        try {
            HttpClient client = HttpClient.newBuilder().build();

            categoryID = r.nextInt(CATEGORY_BOUND) + 1;
            String uri = CATEGORY_URI + categoryID;
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));

            rawResponse = response.body();

        } catch(IOException f) {
            f.printStackTrace();
        }catch (InterruptedException e ) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accesses the raw JSON response String corresponding to a category of Jeopardy questions from the jService API
     * @return a String containing a raw JSON response
     */
    public String getRawResponse() {
        return rawResponse;
    }
}
