import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MainServer {

    private static int portnumber = 6968;
    static ArrayList <ClientHandler> allClients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        // init
        // server works by default on localhost
        ServerSocket serversocket = new ServerSocket(portnumber);
        int clientNo = 0;
    
        System.out.println("server is running on port : " + portnumber);

        while (true) {

          // creating a socket for each connection
          Socket clientsocket = null;

          try {
             // receiving incoming requests from users/clients
            clientsocket = serversocket.accept();

            // input and output from client
            PrintWriter out = new PrintWriter(clientsocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));

            // create a threads
            ClientHandler ch = new ClientHandler(clientsocket, "Client#" + clientNo, in, out);
        
            // adding to the clientList
            allClients.add(ch);

            System.out.println(ch.clientName + " has joined");
            Thread clientThread = new Thread(ch);
            
            clientThread.start();

            // decrease when user leaves
            clientNo++;
           
          } catch (Exception e) {
            clientsocket.close();
            e.printStackTrace();
            System.exit(1);

            //out.close();
            //in.close();
            //clientSocket.close();
            //serverSocket.close();
          }
        }
    }
}


// handle client requests
public class ClientHandler implements Runnable{

  public String clientName;
  public Socket clientsocket = null;
  public boolean active = false;
  private BufferedReader inp;
  private PrintWriter out;
  private final String EXIT_STR = "exit";
  public Scanner clientSc = new Scanner(System.in);

  public ClientHandler(Socket socket, String name, BufferedReader inp, PrintWriter out) {
    this.clientsocket = socket;
    this.clientName = name;
    this.inp = inp;
    this.out = out;

    // active when the thread is created
    this.active = true;
  }

  @Override
  public void run() {

    // getting the output temp
    String recivedMsg = "";
    
    while (true) {
      try {
        recivedMsg = inp.readLine();

        System.out.println(recivedMsg);

        // check for ctrl+C
        if (recivedMsg.equals(this.EXIT_STR)){

          // send to all
          System.out.println(this.clientName + " exits");

          // close the connection and break
          this.active = false;
          this.clientsocket.close();

          // bye
          break;
        }

        // send to all except me
        for (ClientHandler client : MainServer.allClients){

          if (!client.clientName.equals(this.clientName)){
            client.out.println(this.clientName + ":" + recivedMsg);
            client.out.flush();
            break;
          }
        }
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      // clean
      this.clientsocket.close();
      this.inp.close();
      this.out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


