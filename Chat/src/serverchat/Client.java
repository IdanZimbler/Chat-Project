/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverchat;

 import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *Client class represent the client in the chat,the class open a socket and communicate with the client
 * @author idan adam
 */
public class Client implements ChatProtocol{
    /**
     * Inner class of Cthread extends Thread, this class open a socket and list for a messages from the client
     */
    class Cthread extends Thread{
        Socket s;
        public Cthread(Socket s) {
        this.s = s;
        }
        
        @Override
        /**
         * This function open a socket and list for a messages from the client
         */
        public synchronized void run() {
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String s;
            while ((s = in.readLine()) != null) {
             switch(GetRequest(s)){
                 case GET_USERS:
                     out.println(getUsers(ClientList.getOnlineUsers()));
                     break;
                 case DISCONNECT:
                     if(getDisconnectedName(s).equals(userName)) remove();
                     break;
                 case SEND_MESSAGE:
                        sendPrivateMessage(getSendName(s),GetMessage(s));
                    break; 
                 default:
                    sendToClientList(s);
                  break;
             }
             
            }
            out.close();
            in.close();
        }catch(Exception e){};
       } 


    }
    private Socket s;
    private Cthread t;
    public String userName;
 
    /**
     * Client constructor get a socket and username of the client
     * @param s socket of the client
     * @param userName username of the client
     */
    public Client(Socket s,String userName) {
        this.userName = userName;
        this.s = s;
        t = new Cthread(s);
        t.start();
    } 
    
    /**
     * This function send a message to the clientList to send it to all of the clients
     * @param msg message to send
     */
    private void sendToClientList(String msg){
        ClientList.messageAll(userName+": "+GetMessage(msg));
    }
    
    /**
     * This function send a message to a specific client
     * @param sendName the name of the client to send
     * @param message the message
     */
    private void sendPrivateMessage(String sendName, String message) {
        ClientList.messageTo(userName,sendName,message);
        
    }
    
    /**
     * This function send message to the client
     * @param msg message to send
     */
    public void sendMessage(String msg){
      try{
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        out.println(msg);
      }catch(Exception e){};
    }
    
    /**
     * This function remove the client and close the socket
     */
    public void remove(){
    try{
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        ClientList.messageAll(disconnectMsg(userName));
        ClientList.remove(userName);
        t.join();
        t.interrupt();
        s.close();
    }catch(Exception e){};
    }
    
}

