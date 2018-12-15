package sample;
import javafx.fxml.FXML;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client implements Runnable{

    private ObjectInputStream input;
    private OutputStream os;
    private static ObjectOutputStream output;
    private InputStream is;
    private String ip;
    private Socket socket;
    Scanner scanner;
    private Controller controller;

    public Client(){
        scanner = new Scanner(System.in);
    }

    public void run(){
        this.ip = scanner.next();
        try{
            socket = new Socket("localhost", 5000);
            os = socket.getOutputStream();//kanal iz kojeg teku podatci do servera
            output = new ObjectOutputStream(os);//to je kanal koji može slati objekte
            is = socket.getInputStream();//kanal iz kojeg dolaze podatci od servera
            input = new ObjectInputStream(is);//kanal koji može dobivati objekte
        }catch(IOException e){
            e.printStackTrace();
        }


//        try{
//            connect(ip);
//            while(socket.isConnected()){
//                String message = null;
//                message = (String) input.readObject();
//
//                if(message != null){
//                    //check for stuff and print the message to the user
//                    System.out.println(message);
//                    //tu bi trebao imati static metodu iz controlera koja samo updatea textarea
//                    //Controller.updateTextArea(message);
//                }
//
//            }
//        }catch (IOException | ClassNotFoundException e){
//            e.printStackTrace();
//        }

        try{
            connect(ip);
            while(socket.isConnected()){
                Message message = null;
                message = (Message) input.readObject();

                if(message != null){
                    switch (message.getType()){
                        case CONNECT:
                            System.out.println(message.getIP() + " just connected.");
                            break;
                        case DISCONNECT:
                            System.out.println(message.getIP() + " just disconnected.");
                            break;
                        case MESSAGE:
                            if(!message.getIP().equals(ip))
                                System.out.println(message.getIP() + " : " + message.getMessage());
                            else
                                System.out.println(message.getMessage());
                            break;
                        case DHT:
                            //dakle mi šaljemo nešto kao GET upit pa da nam server vrati nazad DHT tablicu
                            //tu dok šaljem poruku trebam na neki način napravit da mogu odabrati dht
                            //ili bolje rješenje bi bilo da imam button koji pukne serveru poruku
                            //daj šibni listu ip adresa i čvorova koji djele file-ove
                            System.out.println("Server je posalo DHT tablicu:\n" + message.getIP());
                            break;
                    }
                }
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    public void send(String msg)throws IOException{
        //Message createMessage = new Message();
        //createMessage.setIP(msg);
        //createMessage.setFile("fizicki file");
        Message msgToSend = new Message(MessageType.MESSAGE, ip, msg);
        output.writeObject(msgToSend);
        output.flush();
    }

    public void sendDHTRequest()throws IOException{
        Message getDHTMessage = new Message(MessageType.DHT, ip);
        output.writeObject(getDHTMessage);
        output.flush();
    }

    public static void connect(String ip)throws IOException{
        Message msg = new Message(MessageType.CONNECT, ip, "Connect me to the server");
        output.writeObject(msg);
    }
}
