package tebogo.mkhize.projects.database;

import java.sql.*;

public class DbHandler implements DbInterface {
    private boolean initialConnectStatus = false;
    public static final String dbUrl = "jdbc:sqlite:server/src/main/resources/atm_database.db";

    public DbHandler() {
        makeConnection();
    }

    @Override
    public boolean isInitialConnectStatus() {
        return initialConnectStatus;
    }

    /**
     * Establishes connection to database using jdbc, uses DriverManager
     * to get connection object. try-with-resources statement is used for
     * automatic resource mngmt, resources that implement AutoCloseable
     * (such as Connection) are auto. closed at the end of the try block,
     * prevents resource leaks.
     */
    private void makeConnection() {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
            createTable(connection);
            initialConnectStatus = true;

        } catch (SQLException e) {
            System.out.println("Error in Establishing Connection to "+
                "Database.");
        }
    }

    /**
     * Creates Table.
     * statement object is ued to execute SQL statements against a database
     * Statement [SQL statements without parameters], CallableStatement
     * PreparedStatement [SQL statements with parameters],
     * Parameters - E.g. 'SELECT * FROM customers WHERE id = ?' - ? is
     * placeholder for id to be provided.
     * @param connection
     */
    private void createTable(Connection connection) {
        try (final Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS accounts(" +
                "account TEXT, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "contact TEXT, " +
                "pin INTEGER, " +
                "balance INTEGER, " +
                "PRIMARY KEY(account))";  // primary key ensures that each value is unique.

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
        }
    }

    @Override
    public boolean verifyUser(String accNum) {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
           String query = "SELECT * FROM accounts WHERE account = ?";
           PreparedStatement statement = connection.prepareStatement(query);
           statement.setString(1, accNum);

           ResultSet resultSet = statement.executeQuery();

           // User exists (record retrieved)?
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
            return false;
        }
    }

    @Override
    public boolean verifyUser(String firstName, String lastName, String contact) {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "SELECT * FROM accounts WHERE firstName = ? AND lastName = ? AND contact = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, contact);

            ResultSet resultSet = statement.executeQuery();

            // User exists (record retrieved)?
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
            return false;
        }
    }

    @Override
    public boolean Login(String accNum, String pin) {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "SELECT * FROM accounts WHERE account = ? AND pin = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, accNum);
            statement.setString(2, pin);

            ResultSet resultSet = statement.executeQuery();

            // User exists (record retrieved, logged in Successfully)?
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
            return false;
        }
    }

    @Override
    public void signup(String firstName, String lastName, String account, String contact, String generatedPin) {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "INSERT INTO accounts (account, firstName, lastName, contact, pin, balance) " +
                "VALUES (?, ?, ?, ?, ?, 0)";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, account);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, contact);
            statement.setString(5, generatedPin);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
        }
    }

    @Override
    public int balance(String accNum) {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "SELECT balance FROM accounts where account = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accNum);

            int balance = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                // next() moves to next row that meets criteria (Can use loop to loop through all rows).
                // Can retrieve data from current row using getter method (getInt/getString/etc) specifying column name/index.
                if (resultSet.next()) {
                    balance = resultSet.getInt("balance");
                }
            }
            return balance;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
            return -1000;  // if withdrawal facility - throw error.
        }
    }

    @Override
    public void deposit(String accNum, int amount) {
        try (final Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "UPDATE accounts SET balance = balance + ? WHERE account = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, amount);  // first ?
            statement.setString(2, accNum);  //  second ?

            int rowsUpdated = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
        }
    }

    @Override
    public void withdraw(String accNum, int amount) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "UPDATE accounts SET balance = balance - ? WHERE account = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, amount);
            statement.setString(2, accNum);

            int rowsUpdated = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Establishing Connection " +
                "to Database.");
        }
    }
}
