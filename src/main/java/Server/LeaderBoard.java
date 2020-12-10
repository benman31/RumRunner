package Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Leaderboard is a thread-safe class which keeps track of multiple users' quiz score streaks in real time.
 * All methods utilize a basic lock-try-finally-unlock idiom to avoid race conditions and provide thread safety.
 */
public class LeaderBoard implements Scorable{

    private final String LEADERBOARD_HEADER = "************************\n* Top 3 Active Streaks *\n************************\n";
    private final String COLUMN_SPACER = ":   ";
    private Map<String, Integer> leaderBoard;
    private Lock leaderBoardLock;

    /**
     * Constructs an empty LeaderBoard
     */
    public LeaderBoard(){
        leaderBoard = new HashMap<>();
        leaderBoardLock = new ReentrantLock();
    }

    /**
     * Updates the correct streak of correct answers for a user by taking the user name and their current streak
     * @param name   The user whose streak is to be updated
     * @param streak The current number of correct answers in a row
     */
    public void update(String name, int streak) {
        leaderBoardLock.lock();
        try{
            leaderBoard.put(name, streak);
        }
        finally {
            leaderBoardLock.unlock();
        }
    }

    /**
     * Removes a user from the LeaderBoard
     * @param name the user to be removed
     */
    public void delete(String name) {
        leaderBoardLock.lock();
        try{
            leaderBoard.remove(name);
        }
        finally {
            leaderBoardLock.unlock();
        }
    }

    /**
     * Gets the active streak for a given user
     * @param name name of the user whose streak is to be retrieved. If no such user exists 0 is returned
     * @return the current active streak of a given user or 0 if the user does not exist
     */
    public int get(String name) {
        int result = 0;
        leaderBoardLock.lock();
        try{
            result = leaderBoard.getOrDefault(name, 0);
        }
        finally {
            leaderBoardLock.unlock();
        }
        return result;
    }

    /**
     * Convert the Leaderboard into a snazzy String containing the Top 3 users with their active streaks
     * this is done by creating a stream from the keySet of the leaderBoard Map and sorting them into a list
     * such that the index of each name in the list correspond to the top 3 players by streak in descending order.
     * @return a text version of the top 3 streaks
     */
    public String prettyPrintTop3() {
        String result = LEADERBOARD_HEADER;
        leaderBoardLock.lock();
        try {
            Set<String> keySet = leaderBoard.keySet();
            List<String> top3 = keySet.stream()
                    .sorted((n, m) -> leaderBoard.get(m) - leaderBoard.get(n)) //sort in descending order
                    .limit(3)
                    .collect(Collectors.toList());
            for (int i = 0; i < top3.size(); i++){
                result = result + top3.get(i) + COLUMN_SPACER + leaderBoard.get(top3.get(i)) + "\n";
            }
        }
        finally {
            leaderBoardLock.unlock();
        }
        return result;
    }

    /**
     * Retrieves the total number of users currently store in the LeaderBoard
     * @return the number of users saved in the LeaderBoard
     */
    public int size(){
        return leaderBoard.size();
    }
}
