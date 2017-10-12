package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Main extends Application {

    public static String playlist="SaveState/playlist.txt";
    public static String history="SaveState/history.txt";

    public static ObservableList<Playlist> listOfPlaylist = FXCollections.observableArrayList();
    public static ObservableList<Playlist> listOfHistory = FXCollections.observableArrayList();

    public static ArrayList<Playlist> l1=new ArrayList<>();
    public static ArrayList<Playlist> l2=new ArrayList<>();

    private Stage stage;
    private defaultControl DC;


    /*public String defaultUrl="F:/BUET/PROJECTS/2-1 on JAVA/making mediaPlayer/pkgMediaPlayer/SaveState/let it go(female).mp3";
    File fl=new File(defaultUrl);*/


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        stage=primaryStage;
        showDefaultWindow();
    }

    public void showDefaultWindow() throws Exception
    {
        /*
        defaultUrl=fl.toURI().toASCIIString();
        Media m=new Media(defaultUrl);
        MediaPlayer mp=new MediaPlayer(m);
        */

        FXMLLoader loader= new FXMLLoader();
        loader.setLocation(getClass().getResource("defaultPage.fxml"));
        Parent root=loader.load();

        defaultControl cnt=loader.getController();
        cnt.setMain(this);
        this.DC=cnt;

        Scene scene=new Scene(root, 600, 400);
        stage.setTitle("Xtreme Player");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();

        //mp.play();
    }

    public void showPlayWindow(Playlist pl,String url,File f,Stage stage2) throws Exception  //generates from pressing single media playing button
    {
        try {
            stage2.setTitle(f.getName());
            Group root = new Group();
            Scene scene;
            if(url.endsWith("mp3")){scene = new Scene(root,600, 100);}
            else {scene = new Scene(root,600, 400);}


            Media media = new Media(url);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            Controller cn = new Controller(mediaPlayer,pl,0,stage2);
            scene.setRoot(cn);
            cn.setMain(this);
            cn.setDfC(DC);

            stage2.setScene(scene);
            stage2.sizeToScene();
            stage2.show();
            scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage2.setFullScreen(true);
                }
            });
            stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
        catch (Exception e){}
    }

    public void showPlayWindow(Playlist pl,String url,String name) throws Exception
    {
        try {
            Stage stageforplaylist=new Stage();
            stageforplaylist.setTitle(name);
            Group root = new Group();
            Scene scene ;
            if(url.endsWith("mp3")){scene = new Scene(root,600, 100);}
            else {scene = new Scene(root,600, 400);}

            Media media = new Media(url);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            Controller cn = new Controller(mediaPlayer,pl,1,stageforplaylist);
            scene.setRoot(cn);
            cn.setMain(this);
            cn.setDfC(DC);

            stageforplaylist.setScene(scene);
            stageforplaylist.sizeToScene();
            stageforplaylist.show();
            scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stageforplaylist.setFullScreen(true);
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
        catch (Exception e){}
    }


    void downloadFromUrl(URL url, String localFilename) throws Exception {
        InputStream is = null;
        FileOutputStream fos = null;
        String destination="downloads/"+localFilename;

        try {
            URLConnection urlConn = url.openConnection();//connect

            System.out.println("File Size: "+urlConn.getContentLength()/1024);

            System.out.println(urlConn.getContentType().split("/")[urlConn.getContentType().split("/").length-1]);

            int size=urlConn.getContentLength();

            destination+="."+urlConn.getContentType().split("/")[urlConn.getContentType().split("/").length-1];
            is = urlConn.getInputStream();//get connection inputstream
            fos = new FileOutputStream(destination);   //open outputstream to local file

            File isf=new File(destination);

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                System.out.println("downloaded: " + isf.length() / 1024 + "KB  of " + size / 1024 + "KB " + (isf.length() * 100) / size + "%");
                double p=((isf.length() * 100) / size)/100.00;
                DC.downloadProgress.setProgress(p);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        DC.progress.setText(String.valueOf(((isf.length() * 100) / size)) + "%");
                        DC.dp1.setText(String.valueOf(isf.length() / 1024)+"KB  OF  "+String.valueOf(size/1024)+" KB");
                    }
                });
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }


    public void onlineStremaing(Playlist pl,String url) throws Exception
    {
        try {
            Stage stageforplaylist=new Stage();
            stageforplaylist.setTitle("Online Stream");
            Group root = new Group();
            Scene scene=new  Scene(root,600, 400);;


            Media media = new Media(url);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            Controller cn = new Controller(mediaPlayer,pl,0,stageforplaylist);
            scene.setRoot(cn);
            cn.setMain(this);
            cn.setDfC(DC);

            stageforplaylist.setScene(scene);
            stageforplaylist.sizeToScene();
            stageforplaylist.show();
            scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stageforplaylist.setFullScreen(true);
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
        catch (Exception e){}
    }



    public static void updatePlaylist()
    {
        try
        {
            l1.clear();
            for(Playlist x:listOfPlaylist)
            {
                l1.add(x);
            }
            File p=new File(playlist);
            FileOutputStream Pfos=new FileOutputStream(p);
            ObjectOutputStream Poos=new ObjectOutputStream(Pfos);
            //Object o=l1;
            //new PrintWriter(playlist).close();
            Poos.writeObject(l1);
            Poos.flush();
            Poos.close();
        }
        catch (Exception e){e.printStackTrace();}
    }

    public static void updateHistory()
    {
        try
        {
            l2.clear();
            for(Playlist x:listOfHistory)
            {
                l2.add(x);
            }
            File h=new File(history);
            FileOutputStream Hfos=new FileOutputStream(h);
            ObjectOutputStream Hoos=new ObjectOutputStream(Hfos);
            //new PrintWriter(history).close();
            //Object o=l2;
            Hoos.writeObject(l2);
            Hoos.flush();
            Hoos.close();
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void stop(){System.exit(0);}

    public static void main(String[] args)
    {
        launch(args);
    }
}
