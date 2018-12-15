package sample;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Server {

    private ArrayList<ObjectOutputStream> writers = new ArrayList<ObjectOutputStream>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)){
            while(true){
                new Handler(serverSocket.accept()).start();
            }
        }catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }
    }

}