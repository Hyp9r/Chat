package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class Controller {

    @FXML
    private TextField field;
    @FXML
    private static TextArea textArea;
    @FXML
    private static ListView<String> listView;

    private Client client = new Client();
    private Thread x = new Thread(client);
    //private List<Message> list = new ArrayList<Message>();

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


    //ova funkcija prima cijelu poruku od servera u kojoj se nalazi svi useri koji su se konektali(tj njihove ip adrese) i fileovi
    //ovdje idemo kroz IP dio i splitamo svaki ip posebno i dodajemo ga u listu stringova
    //onda idemo kroz message dio i splitamo svaki message i dodajemo ga u drugu listu
    //na kraju idemo kroz listu i samo spajamo ip sa porukom
    //i onda updateamo listview
    public static void updateListView(Message msg){
        List<String> mainList = new ArrayList<String>();
        List<String> list = new ArrayList<String>();
        List<String> list1 = new ArrayList<String>();
        String IPData = msg.getIP();
        list.addAll(Arrays.asList(IPData.split("%")));
        String MessageData = msg.getMessage();
        list1.addAll(Arrays.asList(MessageData.split("%")));
        for(int i=0; i<list.size()-1; i++){
            StringBuilder sb = new StringBuilder();
            sb.append(list.get(i));
            sb.append(":");
            sb.append(list1.get(i));
            mainList.add((sb.toString()));
            sb = null;
        }
        ObservableList<String> items =FXCollections.observableArrayList(mainList);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //listView.getItems().setAll(mainList);
                        listView.setItems(items);
                    }
                });
            }
        };

        new Thread(task).start();

    }

    public static void updateTextArea(String text){
        textArea.appendText(text + "\n");
    }

    public void initialize(){
        listView = new ListView<String>();
        x.start();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectFirst();
    }

}
