package sample;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Handler extends Thread {
    private Socket socket;
    private ObjectInputStream input;
    private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;
    private static ArrayList<ObjectOutputStream> writers = new ArrayList<ObjectOutputStream>();

    //This is going to be whole list of items in DHT table
    private static Message dht = new Message();
    private static boolean first = true;

    public Handler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            is = socket.getInputStream(); //initialize input stream(connect it to the socket
            input = new ObjectInputStream(is); //initialize object input stream and hook it up to input stream from socket
            os = socket.getOutputStream();
            output = new ObjectOutputStream(os);
            writers.add(output); //tu smo dodali sve naše usere u jednu listu tako da možemo broadcastat
            System.out.println("Added new user to the list and there are " + writers.size() + " users online" );
            Message connectionMessage = (Message) input.readObject();
            System.out.println("New user has been connected to the server: [IP]:" + connectionMessage.getIP() + " [MESSAGE]:" + connectionMessage.getMessage());
            //write(connectionMessage);

            //ovo je novi kod
            //ovdje radim pocetnu konekciju do servera i uzimamo IP adresu i file od clijenta tako da znamo sve dodati u DHT tablicu
            if(first) {
                dht.setType(MessageType.DHT);
                dht.setIP(socket.getRemoteSocketAddress().toString());
                dht.setMessage(connectionMessage.getMessage());
                first = false;
            }else{
                dht.append(connectionMessage);
            }


            while(socket.isConnected()){
                Message messageFromClient = (Message) input.readObject();
                if(messageFromClient.getType().equals(MessageType.DHT)){
                    //dht = new Message(MessageType.DHT, socket.getRemoteSocketAddress().toString(),"\n169.38.84.49\tfile1\n23.246.195.8\tfile2\n" +
                    //        "5.10.105.200\tfile3");
                    //vrati poruku samo ovo clientu i nikome ostalom

                    writeOnlyToThisClient(dht, output);
                }else{
                    write(messageFromClient);
                }

            }

        }catch (IOException | ClassNotFoundException e){
            System.out.println("User disconnected");
        }finally {
            try{
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void write(Message msg)throws IOException{
        for(ObjectOutputStream writer: writers){
            writer.writeObject(msg);
            writer.flush();
        }
    }

    private void writeOnlyToThisClient(Message dhtMessage, ObjectOutputStream output)throws IOException{
        output.writeObject(dhtMessage);
        output.flush();
    }

}
