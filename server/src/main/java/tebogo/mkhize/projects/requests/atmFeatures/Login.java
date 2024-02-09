package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import java.util.HashMap;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.database.DbInterface;


public class Login extends ATMFeature {
    private String account, pin;
    private boolean loginSucceeded = false;
    private boolean invalidArguments = false;

    public Login(String account, String request, List<String> args) {
        super(account, request, args);
    }

    @Override
    public void execute(DbInterface database) {
        try {
            retrieveAccountPin();
        } catch (IllegalArgumentException e) {
            this.invalidArguments = true;
            return;
        }

        this.loginSucceeded = database.Login(this.account, this.pin);

        if (this.loginSucceeded) {setAccount(this.account);}
    }

    /**
     * Attempts to retrieve account and pin within provided arguments.
     * @throws IllegalArgumentException
     */
    private void retrieveAccountPin() throws IllegalArgumentException {
        try {
            this.account = this.getArgs().get(0).trim();
            this.pin = this.getArgs().get(1).trim();

            if (this.account.length() != 6 || ! numCharOnly(this.account) ||
                this.pin.length() != 4 || ! numCharOnly(this.pin)) {

                throw new IllegalArgumentException(
                    "Account/Pin arent 6/4 digits.");
            }

        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Not enough arguments " +
                "provided");
        }
    }

    /**
     * Ensures that provided string contains digits only.
     * @param numString string to be checked for digits.
     * @return boolean representing result of check.
     */
    private boolean numCharOnly(String numString) {
        for (char character: numString.toCharArray()) {
            if (! Character.isDigit(character)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Response generateResponse() {
        HashMap<String, Object> data = new HashMap<>();

        String outcome, message;
        if (this.invalidArguments) {
            outcome = "InvalidArgumentError";
            message = "Please ensure correct account number (6 digits) and " +
            "pin (4 digits) are provided.";

        } else if (! this.loginSucceeded) {
            outcome = "UserNotFoundError";
            message = "No user corresponds with provided account number " +
                    "and pin. Please try again.";

        } else {
            outcome = "OK";
            message = "Login Successful.";
        }

        return new Response(getAccount(), outcome, message, data);
    }

    @Override
    public boolean getRequestOutcome() {
        return this.loginSucceeded;
    }
}
