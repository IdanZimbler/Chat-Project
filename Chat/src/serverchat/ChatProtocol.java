/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverchat;

/**
 * This is the interface of the protocol of the chat
 *
 * @author idan adam
 */
public interface ChatProtocol {

    static final String CONNECT = "connect";
    static final String GET_USERS = "get_users";
    static final String DISCONNECT = "disconnect";
    static final String SEND_MESSAGE = "set_msg";
    static final String SEND_MESSAGE_ALL = "set_msg_all";
    static final String NAME_USED = "name_used";

    /**
     * this function get a formated string and return the second paramater      *
     * @param payload the formated string
     * @return string the name of the sender
     * @throws Exception not a connect request
     */
    default public String getConnectedName(String payload) throws Exception {
        String name = "";
        String[] a = payload.split(">", 3);
        a[0] = a[0].replace("<", "");
        a[1] = a[1].replace("<", "");
        if (a[0].equals(CONNECT)) {
            name = a[1];
        } else {
            throw new Exception();
        }
        return name;
    }

    /**
     * this function get a string formated and return String "name"
     * @param payload the formated string
     * @return string the name of the sender
     * @throws Exception not a disconnect request
     */
    default public String getDisconnectedName(String payload) throws Exception {
        String name = "";
        String[] a = payload.split(">", 3);
        a[0] = a[0].replace("<", "");
        a[1] = a[1].replace("<", "");
        if (a[0].equals(DISCONNECT)) {
            name = a[1];
        } else {
            throw new Exception();
        }
        return name;
    }

    /**
     * this function get a string formated and return String "name"
     *
     * @param payload the formated string
     * @return string the name of the sender
     * @throws Exception not a set_msg request
     */
    default public String getSendName(String payload) throws Exception {
        String name = "";
        String[] a = payload.split(">", 3);
        a[0] = a[0].replace("<", "");
        a[1] = a[1].replace("<", "");
        if (a[0].equals(SEND_MESSAGE)) {
            name = a[1];
        } else {
            throw new Exception();
        }
        return name;
    }

    /**
     * this function get a string formated and return String "name"
     *
     * @param payload the formated string
     * @return string the request
     */
    default public String GetRequest(String payload) {
        String request = "";
        String[] a = payload.split(">", 2);
        a[0] = a[0].replace("<", "");
        return a[0];
    }

    /**
     * this function add the disconnect signature to the string
     *
     * @param name the name of the sender
     * @return formated string
     */
    default public String disconnectMsg(String name) {
        return "<" + DISCONNECT + "><" + name + ">";
    }

    /**
     * this function return "get users" request formated string
     *
     * @return "get users" request formated string
     */
    default public String getUsers() {
        return "<" + GET_USERS + ">";
    }

    /**
     * this function get a list of users and return formated string of the list
     *
     * @param list list of users
     * @return formated list of users
     */
    default public String getUsers(String[] list) {
        String s = "<" + GET_USERS + ">";
        for (int i = 0; i < list.length; i++) {
            s += "<" + list[i] + ">";
        }
        return s;
    }

    /**
     * this function return "connect" request formated string
     *
     * @param name the name of the sender
     * @return "connect" request formated string
     */
    default public String connectMsg(String name) {
        return "<" + CONNECT + "><" + name + ">";
    }

    /**
     * this function get a name of sender and message and return formated string
     * of the message
     *
     * @param from name of the sender
     * @param msg the message
     * @return formated string
     */
    default public String messsageALL(String from, String msg) {
        return "<" + SEND_MESSAGE_ALL + "><" + from + "><" + msg + ">";
    }

    
    /**
     * this function return "name used" request formated string
     * @return "name used" request formated string
     */
    default public String nameUsed() {
       return "<" + NAME_USED + ">";
    }

    /**
     * this function get a name of sender, name of the receiver and message and
     * return a formated string of send_to request
     *
     * @param from name of the sender
     * @param to name of the receiver
     * @param msg the message
     * @return formated string
     */
    default public String messsageTo(String from, String to, String msg) {
        return "<" + SEND_MESSAGE + "><" + from + "><" + to + "><" + msg + ">";
    }

    /**
     * this function "peel" the request of a formated string and return pure
     * message
     *
     * @param msg formated string
     * @return message
     */
    default public String GetMessage(String msg) {
        String[] a;
        switch (GetRequest(msg)) {
            case CONNECT:
                return "is connected";
            case SEND_MESSAGE:
                a = msg.split(">", 5);
                a[2] = a[2].replace("<", "");
                return a[2];
            case SEND_MESSAGE_ALL:
                a = msg.split(">", 5);
                a[2] = a[2].replace("<", "");
                return a[2];
            default:
                break;
        }
        return msg;
    }
}
