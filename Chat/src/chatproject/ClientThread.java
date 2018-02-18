/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatproject;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClientThread extends from Thread, this class handle the gui of the client and
 * communicate with the server by socket.
 *
 * @author idan adam
 */
public class ClientThread extends Thread implements ChatProtocol {

    private static final int _PORT = 5060;
    private String userName;
    private String ipAddr;
    private BufferedReader _reader;
    private PrintWriter _writer;
    private Socket soc;

    /**
     * ClientThread constructor, get a username and ip address.
     *
     * @param userName the username of the client
     * @param ipAddr ip address of the server
     */
    public ClientThread(String userName, String ipAddr) {
        this.userName = userName;
        this.ipAddr = ipAddr;

    }

    /**
     * This function waits for new messages from the server and handle them.
     */
    public synchronized void run() {
        try {
            soc = new Socket(ipAddr, _PORT);
            _reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            _writer = new PrintWriter(soc.getOutputStream(), true);
            ClientGui.isConnected = true;
            ClientGui.ConnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/button_disconnect.png")));
            ClientGui.ConnectBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/button_disconnectp.png")));
            ClientGui.nameBox.setText(userName);
            _writer.println(connectMsg(userName));
            startCheckForOnlineUsers();
            String str;
            AudioClip clip = Applet.newAudioClip(getClass().getResource("/beep.wav"));
            while ((str = _reader.readLine()) != null) {
                switch (GetRequest(str)) {
                    case DISCONNECT:
                        if (!ClientGui.speakerBox.isSelected()) {
                            clip.play();
                        }
                        if (getDisconnectedName(str).equals(userName)) {
                            disconnectFromServer();
                        }
                        break;
                    case NAME_USED:
                        invalidUsername();
                        break;
                    case GET_USERS:
                        UpdateUsersList(str);
                        break;
                    default:
                        if (!ClientGui.speakerBox.isSelected()) {
                            clip.play();
                        }
                        ClientGui.chatWin.setText(ClientGui.chatWin.getText().trim() + "\n" + str);
                        break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * If for some reason the server disconnected the client this function will
     * run and close the thread and the socket properly.
     */
    public void disconnectFromServer() {
        ClientGui.isConnected = false;
        ClientGui.ConnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/button_connect.png")));
        ClientGui.ConnectBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/button_connectp.png")));
        ClientGui.nameBox.setText("0");
        ClientGui.usersList.setListData(new String[0]);
        ClientGui.chatWin.setText(ClientGui.chatWin.getText().trim() + "\nYou disconnected");
        try {
            soc.close();
            this.interrupt();
        } catch (Exception e) {
        }
    }

    /**
     * This function disconnect the Client from the chat and popup a invaild user name error message
     */
    public void invalidUsername() {
        ClientGui.isConnected = false;
        ClientGui.ConnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/button_connect.png")));
        ClientGui.ConnectBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/button_connectp.png")));
        ClientGui.nameBox.setText("0");
        ClientGui.usersList.setListData(new String[0]);
        ClientGui.ErrorMsg.setText("The name "+userName+" is used");
        ClientGui.jDialog1.setVisible(true);
        Toolkit.getDefaultToolkit().beep();
        try {
            soc.close();
            this.interrupt();
        } catch (Exception e) {
        }
    }

    /**
     * Disconnect the client from the chat and close the socket and the thread.
     */
    public void disconnect() {
        try {
            _writer = new PrintWriter(soc.getOutputStream(), true);
            ClientGui.chatWin.setText(ClientGui.chatWin.getText().trim() + "\nYou disconnected");
            ClientGui.isConnected = false;
            ClientGui.ConnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/button_connect.png")));
            ClientGui.ConnectBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/button_connectp.png")));
            ClientGui.nameBox.setText("0");
            ClientGui.usersList.setListData(new String[0]);
            _writer.println(disconnectMsg(userName));
            soc.close();
            this.interrupt();
        } catch (Exception e) {
        }
    }

    /**
     * This function send the message from the client to the server.
     *
     * @param msg message to send
     */
    public void SendMessage(String msg) {
        if (GetRequest(msg).equals("")) {
            return;
        }
        if (GetRequest(msg).equals(SEND_MESSAGE)) {
            _writer.println((msg));
        } else {
            _writer.println(messsageALL(userName, msg));
        }
    }

    /**
     * this function gets a formated string with list of users and return
     * String[] of the list
     *
     * @param str the formated string
     */
    private void UpdateUsersList(String str) {
        String[] temp = str.split(">");
        String[] list = new String[temp.length - 2];
        for (int i = 1, j = 0; i < temp.length; i++) {
            temp[i] = temp[i].replace("<", "");
            if (!temp[i].equals(userName)) {
                list[j++] = temp[i];
            }
        }
        ClientGui.usersList.setListData(list);
    }

    /**
     * This function run a thread that request for the list of online users ever
     * 500 ms.
     */
    private void startCheckForOnlineUsers() {
        Thread t = new Thread() {
            @Override
            public void run() {
                _writer.println(getUsers());
                String str;
                try {
                    while (true) {
                        this.sleep(500);
                        _writer.println(getUsers());
                    }
                } catch (Exception e) {
                };
            }

        };
        t.start();
    }

}
