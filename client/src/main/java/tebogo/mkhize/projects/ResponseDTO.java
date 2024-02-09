package tebogo.mkhize.projects;

import java.util.HashMap;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;


public class ResponseDTO {
    private String account;
    private String outcome;
    private String message;
    private HashMap<String, Object>  data;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ResponseDTO jsonStringToRequest(String json) {
        ResponseDTO request = null;

        try {
            request = mapper.readValue(json, ResponseDTO.class);
        } catch (IOException e) {
            System.out.println("Error occured in mapping json " +
                    "string to object.");
            e.printStackTrace();
        }

        return request;
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
