import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  private String hostName; 
  private int portNumber;

  // set connction and server
  public PrintWriter out = null;
  public BufferedReader in = null; 
  public Socket clientSocket = null;

  Client(String hostName, int port) {
    this.hostName = hostName;
    this.portNumber = port;

    // setting the connction
    this.setConnection();
  }

  private void setConnection () {

    try {
      this.clientSocket = new Socket(this.hostName, this.portNumber);
      this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
      this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

 }

  public void sendMessageToServer(String msg) {
    //System.out.println("The msg is : " + msg);
    out.println(msg);
  }

  public String readMessage() {
    String outputMsg = "";
    try {
      outputMsg = in.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return outputMsg;
  }


  // shit cleaning
  public void closeSession(){

    try {
      this.out.close();
      this.in.close();
      this.clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Session has been terminated!");
  }

  public static void main(String args[]) throws IOException{

    // init    
    String host = "localhost";
    int port = 6968;

    Client newClient = new Client(host, port);

    // // send a message 
    Scanner sc = new Scanner(System.in);

    Thread sendMsg = new Thread(new Runnable() {

      @Override
      public void run() {
        while (true) {

          try {

            String userInput = sc.nextLine();
            newClient.sendMessageToServer(userInput);

            if (userInput.equals("exit")) {
              System.exit(1);
            }
          } catch (Exception e) {
            System.exit(1);
          }
        }
      }
    });

    Thread getMsg = new Thread(new Runnable() {

      @Override
      public void run() {
        while (true) {

          String msgRead = "";

          try {

            msgRead = newClient.readMessage();

            if (msgRead != null){
              System.out.println(msgRead);
            }else {
              // clean the meh
              System.out.println("Server has been terminated, don't panic or panic");
              newClient.closeSession();

              System.exit(1);
            }

          } catch (Exception e) {
            System.exit(1);
          }
        }
      }
    });

    sendMsg.start();
    getMsg.start();
  }
}
