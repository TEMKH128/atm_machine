package tebogo.mkhize.projects;

import java.io.*;
import java.util.List;
import java.net.Socket;
import java.util.HashMap;

import tebogo.mkhize.projects.requests.Response;
import tebogo.mkhize.projects.requests.RequestDTO;
import tebogo.mkhize.projects.database.DbInterface;
import tebogo.mkhize.projects.requests.atmFeatures.ATMFeature;


public class ClientHandler implements Runnable {
    private Socket socket;
    private DbInterface atmDatabase;
    private BufferedWriter msgSender;
    private BufferedReader msgReceiver;
    private boolean isLoggedIn = false;

    public ClientHandler(Socket socket, DbInterface atmDatabase) {
        this.socket = socket;
        this.atmDatabase = atmDatabase;

        try {
            this.msgReceiver = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

            this.msgSender = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String request = msgReceiver.readLine();

                if (request == null) {
                    break;
                }
                System.out.println("Request: " + request);
                handleRequest(request);
            }

        } catch (IOException ignored) {

        } finally {
            closeEverything();;
        }
    }

    private void handleRequest(String request) {
        RequestDTO mappedRequest = RequestDTO.jsonStringToRequest(request);
        String clientRequest = mappedRequest.getRequest();

        String[] preAccessRequests = new String[] {"login", "signup", "help"};

        if (! isLoggedIn && ! List.of(preAccessRequests).contains(
            clientRequest.toLowerCase())) {

            Response response = generalResponseHandler("LoginSignup",
                mappedRequest);

            String responseStr = response.responseMsgToJsonString();
            dispResponseOnServer(responseStr);
            sendMessage(responseStr);
            return;

        } else if (mappedRequest.getRequest().equalsIgnoreCase("login") &&
            this.isLoggedIn) {

            Response response = generalResponseHandler("AlreadyLoggedIn",
                mappedRequest);

            String responseStr = response.responseMsgToJsonString();
            dispResponseOnServer(responseStr);
            sendMessage(responseStr);
            return;

        } else if (mappedRequest.getRequest().equalsIgnoreCase("logout")) {
            Response response = generalResponseHandler("LoggingOut",
                mappedRequest);

            String responseStr = response.responseMsgToJsonString();
            dispResponseOnServer(responseStr);
            sendMessage(responseStr);
            closeEverything();
        }

        try {
            ATMFeature featureRequest = ATMFeature.createRequestFeature(
                mappedRequest);
            featureRequest.execute(this.atmDatabase);

            String response = featureRequest.generateResponse().responseMsgToJsonString();
            dispResponseOnServer(response);
            sendMessage(response);

            if (mappedRequest.getRequest().equalsIgnoreCase("login")) {
                this.isLoggedIn = featureRequest.getRequestOutcome();
            }

        } catch (IllegalArgumentException e) {
            Response response = generalResponseHandler("UnsupportedRequest",
                mappedRequest);

            String responseStr = response.responseMsgToJsonString();
            dispResponseOnServer(responseStr);
            sendMessage(responseStr);
        }
    }

    /**
     * Displays response to be sent to client on the server console.
     * @param response response to be displayed.
     */
    private void dispResponseOnServer(String response) {
        System.out.println("response: " + response + "\n");
    }

    private Response generalResponseHandler(String type, RequestDTO request) {
        HashMap<String, Object> input;
        Response response;
        switch (type) {
            case "LoginSignup":
                 input = Response.loginSignupResponse();

                response = new Response(request.getAccount(),
                    (String) input.get("outcome"),
                    (String) input.get("message"),
                    (HashMap<String, Object>) input.get("data"));

                return response;

            case "UnsupportedRequest":
                input = Response.unsupportedRequest(
                    request.getRequest());

                response = new Response(request.getAccount(),
                    (String) input.get("outcome"),
                    (String) input.get("message"),
                    (HashMap<String, Object>) input.get("data"));

                return response;

            case "AlreadyLoggedIn":
                input = Response.alreadyLoggedInResponse();

                response = new Response(request.getAccount(),
                (String) input.get("outcome"),
                    (String) input.get("message"),
                    (HashMap<String, Object>) input.get("data"));

                return response;

            default:  // LoggingOut
                input = Response.logOutResponse();

                response = new Response(request.getAccount(),
                    (String) input.get("outcome"),
                    (String) input.get("message"),
                    (HashMap<String, Object>) input.get("data"));

            return response;
        }
    }


    private void sendMessage(String message) {
        try {
            this.msgSender.write(message);
            this.msgSender.newLine();
            this.msgSender.flush();
        }
        catch (IOException ignored) {}
    }

    /**
     * Closes resources used, namely - BufferedReader, BufferedWriter
     * and the socket.
     */
    private void closeEverything() {
        System.out.println("Client has disconnected.\n");

        try {
            this.msgReceiver.close();
            this.msgSender.close();
            this.socket.close();

        } catch (IOException ignored) {}
    }
}
