package tebogo.mkhize.projects;

import java.util.List;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;


public class Request {
    private String account;
    private String request;
    private List<String> args;
    private static final ObjectMapper mapper = new ObjectMapper();

    public Request(String account, String request, List<String> args) {
        this.account = account;
        this.request = request;
        this.args = args;
    }

    /**
     * Generates JSONObject using passed object and
     * returns JSONObject as a string.
     * @return
     */
    public String createJSONString() {
        try {
            return mapper.writeValueAsString(this);

        } catch (IOException e) {
            System.out.println("Error in creating JSON object string");
            return null;
        }
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
