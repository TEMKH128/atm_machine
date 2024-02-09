package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import java.util.HashMap;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.database.DbInterface;


public class Balance extends ATMFeature {
    int balance;
    private boolean accountExists;
    private boolean requestSucceeded = false;

    public Balance(String account, String request, List<String> args) {
        super(account, request, args);
    }

    @Override
    public void execute(DbInterface database) {
        accountExists = database.verifyUser(getAccount().trim());

        if (! accountExists) {return;}

        balance = database.balance(getAccount().trim());
        if (balance >= 0) {
            requestSucceeded = true;
        }
    }

    @Override
    public Response generateResponse() {
        HashMap<String, Object> data = new HashMap<>();

        String outcome, message;
        if (! accountExists) {
            outcome = "AccountNotFoundError";
            message = "User with provided account (" + getAccount().trim() +
                ") doesn't exist.";

        } else {
            outcome = "OK";
            message = "Balance - " + balance + ".";
        }

        data.put("balance", balance);

        return new Response(getAccount(), outcome, message, data);
    }

    @Override
    public boolean getRequestOutcome() {
        return this.requestSucceeded;
    }
}
