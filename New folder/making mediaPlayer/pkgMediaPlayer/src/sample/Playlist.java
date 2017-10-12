package sample;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import java.io.Serializable;

public class Playlist implements Serializable{

    //private StringProperty filename;
    //private StringProperty filepath;
    private String filename;
    private String filepath;
    private transient Button play;
    private transient Button remove;
    private Playlist pl;

    Playlist(String name,String path)
    {
        filename=name;
        filepath=path;
        play=new Button(">");
        remove=new Button("X");
        pl=this;

        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                Main main=new Main();
                Playlist ple=new Playlist(name,path);
                Main.listOfHistory.add(ple);
                Main.updateHistory();
                try {
                    main.showPlayWindow(pl,pl.getFilepath(),pl.getFilename());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.listOfPlaylist.remove(pl);
                Main.updatePlaylist();
            }
        });
    }

    public void setFilename(String name)
    {
        filename=name;
    }

    public void setFilepath(String path)
    {
        filepath=path;
    }
    public void setPlay(String n)
    {
        play.setText(n);
    }

    public void setRemove(String n)
    {
        remove.setText(n);
    }



    public String getFilename()
    {
        return this.filename;
    }

    public String getFilepath()
    {
        return this.filepath;
    }

    public Button getPlay()
    {
        return this.play;
    }

    public Button getRemove()
    {
        return this.remove;
    }
}
