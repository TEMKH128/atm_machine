package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import java.util.Random;
import java.util.HashMap;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.database.DbInterface;


public class Signup extends ATMFeature {
    boolean userExists = false;
    boolean signupSucceeded = false;
    boolean invalidArguments = false;
    String generatedPin, firstName, lastName, contact;

    public Signup(String account, String request, List<String> args) {
        super(account, request, args);
    }

    @Override
    public void execute(DbInterface database) {
        try {
            retrieveNameContact();
        } catch (IllegalArgumentException e) {
            this.invalidArguments = true;
            return;
        }

        this.userExists = database.verifyUser(firstName, lastName, contact);
        if (this.userExists) {return;}

        String account;
        do {
            account = generateNum(6);

        } while (database.verifyUser(account));  // unique account required.

        generatedPin = generateNum(4);

        database.signup(firstName, lastName, account, contact, generatedPin);
        this.signupSucceeded = true;
        setAccount(account);
    }

    private void retrieveNameContact() throws IllegalArgumentException {
        try {
            this.contact = this.getArgs().get(0).trim();
            // digit-only check
            Integer.parseInt(this.contact);


            this.firstName = this.getArgs().get(1).trim().toLowerCase();
            this.lastName = this.getArgs().get(2).trim().toLowerCase();

            if (this.contact.length() != 10) {
                throw new IllegalArgumentException("Contact Number doesn't " +
                    "contain 10 digits");
            }

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Invalid arguments provided");
        }
    }

    /**
     * Generates String representation of number
     * with specified number of digits.
     * @param numOfDigits number of digits number should have.
     * @return String representation of number.
     */
    private String generateNum(int numOfDigits) {
        StringBuilder accNum = new StringBuilder();
        Random random = new Random();

        while (accNum.length() < numOfDigits) {
            int digit = random.nextInt(10 - 0) + 0;  // max - min + min.
            accNum.append(digit);
        }

        return accNum.toString();
    }

    @Override
    public Response generateResponse() {
        HashMap<String, Object> data = new HashMap<>();

        String outcome, message;
        if (this.invalidArguments) {
            outcome = "InvalidArgumentError";
            message = "Please ensure correct first name, last name and " +
                "contact details (10 digits) are provided.";

        } else if (this.userExists) {
            outcome = "UserExistsError";
            message = "Name and contact details provided already belong to " +
                "a user within system.";

        } else {
            outcome = "OK";
            message = "Sign up successful. \n\nPlease safeguard following: " +
                "\n\t1. account number (" + getAccount() + ")\n\t2. pin (" +
                this.generatedPin + ")";
        }

        return new Response(getAccount(), outcome, message, data);
    }

    @Override
    public boolean getRequestOutcome() {
        return this.signupSucceeded;
    }
}
