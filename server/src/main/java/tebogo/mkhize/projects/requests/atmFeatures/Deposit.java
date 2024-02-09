package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import java.util.HashMap;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.database.DbInterface;


public class Deposit extends ATMFeature {
    private boolean accountExists = false;
    private boolean invalidArguments = false;
    private boolean requestSucceeded = false;

    public Deposit(String account, String request, List<String> args) {
        super(account, request, args);
    }

    @Override
    public void execute(DbInterface database) {
        accountExists = database.verifyUser(getAccount().trim());

        if (! accountExists) {return;}

        int amount;
        try {
            amount = Integer.parseInt(getArgs().get(0).trim());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {  // use pipe (|) as separator.
            this.invalidArguments = true;
            return;
        }

        database.deposit(getAccount().trim(), amount);
        requestSucceeded = true;
    }

    @Override
    public Response generateResponse() {
        HashMap<String, Object> data = new HashMap<>();

        String outcome, message;
        if (! this.accountExists) {
            outcome = "AccountNotFoundError";
            message = "User with provided account (" + getAccount().trim() +
                ") doesn't exist.";

        } else if (this.invalidArguments) {
            outcome = "InvalidArgumentError";
            message = "Ensure deposit amount is included and consists of "+
                "digits only.";

        } else {
            outcome = "OK";
            message = "Deposited into account - " + getArgs().get(0).trim() +
                ".";
            data.put("deposit", getArgs().get(0).trim());
        }

        return new Response(getAccount(), outcome, message, data);
    }

    @Override
    public boolean getRequestOutcome() {
        return this.requestSucceeded;
    }
}
