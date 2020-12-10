package Server;

/**
 * An interface for a leaderboard which keeps score of player Streaks in a quiz game
 */
public interface Scorable {

    /**
     * Updates the correct streak of correct answers for a user by taking the user name and their current streak
     * @param name The user whose streak is to be updated
     * @param streak The current number of correct answers in a row
     */
    void update(String name, int streak);

    /**
     * Removes a user from the Server.LeaderBoard
     * @param name the user to be removed
     */
    void delete(String name);

    /**
     * Gets the active streak for a given user
     * @param name name of the user whose streak is to be retrieved. If no such user exists 0 is returned
     * @return the current active streak of a given user or 0 if the user does not exist
     */
    int get(String name);

    /**
     * Convert the Leaderboard into a snazzy String containing the Top 3 users with their active streaks
     * @return a text version of the top 3 streaks
     */
    String prettyPrintTop3();

}
