package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import java.util.HashMap;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.database.DbInterface;


public class Withdraw extends ATMFeature {
    private boolean accountExists = false;
    private boolean invalidArguments;
    private boolean requestSucceeded;
    private boolean invalidWithdrawal = false;
    private int balance;

    public Withdraw(String account, String request, List<String> args) {
        super(account, request, args);
    }

    @Override
    public void execute(DbInterface database) {

        accountExists = database.verifyUser(getAccount().trim());
        if (! accountExists) {return;}

        int amount;
        try {
            amount = Integer.parseInt(getArgs().get(0).trim());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {  // use pipe (|) as seperator.
            this.invalidArguments = true;
            return;
        }

        this.balance = database.balance(getAccount().trim());

        if (balance - amount < 0) {  // invalid withdrawal.
            this.invalidWithdrawal = true;
            return;
        }

        database.withdraw(getAccount().trim(), amount);
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
            message = "Ensure withdrawal amount is included and consists "+
                "of digits only.";

        } else if (this.invalidWithdrawal) {
            outcome = "InvalidWithdrawalError";
            message = "Invalid withdrawal amount (" + (getArgs().get(0).trim() +
                "), would make balance (" + this.balance +") negative.");

        } else {
            outcome = "OK";
            message = "Withdrew from account - " + getArgs().get(0).trim() +
                ".";
            data.put("withdraw", getArgs().get(0).trim());
        }

        return new Response(getAccount().trim(), outcome, message, data);
    }

    @Override
    public boolean getRequestOutcome() {
        return requestSucceeded;
    }
}
