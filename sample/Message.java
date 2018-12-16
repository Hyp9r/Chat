package sample;

import javafx.fxml.FXML;

import java.io.Serializable;

public class Message implements Serializable {

    private String ip;
    private String message;
    private MessageType type;

    public Message(MessageType type, String ip, String message){
        this.ip = ip;
        this.message = message;
        this.type = type;
    }

    public Message(){
    }

    public Message(MessageType type, String ip){
        this.type = type;
        this.ip = ip;
    }

    public void setIP(String ip){
        this.ip = ip;
    }

    public String getIP(){
        return this.ip;
    }

    public void setMessage(String message){ this.message = message; }

    public String getMessage(){
        return this.message;
    }

    public MessageType getType() { return type; }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString(){
        return this.type + ":" + this.ip + ":" + this.message;
    }


    public void append(Message dataToAppend){
        this.ip = getIP() + "%" + dataToAppend.getIP();
        this.message = getMessage() + "%" + dataToAppend.getMessage();
    }

}
