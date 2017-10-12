package sample;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class defaultControl implements Initializable {

    private Main main;

    private String url="src/sample/p2.jpg";
    private File fl=new File(url);

    Image img=new Image(fl.toURI().toASCIIString());

    @FXML
    private Pane pane;

    @FXML
    private TabPane tabpane;

    @FXML
    private Tab homeTab;

    @FXML
    private ImageView imageSector;

    @FXML
    private Tab playlistTab;

    @FXML
    private TableView<Playlist> playlistTable;

    @FXML
    private TableColumn<Playlist,String> playlistTableCol;

    @FXML
    private TableColumn<Playlist,Button> playlistTableCol2;

    @FXML
    private TableColumn<Playlist,Button> playlistTableCol3;

    @FXML
    private Tab historyTab;

    @FXML
    private TableView<Playlist> tableHistory;

    @FXML
    private TableColumn<Playlist,String> historyTableCol;

    @FXML
    private TableColumn<Playlist,Button> historyTableCol2;

    @FXML
    private Tab aboutTab;

    @FXML
    private Tab onlineTab;

    @FXML
    private Button play;

    @FXML
    private Button updatePlaylist;

    @FXML
    private Button clearHistory;

    @FXML
    private Button clearPlaylist;

    @FXML
    private Button startPlaylist;

    @FXML
    private TextField downloadLink;
    @FXML
    private TextField fileName;
    @FXML
    private Button onlineStream;
    @FXML
    private Button downloadButton;
    @FXML
    public ProgressBar downloadProgress;
    @FXML
    public Label progress;
    @FXML
    public Label dp;
    @FXML
    public Label dp1;

    public void setMain(Main m)
    {
        main=m;
    }

    static int i=0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        try
        {
            main.l1.clear();
            main.l2.clear();
            File p=new File(main.playlist);
            if(p.length()!=0)
            {
                FileInputStream Pfis=new FileInputStream(p);
                ObjectInputStream Pois=new ObjectInputStream(Pfis);
                //Object o=Pois.readObject();
                main.l1=(ArrayList)Pois.readObject();
                //main.l1=(ArrayList)o;
                Pois.close();
                for(Playlist x:main.l1)
                {
                    main.listOfPlaylist.add(x);
                }
            }

            File h=new File(main.history);
            if(h.length()!=0)
            {
                FileInputStream Hfis=new FileInputStream(h);
                ObjectInputStream Hois=new ObjectInputStream(Hfis);
                //Object o=Hois.readObject();
                main.l2=(ArrayList)Hois.readObject();
                //main.l2=(ArrayList)o;
                Hois.close();
                for(Playlist x:main.l2)
                {
                    main.listOfHistory.add(x);
                }
            }
            ObservableList<Playlist> temp = FXCollections.observableArrayList();
            for(Playlist pl:main.listOfPlaylist) {
                Playlist q=new Playlist(pl.getFilename(),pl.getFilepath());
                temp.add(q);
            }
            main.listOfPlaylist.clear();
            for (Playlist x:temp){main.listOfPlaylist.add(x);}
            temp.clear();

            for(Playlist pl:main.listOfHistory)
            {
                Playlist q=new Playlist(pl.getFilename(),pl.getFilepath());
                temp.add(q);
            }
            main.listOfHistory.clear();
            for (Playlist x:temp){main.listOfHistory.add(x);}
            temp.clear();
        }
        catch (Exception e){e.printStackTrace();}


        playlistTableCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("filename"));
        playlistTableCol2.setCellValueFactory(new PropertyValueFactory<Playlist, Button>("play"));
        playlistTableCol3.setCellValueFactory(new PropertyValueFactory<Playlist, Button>("remove"));
        playlistTableCol.setStyle("-fx-alignment: CENTER;");
        playlistTableCol2.setStyle("-fx-alignment: CENTER;");
        playlistTableCol3.setStyle("-fx-alignment: CENTER;");
        playlistTable.setItems(main.listOfPlaylist);


        historyTableCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("filename"));
        historyTableCol2.setCellValueFactory(new PropertyValueFactory<Playlist, Button>("play"));
        historyTableCol.setStyle("-fx-alignment: CENTER;");
        historyTableCol2.setStyle("-fx-alignment: CENTER;");
        tableHistory.setItems(main.listOfHistory);


        pane.setStyle("-fx-background-color: black;");
        play.setStyle("-fx-background-color: grey;");
        play.setShape(new Circle(2));
        updatePlaylist.setStyle("-fx-background-color: grey;");
        updatePlaylist.setShape(new Circle(2));
        clearHistory.setStyle("-fx-background-color: grey;");
        clearHistory.setShape(new Circle(2));
        clearPlaylist.setStyle("-fx-background-color: grey;");
        clearPlaylist.setShape(new Circle(2));
        startPlaylist.setStyle("-fx-background-color: grey;");
        startPlaylist.setShape(new Circle(2));
        imageSector.setImage(img);
        aboutTab.setStyle("-fx-background-color: aqua");
        playlistTab.setStyle("-fx-background-color: aqua");
        homeTab.setStyle("-fx-background-color: aqua");
        historyTab.setStyle("-fx-background-color: aqua");
        onlineTab.setStyle("-fx-background-color: aqua");
    }


    @FXML
    public void playButtonAction(ActionEvent event)
    {
        Stage stage2=new Stage();
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Media");
        File f = fc.showOpenDialog(stage2);
        if(f!=null) {
            String url = f.toURI().toASCIIString();
            Playlist pl = new Playlist(f.getName(), url);
            main.listOfHistory.add(pl);
            main.updateHistory();
            try {
                main.showPlayWindow(pl,url, f, stage2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
    @FXML
    public void playlistButtonAction(ActionEvent event)
    {
        Stage stage=new Stage();
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Media to Add to Playlist");
        List<File> list = fc.showOpenMultipleDialog(stage);
        if(list==null) return;
        for(File f:list)
        {
            String url = f.toURI().toASCIIString();
            Playlist pl=new Playlist(f.getName(),url);
            main.listOfPlaylist.add(pl);
        }
        main.updatePlaylist();
    }

    @FXML
    public void RunPlaylist()  {
        try {
            if(i>=main.listOfPlaylist.size()) {i=0;}//return;}
            Stage stageforplaylist = new Stage();
            Group root = new Group();
            Scene scene;
            //scene = new Scene(root, 600, 400);
            if (main.listOfPlaylist.get(i).getFilepath().endsWith("mp3")) {
                scene = new Scene(root, 600, 100);
            } else {
                scene = new Scene(root, 600, 400);
            }

            stageforplaylist.setScene(scene);
            stageforplaylist.sizeToScene();
            stageforplaylist.show();


            Media media = new Media(main.listOfPlaylist.get(i).getFilepath());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            stageforplaylist.setTitle(main.listOfPlaylist.get(i).getFilename());
            Controller cn=new Controller(mediaPlayer,main.listOfPlaylist.get(i),1,stageforplaylist);
            scene.setRoot(cn);
            cn.setMain(this.main);
            cn.setDfC(this);

            Playlist px=new Playlist(main.listOfPlaylist.get(i).getFilename(),main.listOfPlaylist.get(i).getFilepath());
            main.listOfHistory.add(px);
            main.updateHistory();

            cn.mp.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    i++;
                    stageforplaylist.close();
                    RunPlaylist();
                }
            });

            stageforplaylist.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                    public void handle(WindowEvent event) {
                        cn.mp.stop();
                    }
                });

            final DoubleProperty width = cn.getMediaView().fitWidthProperty();
            final DoubleProperty height = cn.getMediaView().fitHeightProperty();
            width.bind(Bindings.selectDouble(cn.getMediaView().sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(cn.getMediaView().sceneProperty(), "height"));

        }
        catch (Exception e){e.printStackTrace();}
    }

    @FXML
    public void historyClearAction(ActionEvent event)
    {
        main.listOfHistory.clear();
        main.updateHistory();
    }

    @FXML
    public void playlistClearAction(ActionEvent event)
    {
        main.listOfPlaylist.clear();
        main.updatePlaylist();
    }

    @FXML
    public void onlineStreamAction(ActionEvent event) throws Exception {
        if(downloadLink.getText().isEmpty()) return;
         try {
             Playlist p=new Playlist("fahim","guysgfuf");
             main.onlineStremaing(p, downloadLink.getText());
             Platform.runLater(new Runnable() {
                 @Override
                 public void run() {
                     downloadLink.clear();
                 }
             });
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
    }

    @FXML
    public void downloadAction(ActionEvent event) throws Exception {
        if(downloadLink.getText().isEmpty()) return;
        if(fileName.getText().isEmpty()) return;

        Thread download=new Thread()
        {
            public void run()
            {
                try {
                    String link = downloadLink.getText();
                    String name = fileName.getText();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            downloadLink.clear();
                            fileName.clear();
                        }
                    });
                    URL url = new URL(link);
                    System.out.println("Connecting");
                    main.downloadFromUrl(url, name);
                    System.out.println("downloaded");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        download.start();
    }
}


