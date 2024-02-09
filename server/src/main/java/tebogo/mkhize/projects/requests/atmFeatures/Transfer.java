package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import java.util.HashMap;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.database.DbInterface;


public class Transfer extends ATMFeature {
    private int balance;
    private boolean ownAccount = false;
    private boolean accountExists = false;
    private boolean invalidTransfer = false;
    private boolean invalidArguments = false;
    private boolean requestSucceeded = false;
    private boolean transferAccExists = false;


    public Transfer(String account, String request, List<String> args) {
        super(account, request, args);
    }

    @Override
    public void execute(DbInterface database) {
        this.accountExists = database.verifyUser(getAccount().trim());
        if (! accountExists) {return;}

        int amount;
        String transferAcc;
        try {
            transferAcc = getArgs().get(0).trim();

            if (! numCharOnly(transferAcc) || transferAcc.length() != 6) {
                throw new IllegalArgumentException(
                        "Account/Pin arent 6/4 digits.");
            }

            amount = Integer.parseInt(getArgs().get(1).trim());
            
            this.transferAccExists = database.verifyUser(transferAcc);
            if (! transferAccExists) {return;}

            // transfer into own account.
            if (transferAcc.equalsIgnoreCase(getAccount())) {
                this.ownAccount = true;
                return;
            }

        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {  // use pipe (|) as separator.
            this.invalidArguments = true;
            return;
        }

        this.balance = database.balance(getAccount().trim());

        if (balance - amount < 0) {  // invalid withdrawal.
            this.invalidTransfer = true;
            return;
        }

        database.withdraw(getAccount().trim(), amount);
        database.deposit(transferAcc, amount);
        requestSucceeded = true;

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
        if (! this.accountExists) {
            outcome = "AccountNotFoundError";
            message = "User with provided account (" + getAccount().trim() +
                ") doesn't exist.";

        } else if (this.invalidArguments) {
            outcome = "InvalidArgumentError";
            message = "Ensure transfer account (6 digits only) and amount " +
                "(digits only) are included.";

        } else if (! this.transferAccExists) {
            outcome = "AccountNotFoundError";
            message = "Provided transfer account (" + getArgs().get(0).trim() +
                ") doesn't exist.";

        } else if (this.ownAccount) {
            outcome = "OwnAccountError";
            message = "Cannot transfer into own account (" +
                getArgs().get(0).trim() + ").";

        } else if (this.invalidTransfer) {
            outcome = "InvalidWithdrawalError";
            message = "Invalid transfer amount (" +
                (getArgs().get(1).trim() + "), would make balance (" +
                this.balance +") negative.");

        } else {
            outcome = "OK";
            message = "Transferred into account (" + getArgs().get(0).trim() +
                ") - " + getArgs().get(1).trim() + ".";

            data.put("transferAcc", getArgs().get(0).trim());
            data.put("amount", getArgs().get(1).trim());
        }

        return new Response(getAccount().trim(), outcome, message, data);
    }

    @Override
    public boolean getRequestOutcome() {
        return this.requestSucceeded;
    }
}
