package tebogo.mkhize.projects.requests.atmFeatures;

import java.util.List;
import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.requests.RequestDTO;
import tebogo.mkhize.projects.database.DbInterface;


public abstract class ATMFeature {
    private String account;
    private String request;
    private List<String> args;

    public ATMFeature (String account, String request, List<String> args) {
        this.account = account;
        this.request = request;
        this.args = args;
    }

    public abstract void execute (DbInterface database);

    /**
     * Generates response instance reflecting outcome of request
     * to be sent back to client.
     * @return Response instance reflecting outcome.
     */
    public abstract Response generateResponse();
    public abstract boolean getRequestOutcome();

    public static ATMFeature createRequestFeature(RequestDTO request) {
        switch (request.getRequest().toLowerCase()) {
            case "balance":
                return new Balance(request.getAccount(), request.getRequest(),
                        request.getArgs());

            case "deposit":
                return new Deposit(request.getAccount(), request.getRequest(),
                        request.getArgs());

            case "login":
                return new Login(request.getAccount(), request.getRequest(),
                        request.getArgs());

            case "signup":
                return new Signup(request.getAccount(), request.getRequest(),
                        request.getArgs());

            case "transfer":
                return new Transfer(request.getAccount(),
                        request.getRequest(), request.getArgs());

            case "withdraw":
                return new Withdraw(request.getAccount(),
                        request.getRequest(), request.getArgs());

            default:  // unrecognised request/feature.
                throw new IllegalArgumentException("Unsupported request: ");
        }
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
