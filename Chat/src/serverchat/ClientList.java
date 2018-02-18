/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverchat;

import java.util.ArrayList;

/**
 * This class represent a collection that holds list of clients
 *
 * @author idan adam
 */
public class ClientList extends Thread {

    private static ArrayList<Client> clients;

    /**
     * ClientList constructor
     */
    public ClientList() {
        clients = new ArrayList<>();
    }

    /**
     * This function get a message and send it to all of the clients in the list
     *
     * @param msg message to send
     */
    public synchronized static void messageAll(String msg) {
        ServerGui.chatWin.setText(ServerGui.chatWin.getText().trim() + "\n" + msg);
        for (Client client : clients) {
            client.sendMessage(msg);
        }
    }

    /**
     * This function send a message to specific user.
     *
     * @param userName the sender name
     * @param sendName the user to send the message to
     * @param message the message to send
     */
    static void messageTo(String userName, String sendName, String message) {
        Client user = null;
        Client send = null; 
        for (Client client : clients) {
            if (client.userName.equals(sendName)) {
                send = client;
            } else if (client.userName.equals(userName)) {
                user = client;
            }
        }
        if(send!=null){
        ServerGui.chatWin.setText(ServerGui.chatWin.getText().trim() + "\nPM:<" + userName + "><" + sendName + ">" + message);
        send.sendMessage("PM from " + userName + ": " + message);
        user.sendMessage("PM to " + sendName + ": " + message);
        }else{
        user.sendMessage("Invalid Username");
        }

    }

    /**
     * This function get a client and add him to the list
     *
     * @param c client to add
     */
    public void add(Client c) {
        clients.add(c);
    }

    /**
     * This function remove a client from the list
     *
     * @param userName client's username to remove
     */
    static synchronized public void remove(String userName) {
        for (Client client : clients) {
            if (client.userName.equals(userName)) {
                clients.remove(client);
            }
        }
    }

    /**
     * This function remove all the client from the list
     */
    public synchronized void removeAll() {
        for (Client client : clients) {
            client.remove();
        }
    }

    /**
     * This function check if the username is in the list
     *
     * @param usersname the username to check
     * @return true if the username is in the list
     */
    public static synchronized boolean isContained(String usersname) {
        for (Client client : clients) {
            if (client.userName.equals(usersname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function return a string list of all the users in the Clientlist
     *
     * @return string list of users
     */
    public static synchronized String[] getOnlineUsers() {
        String[] list = new String[clients.size() + 1];
        list[0] = "Lobby";
        int i = 1;
        for (Client client : clients) {
            list[i++] = client.userName;
        }
        return list;
    }
    /**
     * This function will return the size of the clients list
     * @return integer the number clients
     */
    public int getSize() {
        return clients.size();
    }

}
