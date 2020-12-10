import Server.LeaderBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Server.LeaderBoard class
 */
public class LeaderBoardTests {

    private  final String LEADERBOARD_HEADER = "************************\n* Top 3 Active Streaks *\n************************\n";
    private final String COLUMN_SPACER = ":   ";
    private final String DEFAULT_USER = "Ben";
    private static final int DEFAULT_STREAK = 10;
    private final int DEFAULT_SIZE = 5;

    private final int THREADS = 100;
    private final int REPETITIONS = 1000;



    private static LeaderBoard leaderBoard;

    /**
     * Leaderboard 1000 times on a thread
     */
    public static void update1000(){
        try{
            for(int i = 0; i < 1000 &&!Thread.interrupted(); i++){
                leaderBoard.update(""+i, DEFAULT_STREAK);
                Thread.sleep(1);
            }
        }
        catch(InterruptedException e){}
    }

    /**
     * delete a player 1000 times on a thread
     */
    public static void delete1000(){
        try{
            for(int i = 0; i < 1000 &&!Thread.interrupted(); i++){
                leaderBoard.delete(""+i);
                Thread.sleep(5);
            }
        }
        catch(InterruptedException e){}
    }

    /**
     * print 1000 times on a thread
     */
    public static void print1000(){
        try{
            for(int i = 0; i < 1000 &&!Thread.interrupted(); i++){
                leaderBoard.prettyPrintTop3();
                Thread.sleep(1);
            }
        }
        catch(InterruptedException e){}
    }

   @BeforeEach
   public void init(){
        leaderBoard = new LeaderBoard();
   }

   @Test
   @DisplayName("Perform a single update")
   public void singleUpdateTest(){
        leaderBoard.update(DEFAULT_USER, DEFAULT_STREAK);
        assertEquals(1, leaderBoard.size());
   }

   @Test
   @DisplayName("Perform a single delete")
   public void singleDeleteTest(){
        leaderBoard.update(DEFAULT_USER, DEFAULT_STREAK);
        leaderBoard.delete(DEFAULT_USER);
        assertEquals(0, leaderBoard.size());
   }

   @Test
   @DisplayName("Attempt to delete non-existant user")
   public void deleteNonExistantUserTest(){
        leaderBoard.delete(DEFAULT_USER);
        assertEquals(0, leaderBoard.size());
   }

   @Test
   @DisplayName("Get a player streak once")
   public void getSingleStreakTest(){
        leaderBoard.update(DEFAULT_USER, DEFAULT_STREAK);
        assertEquals(DEFAULT_STREAK, leaderBoard.get(DEFAULT_USER));
   }

   @Test
   @DisplayName("Attempt to get non-existant player streak")
   public void getNonExistantStreak(){
        assertEquals(0, leaderBoard.get(DEFAULT_USER));
   }

   @Test
   @DisplayName("Print empty leaderboard")
   public void printEmptyLeaderBoard(){
        assertEquals(LEADERBOARD_HEADER, leaderBoard.prettyPrintTop3());
   }

   @Test
   @DisplayName("Correctly print top 3 of 5 players")
   public void printTop3Of5(){
        for(int i = 0; i < DEFAULT_SIZE; i++){
            leaderBoard.update("" + i, i);
        }
        assertEquals(LEADERBOARD_HEADER + "4" + COLUMN_SPACER + "4\n3"
                + COLUMN_SPACER + "3\n2" + COLUMN_SPACER + "2\n", leaderBoard.prettyPrintTop3() );
   }

   @Test
   @DisplayName("Repeated updates on single thread")
   public void singleThreadUpdate1000Test(){
        Thread thread = new Thread(LeaderBoardTests::update1000);
        thread.start();
       try {
           thread.join();
       }catch(InterruptedException e) {
           e.printStackTrace();
       }
       assertEquals(REPETITIONS, leaderBoard.size());
       thread.interrupt();
   }

   @Test
   @DisplayName("Repeated updates and deletes on multiple threads")
   public void multiUpdateDeleteTest(){

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREADS; i++){
            Thread thread1 = new Thread(LeaderBoardTests::update1000);
            Thread thread2 = new Thread(LeaderBoardTests::delete1000);
            thread1.start();
            thread2.start();
            threads.add(thread1);
            threads.add(thread2);
        }
        try{
            for(Thread thread: threads){
                thread.join();
            }
        }
        catch (InterruptedException e){}
       for(Thread thread: threads){
           thread.interrupt();
       }
       assertEquals(0, leaderBoard.size());
   }

    @Test
    @DisplayName("Perform get method multiple times with multiple threads")
    public void multiUpdateGetTest(){
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREADS; i++){
            Thread thread1 = new Thread(LeaderBoardTests::update1000);
            thread1.start();
            threads.add(thread1);
        }
        try{
            for(Thread thread: threads){
                thread.join();
            }
        }
        catch (InterruptedException e){}
        for(Thread thread: threads){
            thread.interrupt();
        }
        for(int i = 0; i < REPETITIONS; i++){
            assertEquals(DEFAULT_STREAK, leaderBoard.get("" + i));
        }
    }

    @Test
    @DisplayName("Perform multiple prints on multiple threads")
    public void multiPrintTest(){
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREADS; i++){
            Thread thread1 = new Thread(LeaderBoardTests::update1000);
            Thread thread2 = new Thread(LeaderBoardTests::print1000);
            thread1.start();
            thread2.start();
            threads.add(thread1);
            threads.add(thread2);
        }
        try{
            for(Thread thread: threads){
                thread.join();
            }
        }
        catch (InterruptedException e){}
        for(Thread thread: threads){
            thread.interrupt();
        }
        assertEquals(LEADERBOARD_HEADER + "0" + COLUMN_SPACER + "10\n1"
                + COLUMN_SPACER + "10\n2" + COLUMN_SPACER + "10\n", leaderBoard.prettyPrintTop3() );
    }

}
