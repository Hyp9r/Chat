package sample;

import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller {

    @FXML
    private TextField field;
    @FXML
    private static TextArea textArea;
    @FXML
    private ListView<Message> listView;

    private Client client = new Client();
    private Thread x = new Thread(client);

    @FXML
    public void onButtonClicked(){
        String dataToSend = field.getText();
        try {
        client.send(dataToSend);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void onButtonClicked1(){
        try{
            client.sendDHTRequest();
        }catch (IOException e){
            e.printStackTrace();
        }
        //tu updateaj listView sa itemima koje si dobio od servera, tj sve ih parsaj kako trebaj
    }

    public static void updateTextArea(String text){
        textArea.appendText(text + "\n");
    }

    public void initialize(){
        listView = new ListView<Message>();
        x.start();
    }

}
