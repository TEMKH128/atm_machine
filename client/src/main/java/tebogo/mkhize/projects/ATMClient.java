package tebogo.mkhize.projects;

import java.io.*;
import java.util.*;
import java.net.Socket;


public class ATMClient {
    private String accNum;
    private final static int PORT_NUM = 5000;
    private final static String IP_ADDR = "localhost";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean isLoggedIn = false;
    private boolean isLoggedSignedIn = false;
    private boolean isClientConnected = false;

    public ATMClient(int port, String ipAddr) {
        serviceEntranceDisplay();
        connectToATM(port, ipAddr);
    }

    /**
     * Establishes a connection to a running server,
     * which shall serve as the atm in the simulation.
     * @param port port number server is accepting connections.
     * @param ipAddr ip address of machine server is running on.
     */
    private void connectToATM(int port, String ipAddr) {
        try {
            this.socket = new Socket(ipAddr, port);
            this.isClientConnected = true;

            this.reader = new BufferedReader(new InputStreamReader(
                this.socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(
                this.socket.getOutputStream()));

        } catch (IOException e) {
            System.out.println("\nATM Server Down...Please Try Again Later.");
            exitProgram();
        }
    }

    private void serviceEntranceDisplay() {
        System.out.println("*".repeat(50) + "ATM MACHINE SIMULATION" +
            "*".repeat(50));
    }

    private void run() {
        loginSignup();
        loginPostSignup();

        new Thread(this:: sendToServer).start();
        new Thread(this:: receiveFromServer).start();
    }

    /**
     * Prompts user to login/signup for ATM Simulation.
     */
    private void loginSignup() {
        while (! this.isLoggedSignedIn) {
            String message = "\nPlease Login/Signup, Select Number " +
                "Reflecting Choice:\n\t1. Login\n\t2. Signup\n\t3. Exit";

            Integer[] validOptions = new Integer[] {1, 2, 3};
            int choice = promptClient(message, validOptions);

            boolean requestSent = false;
            if (choice == 1) {
                requestSent = loginUser();

            } else if (choice == 2){
                requestSent =signUpUser();

            } else if (choice == 3) {
                exitProgram();
            }

            if (requestSent) {retrieveLoginSignupOutcome();}
        }
    }

    /**
     * Prompts user to login post signing up for ATM Simulation service.
     */
    private void loginPostSignup() {
        while (! this.isLoggedIn) {
            String message = "\nPlease login with provided details, Select " +
                "Number Reflecting Choice:\n\t1. Login\n\t2. Exit";

            Integer[] validOptions = new Integer[] {1, 2};
            int choice = promptClient(message, validOptions);

            boolean requestSent = false;
            if (choice == 1) {
                requestSent = loginUser();

            } else if (choice == 2){
                exitProgram();
            }

            if (requestSent) {retrieveLoginSignupOutcome();}
        }
    }

    private void exitProgram() {
        System.out.println("Exiting ATM Machine Simulation...Thank You " +
            "For Using Service.\n");
        System.out.println("*".repeat(122));
        System.exit(0);
    }

    /**
     * Sends client requests to connected ATM Server.
     */
    private void sendToServer() {
        String[] choices = new String[] {"balance", "deposit", "transfer",
            "withdraw"};

        while (true) {
            String message = "\nPlease Enter Request, Select Number " +
                "Reflecting Choice:\n\t1. Balance \n\t2. Deposit \n\t3. " +
                "Transfer \n\t4. Withdraw\n\t5. Exit";

            int choice = promptClient(message, new Integer[] {1, 2, 3, 4, 5});

            if (choice == 5) {exitProgram();}

            String request = choices[choice - 1];
            Scanner verifier = new Scanner(System.in);
            List<String> args;

            boolean continueTrans = true;
            do {
                args = new ArrayList<>();
                StringBuilder verifyMessage = new StringBuilder(
                    "Please Verify Selections:");

                if (request.equalsIgnoreCase("transfer")) {
                    String transferAcc = retrieveSelection("transferaccount");

                    if (transferAcc.equalsIgnoreCase("exit")) {
                        continueTrans = false;
                        break;
                    }

                    args.add(transferAcc);
                    verifyMessage.append("\n\t* transfer account (").append(
                        transferAcc.trim()).append(")");

                } else if (request.equalsIgnoreCase("balance")) {
                    verifyMessage.append("\n\t* No Selections Required");
                }

                if (request.equalsIgnoreCase("transfer") ||
                    request.equalsIgnoreCase("deposit") ||
                    request.equalsIgnoreCase("withdraw")) {

                    String amount = retrieveSelection("amount");

                    if (amount.equalsIgnoreCase("exit")) {
                        continueTrans = false;
                        break;
                    }

                    args.add(amount);
                    verifyMessage.append("\n\t* transaction amount (").append(
                        amount.trim()).append(")");
                }

                verifyMessage.append("\n\nEnter y to continue:");

                System.out.println(verifyMessage);

            } while (! verifier.nextLine().trim().equalsIgnoreCase("y"));

            if (continueTrans) {
                Request reqObject = new Request(this.accNum, request, args);

                sendMessage(reqObject.createJSONString());

                // Paused for response time.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Takes string representation of request and sends it to the Server.
     * @param request string representation of json request.
     */
    private void sendMessage(String request) {
        try {
            this.writer.write(request);
            this.writer.newLine();
            this.writer.flush();
        }
        catch (IOException ignored) {}
    }

    /**
     * Receives responses from connected ATM Server.
     */
    private void receiveFromServer() {
        try {

            while (true) {
                String response = this.reader.readLine();

                if (response == null) {
                    System.out.println("\nATM Server Down...Please Try " +
                        "Again Later.");
                    exitProgram();
                }

                ResponseDTO responseObj = ResponseDTO.jsonStringToRequest(
                    response);

                System.out.println("\n* Outcome (Account - " +
                    responseObj.getAccount() + "): " +
                    responseObj.getMessage());
            }
        } catch (IOException ignored) {}
    }

    /**
     * Retrieves the outcome of login/signup requests from the connected
     * server and modifies related values (E.g. isLoggedIn).
     */
    private void retrieveLoginSignupOutcome() {
        try {
            String response = this.reader.readLine();

            if (response == null) {
                System.out.println("\nATM Server Down...Please Try Again "+
                    "Later");
                exitProgram();
            }

            ResponseDTO responseObj = ResponseDTO.jsonStringToRequest(
                response);

            System.out.println("* Outcome: " + responseObj.getMessage());

            if (responseObj.getMessage().equalsIgnoreCase(
                "login successful.")) {
                this.isLoggedSignedIn = true;
                this.isLoggedIn = true;
                this.accNum = responseObj.getAccount();

            } else if (responseObj.getOutcome().equalsIgnoreCase(
                "OK")) {  // this stage means signup's OK.
                this.isLoggedSignedIn = true;
            }
        } catch (IOException ignored) {}
    }

    /**
     * Ensures user logs in to their account,
     * If an account doesn't exist, user is prompted to create one.
     */
    private int promptClient(String message, Integer[] validOptions) {
        Scanner promptClient = new Scanner(System.in);

        int choice = 0;
        while (true) {
            System.out.println(message);

            try {
                choice = Integer.parseInt(promptClient.nextLine().trim());

                if (Arrays.asList(validOptions).contains(choice)) {
                    break;
                }
            } catch (NumberFormatException ignored) {}

            System.out.println("* Outcome: Please select a valid option.");
        }

        return choice;
    }

    /**
     * Prompts user for necessary details for
     * logging up and sends login request to server.
     * @return boolean representing whether login request sent.
     */
    private boolean loginUser() {
        String account;
        String pin;
        Scanner verifier = new Scanner(System.in);

        do {
            account = retrieveSelection("account");
            if (account.equalsIgnoreCase("exit")) {return false;}

            pin = retrieveSelection("pin");
            if (pin.equalsIgnoreCase("exit")) {return false;}

            System.out.println("Please Verify Selections:\n\t* account " + "("
                + account.trim() + ")" + "\n\t* pin (" + pin.trim() +
                ")\n\nEnter y to continue:");

        } while (! verifier.nextLine().trim().equalsIgnoreCase("y"));

        String[] args = new String[] {account, pin};

        Request reqObject = new Request(this.accNum, "login", List.of(args));
        sendMessage(reqObject.createJSONString());
        return true;
    }


    /**
     * propmts user for various selections and returns user's response.
     * @param selection type of selection to prompt for.
     * @return user's response.
     */
    private String retrieveSelection(String selection) {
        Scanner promptClient = new Scanner(System.in);

        String message;
        switch (selection.toLowerCase()) {
            case "account":
                message = "Please Provide your 6 Digit Account Number, "+
                    "Enter \"exit\" to Stop Transaction:";
                break;

            case "pin":
                message = "Please Provide your 4 Digit Pin, Enter \"exit\" " +
                    "to Stop Transaction:";
                break;

            case "contact":
                message = "Please Provide your 10 Digit Contact Details, " +
                    "Enter \"exit\" to Stop Transaction:";
                break;

            case "firstname":
                message = "Please Provide your First Name, Enter \"exit\" " +
                    "to Stop Transaction:";
                break;

            case "lastname":
                message = "Please Provide your Last Name, Enter \"exit\" " +
                    "to Stop Transaction:";
                break;

            case "transferaccount":
                message = "Please Provide Receivers 6 Digit Account Number," +
                    " Enter \"exit\" to Stop Transaction:";
                break;

            default:
                message = "Please Provide Transaction Amount, Enter "+
                    "\"exit\" to Stop Transaction:";
        }

        System.out.println(message);
        return promptClient.nextLine();
    }

    /**
     * Prompts user for necessary details for
     * signing up and sends signup request to server.
     * @return boolean representing whether signup request sent.
     */
    private boolean signUpUser() {
        String contact, firstName, lastName;
        Scanner verifier = new Scanner(System.in);

        do {
            contact = retrieveSelection("contact");
            if (contact.equalsIgnoreCase("exit")) {return false;}

            firstName = retrieveSelection("firstname");
            if (firstName.equalsIgnoreCase("exit")) {return false;}

            lastName = retrieveSelection("lastname");
            if (lastName.equalsIgnoreCase("exit")) {return false;}

            System.out.println("Please Verify Selections: \n\t* contact " +
                "(" + contact.trim() + ")" + "\n\t* First Name (" +
                firstName.trim() + ") " + "\n\t* Last Name (" +
                lastName.trim() + ")\n\nEnter y to continue:");

        } while (! verifier.nextLine().trim().equalsIgnoreCase("y"));

        String[] args = new String[] {contact, firstName, lastName};

        Request reqObject = new Request(this.accNum, "signup", List.of(args));
        sendMessage(reqObject.createJSONString());
        return true;
    }

    public boolean getConnectionStatus() {
        return this.isClientConnected;
    }

    public static void main(String[] args) {
        ATMClient atmUser = new ATMClient(ATMClient.PORT_NUM,
            ATMClient.IP_ADDR);

        if (atmUser.getConnectionStatus()) {
            atmUser.run();
        }
    }
}