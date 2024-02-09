package tebogo.mkhize.projects.database;


public interface DbInterface {
    /**
     * Verifies user when provided their account number (Exists in database).
     * E.g. Usage: Verifying account to transfer money to exists.
     * @param accNum user's unique account number.
     * @return boolean reflecting whether exists or not.
     */
    boolean verifyUser(String accNum);

    /**
     * Verifies user when provided their name and
     * contact number (Exists in database).
     * E.g. Usage: Verifying whether user already exists before signing up.
     * @param firstName user's first name.
     * @param lastName user's last name.
     * @param contact user's contact number.
     * @return boolean reflecting whether exists or not.
     */
    boolean verifyUser(String firstName, String lastName, String contact);

    /**
     * Logs into account by verifying that account exists and
     * correlates with the provided pin within the database.
     * @param accNum user's unique account number.
     * @param pin user's pin to log into account.
     * @return JSONObject reflecting outcome and data.
     */
    boolean Login(String accNum, String pin);

    /**
     * Signs up user by creating a record within the database provided that
     * generated pin is unique and creates required default values
     * @param firstName user's first name.
     * @param lastName user's last name.
     * @param contact user's contact number.
     * @param generatedPin user's generated pin.
     */
    void signup(String firstName, String lastName, String account, String contact, String generatedPin);

    /**
     * Retrieves account balance within database for provided account number.
     * @param accNum user's unique account number.
     * @return account balance.
     */
    int balance(String accNum);

    /**
     * Deposits money into database for provided account number.
     * @param accNum user's unique account number.
     * @param amount amount to be deposited.
     */
    void deposit(String accNum, int amount);

    /**
     * Withdraws money from user's account.
     * @param accNum user's unique account number.
     * @param amount amount to be withdrawn.
     */
    void withdraw(String accNum, int amount);

    public boolean isInitialConnectStatus();
}
