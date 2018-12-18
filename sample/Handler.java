package sample;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Handler extends Thread {
    private Socket socket;
    private ObjectInputStream input;
    private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;
    private static ArrayList<ObjectOutputStream> writers = new ArrayList<ObjectOutputStream>();

    private HashMap<String, String> dhtTablica= new HashMap<String,String>();
    private int counter = 0;

    private Lock lock = new ReentrantLock();

    //This is going to be whole list of items in DHT table
    private static Message dht;
    private static boolean first = true;

    //problem sa ovim nacinom, tu imamm bug
    //dakle prvi klijent koji se prikljuci poslat ce svoje podatke i kada bude trazio DHT tablicu
    //ovaj ce mu sibat tablicu sa samo njegovom IP adresom
    //thread drugog klijenta ce imati podatke od prvog klijenta i sebe
    //dakle thread samo zadnjeg klijenta ce imati tablicu sa svim klijentima


    //tu je najvjerovatnije bug sa stringovima, zbog poola pa se referencira samo na onaj prvi string pocetni i tamo ostane




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
            counter++;
            System.out.println("Added new user to the list and there are " + writers.size() + " users online" );
            Message connectionMessage = (Message) input.readObject();
            System.out.println("New user has been connected to the server: [IP]:" + connectionMessage.getIP() + " [MESSAGE]:" + connectionMessage.getMessage());
            //write(connectionMessage);

            //ovo je novi kod
            //ovdje radim pocetnu konekciju do servera i uzimamo IP adresu i file od clijenta tako da znamo sve dodati u DHT tablicu
//            if(first) {
//                dht.setType(MessageType.DHT);
//                dht.setIP(socket.getRemoteSocketAddress().toString());
//                dht.setMessage(connectionMessage.getMessage());
//                first = false;
//            }else{
//
//                dht.append(connectionMessage);
//            }


            while(socket.isConnected()){
                Message messageFromClient = (Message) input.readObject();
                if(messageFromClient.getType() == MessageType.DHT){
                    write();
                    //dht = new Message(MessageType.DHT, socket.getRemoteSocketAddress().toString(),"\n169.38.84.49\tfile1\n23.246.195.8\tfile2\n" +
                    //        "5.10.105.200\tfile3");
                    //vrati poruku samo ovo clientu i nikome ostalom
                    //writeOnlyToThisClient(dht, output);
                }else if(messageFromClient.getType() == MessageType.CONNECT){
                    int i=0;
                    lock.lock();
                    try {
                        dhtTablica.put(messageFromClient.getIP(), messageFromClient.getMessage());
                        i++;
                    } finally {
                        lock.unlock();
                    }

                    if(i < counter){
                        lock.lock();
                    }else{
                        lock.unlock();
                    }

                    w

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



    //šaljemo svim klijentima poruku da nam vrati svoje ip adrese i file koji ima
    //i onda kada nam klijent vrati sve dodamo u listu
    private void write()throws IOException{
        for(ObjectOutputStream writer: writers){
            writer.writeObject(new Message(MessageType.METADATA, "dsad"));
            writer.flush();
        }
    }


    private void writeOnlyToThisClient(Message dhtMessage, ObjectOutputStream output)throws IOException{
        output.writeObject(dhtMessage);
        output.flush();
    }

}
