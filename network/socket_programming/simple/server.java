import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Echo {

    private static int portNumber = 6968;

    public static void main(String[] args) throws Exception {

        // init
        // server works by default on localhost
        ServerSocket serverSocket = new ServerSocket(portNumber);
        Socket clientSocket = null;

        try {
            // listening
            clientSocket = serverSocket.accept();
        }
        catch (IOException e) {
            System.out.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Echo server started");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Cat said : " + inputLine);
            out.println(inputLine);
        }

        // cleaning shit
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();

    }
}
