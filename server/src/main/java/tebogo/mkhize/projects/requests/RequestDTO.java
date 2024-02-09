package tebogo.mkhize.projects.requests;

import java.io.IOException;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;


public class RequestDTO {
    private String account;
    private String request;
    private List<String> args;
    private static final ObjectMapper mapper = new ObjectMapper();


    /**
     * Converts json string into an instance of ClientRequest class.
     * @param json String representation of Json object.
     * @return instance of ClientRequest class
     */
    public static RequestDTO jsonStringToRequest(String json) {
        RequestDTO request = null;

        try {
            request = mapper.readValue(json, RequestDTO.class);
        } catch (IOException e) {
            System.out.println("Error occured in mapping json " +
                    "string to object.");
            e.printStackTrace();
        }

        return request;
    }

    // getters and setters required for object mapper.
    public String getAccount() {return this.account;}

    public void setAccount(String account) {this.account = account;}

    public String getRequest() {return this.request;}

    public void setRequest(String request) {this.request = request;}

    public List<String> getArgs() {return this.args;}

    public void setArgs(List<String> args) {
        this.args = args;
    }

}

