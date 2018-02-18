/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverchat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ServerThread extends from Thread ,this class handle the gui of the server and
 * wait for new connection
 *
 * @author idan adam
 */
public class ServerThread extends Thread implements ChatProtocol {

    private static final int _PORT = 5060;
    static ServerSocket serverSocket;
    static Socket clientSocket;
    static PrintWriter out;
    static BufferedReader in;
    static String inputLine;
    static ClientList clients;

    /**
     * Server thread constructor
     */
    public ServerThread() {
        clients = new ClientList();
    }

    /**
     * The run method open a new socket and wait for connections, and then add
     * them to client list
     */
    @Override
    public synchronized void run() {
        try {
            serverSocket = new ServerSocket(_PORT);
            ServerGui.StartBtn.setText("Stop");
            ServerGui.isConnected = true;
            ServerGui.chatWin.setText(ServerGui.chatWin.getText().trim() + "system: <connected>");
            while (true) {
                try {
                    Socket cs = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                    PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
                    String payLoad;
                    String name = "";
                    while ((payLoad = in.readLine()) != null) {
                        if (GetRequest(payLoad).equals(CONNECT)) {
                            try {
                                name = getConnectedName(payLoad);
                                break;
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (!clients.isContained(name)) {
                        clients.add(new Client(cs, name));;
                        clients.messageAll(name + " is connected");
                    }else{
                    out.println(nameUsed());
                    }
                } catch (Exception e) {
                };
            }
        } catch (Exception e) {
        };
    }

    /**
     * Disconnect the server and remove all clients
     */
    public void disconnect() {
        Thread r = new Thread() {
            @Override
            public void run() {
                clients.removeAll();
            }
        };
        r.start();
        try {
            serverSocket.close();
            this.interrupt();
            ServerGui.isConnected = false;
        } catch (Exception e) {
        };

    }
    
    /**
     * This function will return the number of online users
     * @return integer the number of online users
     */
    public int getOnlineUsers(){
    return clients.getSize();
    }

}
