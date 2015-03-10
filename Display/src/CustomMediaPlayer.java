/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 03.03.15.
 */
public class CustomMediaPlayer implements MediaPlayerEventListener {

    private EmbeddedMediaPlayer mediaPlayer;

    private Canvas canvas;

    private String path;

    public CustomMediaPlayer(String videoPath){
        listeners = new ArrayList<>();

        path = videoPath;
        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        mediaPlayer.setVideoSurface(videoSurface);
        mediaPlayer.setRepeat(true);
    }

    public Canvas getCanvas(){
        return canvas;
    }

    private void secondPlay(){
        mediaPlayer.play();
        //mediaPlayer.start();
        mediaPlayer.addMediaPlayerEventListener(this);
    }

    public void play(){
        //mediaPlayer.stop();
        mediaPlayer.playMedia(path);
        //mediaPlayer.start();
        //mediaPlayer.addMediaPlayerEventListener(this);
    }

    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t, String s) {

    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {

    }

    @Override
    public void buffering(MediaPlayer mediaPlayer, float v) {

    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {

    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {

    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {

    }

    @Override
    public void forward(MediaPlayer mediaPlayer) {

    }

    @Override
    public void backward(MediaPlayer mediaPlayer) {

    }

    @Override
    public void finished(MediaPlayer player) {
        for (CustomMediaPlayerListener listener : listeners){
            listener.finishedPlaying();
        }
        mediaPlayer.removeMediaPlayerEventListener(this);
        secondPlay();
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long l) {

    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float v) {

    }

    @Override
    public void seekableChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void pausableChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void titleChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void snapshotTaken(MediaPlayer mediaPlayer, String s) {

    }

    @Override
    public void lengthChanged(MediaPlayer mediaPlayer, long l) {

    }

    @Override
    public void videoOutput(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void scrambledChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void elementaryStreamAdded(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void elementaryStreamSelected(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void error(MediaPlayer mediaPlayer) {

    }

    @Override
    public void mediaMetaChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

    }

    @Override
    public void mediaDurationChanged(MediaPlayer mediaPlayer, long l) {

    }

    @Override
    public void mediaParsedChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void mediaFreed(MediaPlayer mediaPlayer) {

    }

    @Override
    public void mediaStateChanged(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

    }

    @Override
    public void newMedia(MediaPlayer mediaPlayer) {

    }

    @Override
    public void subItemPlayed(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void subItemFinished(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void endOfSubItems(MediaPlayer mediaPlayer) {

    }

    private List<CustomMediaPlayerListener> listeners;

    public void addCustomMediaPlayerListener(CustomMediaPlayerListener listener){
        listeners.add(listener);
    }

    public void removeCustomMediaPlayerListener(CustomMediaPlayerListener listener){
        listeners.remove(listener);
    }

    public interface CustomMediaPlayerListener {
        public void finishedPlaying();
    }
}
