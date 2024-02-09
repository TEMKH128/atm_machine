package tebogo.mkhize.projects.requests;

import java.util.HashMap;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;


public class Response {
    private String account;
    private String outcome;
    private String message;
    private HashMap<String, Object> data;
    private final ObjectMapper mapper = new ObjectMapper();

    public Response(String account, String outcome, String message,
        HashMap<String, Object> data) {

        this.account = account;
        this.outcome = outcome;
        this.message = message;
        this.data = data;
    }

    /**
     * Generates JSONObject using passed object and
     * returns JSONObject as a string.
     * @return
     */
    public String responseMsgToJsonString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            System.out.println("error occurred in creating JSON object " +
                "string");
            return null;
        }
    }

    /**
     * Generates response input in the event a client's request isn't
     * supported by ATM Server.
     * @param request client's request.
     * @return HashMap representing input for response to be sent to client.
     */
    public static HashMap<String, Object> unsupportedRequest(String request) {
        HashMap<String, Object> responseInput = new HashMap<>();

        responseInput.put("outcome", "IllegalRequestError");
        responseInput.put("message", "Provided request (" + request + ") " +
            "is unsupported. Enter \"Help\" for valid requests.");

        responseInput.put("data", new HashMap<>());

        return responseInput;
    }

    /**
     * Generates response input in the event client hasn't logged in before
     * issuing requests requiring you to be logged in.
     * supported by ATM Server.
     * @return HashMap representing input for response to be sent to client.
     */
    public static HashMap<String, Object> loginSignupResponse() {
        HashMap<String, Object> responseInput = new HashMap<>();

        responseInput.put("outcome", "AccessError");
        responseInput.put("message", "Please Login (Provide Account Number " +
            " and Account Pin) or Signup (Provide Name and Contact Number)");

        responseInput.put("data", new HashMap<>());

        return responseInput;
    }

    /**
     * Generates response input in the event client attempts to log in when
     * already logged in.
     * @return HashMap representing input for response to be sent to client.
     */
    public static HashMap<String, Object> alreadyLoggedInResponse() {
        HashMap<String, Object> responseInput = new HashMap<>();

        responseInput.put("outcome", "AccessError");
        responseInput.put("message", "You are already logged into account.");

        responseInput.put("data", new HashMap<>());

        return responseInput;
    }

    public static HashMap<String, Object> logOutResponse() {
        HashMap<String, Object> responseInput = new HashMap<>();

        responseInput.put("outcome", "OK");
        responseInput.put("message", "You have logged out from your " +
            "account.");

        responseInput.put("data", new HashMap<>());

        return responseInput;
    }

    // getters and setters required for object mapper.
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
