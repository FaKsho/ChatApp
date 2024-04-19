package ChatApp;

import java.io.Serializable;
import java.util.ArrayList;

public class PackageDataSend implements Serializable {

    private String nick, message, ip;
    ArrayList<String> connectedClients = new ArrayList<>();


    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setConnectedClients(ArrayList<String> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public ArrayList<String> getConnectedClients() {
        return connectedClients;
    }

    public String toString(){
        return nick +  " to -> (" + ip + "): " + message;
    }
}
