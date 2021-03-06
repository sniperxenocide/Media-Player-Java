package sample;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.SubtitleTrack;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller extends BorderPane{

    public void setMain(Main main) {
        this.main = main;
    }

    private Main main;

    public void setDfC(defaultControl dfC) {
        this.dfC = dfC;
    }
    private defaultControl dfC;

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    public MediaPlayer mp;

    public MediaView getMediaView() {
        return mediaView;
    }

    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;

    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;

    public Controller(final MediaPlayer mp,Playlist pl,int x,Stage stage) {
        this.mp = mp;
        setStyle("-fx-background-color: #bfc2c7;");
        mediaView = new MediaView(mp);

        StackPane mvPane = new StackPane();
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;");
        setCenter(mvPane);

        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        mvPane.getChildren().add(mediaBar);
        mediaBar.setAlignment(Pos.BOTTOM_CENTER);


        /*this.mp.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                System.out.println("yesss");
                defaultControl.i=main.listOfPlaylist.indexOf(pl)+1;
                System.out.println(dfC);
                dfC.RunPlaylist();
            }
        });*/

        final Button prevButton  = new Button("<<");
        prevButton.setShape(new Circle(2));
        prevButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(x==0) return;
                mp.stop();
                stage.close();
                if(Main.listOfPlaylist.indexOf(pl)==0) return;

                Playlist p=Main.listOfPlaylist.get(Main.listOfPlaylist.indexOf(pl) - 1);

                Playlist px=new Playlist(p.getFilename(),p.getFilepath());
                main.listOfHistory.add(px);
                main.updateHistory();

                try {
                    main.showPlayWindow(p,p.getFilepath(),p.getFilename());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mediaBar.getChildren().add(prevButton);

        mediaBar.getChildren().add(new Label("   "));

        final Button playButton  = new Button(">");
        playButton.setShape(new Circle(2));

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MediaPlayer.Status status = mp.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    mp.play();
                } else {
                    mp.pause();
                }
            }
        });


        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                    playButton.setText("||");
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setText(">");
            }
        });

        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if(x==0) return;
                stage.close();
                if(Main.listOfPlaylist.indexOf(pl)==Main.listOfPlaylist.size()-1) return;

                Playlist p=Main.listOfPlaylist.get(Main.listOfPlaylist.indexOf(pl) + 1);

                Playlist px=new Playlist(p.getFilename(),p.getFilepath());
                main.listOfHistory.add(px);
                main.updateHistory();
                try {
                    main.showPlayWindow(p,p.getFilepath(),p.getFilename());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        mediaBar.getChildren().add(playButton);

        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        final Button nextButton  = new Button(">>");
        nextButton.setShape(new Circle(2));
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(x==0) return;
                mp.stop();
                stage.close();
                if(Main.listOfPlaylist.indexOf(pl)==Main.listOfPlaylist.size()-1) return;

                Playlist p=Main.listOfPlaylist.get(Main.listOfPlaylist.indexOf(pl) + 1);

                Playlist px=new Playlist(p.getFilename(),p.getFilepath());
                main.listOfHistory.add(px);
                main.updateHistory();
                try {
                    main.showPlayWindow(p,p.getFilepath(),p.getFilename());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mediaBar.getChildren().add(nextButton);

        mediaBar.getChildren().add(new Label(" "));

// Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

// Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        mediaBar.getChildren().add(timeSlider);

// Add Play label
        playTime = new Label();
        //playTime.setPrefWidth(130);
        //playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);
        mediaBar.getChildren().add(new Label(" "));

// Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

// Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mp.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
        mediaBar.getChildren().add(volumeSlider);
        //setBottom(mediaBar);



    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mp.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mp.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}
