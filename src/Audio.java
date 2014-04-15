/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.sound.sampled.*;
import java.io.File;

/**
 * Created by forando on 08.04.14.
 */
public class Audio {
    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    DataLine.Info dataLineInfo;
    boolean stopPlayback = false;
    boolean playbackFinished = true;
    String path;

    public Audio(String path) {
        this.path = path;
    }

    public void Play(){
        if (playbackFinished) {
            try {
                ResourceFile rf = new ResourceFile(this.path);
                File soundFile = rf.getFile();
                //URL myURL = ClassLoader.getSystemResource("resources/notify.wav");
                //System.out.println(myURL);
                //File soundFile = new File(myURL.getPath());
                //audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                audioFormat = audioInputStream.getFormat();
                System.out.println(audioFormat);
                dataLineInfo =new DataLine.Info(SourceDataLine.class,audioFormat);
                sourceDataLine =(SourceDataLine) AudioSystem.getLine(dataLineInfo);
                //Create a thread to Play back the variables and
                // start it running.  It will run until the
                // end of file, or the Stop button is
                // clicked, whichever occurs first.
                // Because of the variables buffers involved,
                // there will normally be a delay between
                // the click on the Stop button and the
                // actual termination of playback.
                new PlayThread().start();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }//end catch
        }
    }

    //Inner class to Play back the variables from the
// notificationSound file.
    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[10000];
        int readFromInputStream;


        public void run() {
            playbackFinished = false;
            try {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                //Keep looping until the input read method
                // returns -1 for empty stream or the
                // user clicks the Stop button causing
                // stopPlayback to switch from false to
                // true.
                while ((readFromInputStream = audioInputStream.read(
                        tempBuffer, 0, tempBuffer.length)) != -1
                        && !stopPlayback) {
                    if (readFromInputStream > 0) {
                        //Write variables to the internal buffer of
                        // the variables line where it will be
                        // delivered to the speaker.
                        sourceDataLine.write(
                                tempBuffer, 0, readFromInputStream);
                    }//end if
                }//end while
                //Block and wait for internal buffer of the
                // variables line to empty.
                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }//end catch

            playbackFinished = true;

            //Prepare to playback another file
            // stopBtn.setEnabled(false);
            //playBtn.setEnabled(true);
            stopPlayback = false;
        }//end run
    }//end inner class PlayThread

    public void Stop(){
        stopPlayback = true;
    }

    public void Reset(){
        stopPlayback = true;
    }
}
