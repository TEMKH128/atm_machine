package tebogo.mkhize.projects;

import java.util.Scanner;
import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.BindException;

import tebogo.mkhize.projects.database.DbHandler;
import tebogo.mkhize.projects.database.DbInterface;


public class ATMServer {
    private ServerSocket atmServer;
    private DbInterface atmDatabase;
    private boolean shutdownServer = false;
    private final static int PORT_NUM = 5000;

    public ATMServer () {
        createServerSocket();
        atmDatabase = new DbHandler();

        if (atmDatabase.isInitialConnectStatus()) {
            System.out.println("ATM Database Up and Running.");
        }
        new Thread(this:: serverCommandListener).start();
    }

    private void createServerSocket() {
        try {
            this.atmServer = new ServerSocket(ATMServer.PORT_NUM);

            System.out.println("ATM Simulation Up and Running.");

        } catch (BindException e) {
            System.out.println("Port number for Address " +
                    "Already in Use (Bind Failed)");

        } catch (IOException e) {
            System.out.println("Error Occurred...Please Try Again Later.");
        }
    }

    /**
     * Listens for server commands and
     * executes necessary logic in relation to each command.
     */
    private void serverCommandListener() {
        Scanner listener = new Scanner(System.in);

        while (! shutdownServer) {
            System.out.println("Enter \"Quit\" At any Point to Shut Down "+
                "Server.\n\n");

            String serverCommand = listener.nextLine();

            if (serverCommand.equalsIgnoreCase("quit") ||
              serverCommand.equalsIgnoreCase("exit")) {
                shutdownServer = true;
                System.out.println("Server shutting down...");
                System.exit(0);
            }
        }
    }

    private void runATMServer() {
        while (! shutdownServer) {
            try {
                Socket socket = this.atmServer.accept();
                System.out.println("New Client Connection Has " +
                    "Been Established.\n");

                Runnable clientInstance = new ClientHandler(socket,
                    this.atmDatabase);
                Thread task = new Thread(clientInstance);
                task.start();

            } catch (IOException e) {
                System.out.println("An error has occurred. Please try " +
                    "again.");
            }
        }
    }


    public static void main(String[] args) {
        ATMServer atmServer = new ATMServer();
        atmServer.runATMServer();
    }
}