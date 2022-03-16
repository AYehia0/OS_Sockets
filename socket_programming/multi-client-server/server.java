import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer {

    private static int portnumber = 6968;
    public static ArrayList <ClientHandler> allClients = new ArrayList<>();
    public static int clientNo = 1;
    public static void main(String[] args) throws IOException {

        // init
        // server works by default on localhost
        ServerSocket serversocket = new ServerSocket(portnumber);
        System.out.println("server is running on port : " + portnumber);

        PrintWriter out = null;
        BufferedReader in = null;

        while (true) {

          // creating a socket for each connection
          Socket clientsocket = null;

          try {
             // receiving incoming requests from users/clients
            clientsocket = serversocket.accept();

            // input and output from client
            out = new PrintWriter(clientsocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));

            // create a threads
            ClientHandler ch = new ClientHandler(clientsocket, "Client#" + MainServer.clientNo, in, out);
        
            // adding to the clientList
            allClients.add(ch);

            System.out.println(ch.clientName + " has joined");
            Thread clientThread = new Thread(ch);
            
            clientThread.start();

            MainServer.clientNo++;
           
          } catch (Exception e) {
            clientsocket.close();
            System.exit(1);
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

  public ClientHandler(Socket socket, String name, BufferedReader inp, PrintWriter out) {
    this.clientsocket = socket;
    this.clientName = name;
    this.inp = inp;
    this.out = out;

    // active when the thread is created
    this.active = true;
  }

  private void closeConnection(){

    try {
      this.active = false;
      this.clientsocket.close();
      this.inp.close();
      this.out.close();

      MainServer.clientNo--;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // send message to everyone except me
  private void sendToAll(String recivedMsg) {
    for (ClientHandler client : MainServer.allClients){
      if (!client.clientName.equals(this.clientName) && client.active){
        client.out.println(recivedMsg);
       }
     }
  }

  @Override
  public void run() {

    // getting the output temp
    String recivedMsg = "";
    
    while (true) {
      try {
        recivedMsg = inp.readLine();

        // check for ctrl+C
        //
        try {
         if (recivedMsg.equals(this.EXIT_STR)){

          // send to all
          this.sendToAll(this.clientName + " exits");
          this.closeConnection();

          // bye
          break;
        }

        } catch (NullPointerException e) {
          this.sendToAll(this.clientName + " exits");
          this.closeConnection();
          break;
        }

        // send to all except me
        this.sendToAll(this.clientName + "sends " +  recivedMsg);

      } catch (IOException e) {
        System.exit(1);
      }
    }

    try {
      // clean
      this.clientsocket.close();
      this.inp.close();
      this.out.close();

    } catch (IOException e) {

    }
  }
}


