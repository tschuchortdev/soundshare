package com.tschuchort.soundshare.soundshare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jvollmer on 26.11.2017.
 */

public class Player extends Thread {


    private static final String TAG = Player.class.getSimpleName();

    private int NATIVESAMPLERATE = 44100; // native SampleRate
    private int BIT_DEPTH_CODE = AudioFormat.ENCODING_PCM_16BIT;
    private int CHANNEL_CODE = AudioFormat.CHANNEL_OUT_MONO;
    private final int MONOFRAMESIZEINBYTES 			= 2;	 // 2 Bytes für ein Sample da nur in Mono aufgenommen wird
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final int MINIMALLOOPFRAMES = 44100; // one sec = 44100frames
    private static final long RECORDING_TIME_LIMIT = 6000; // Millisec
    private static final int SAMPLESIZEINBYTES = 2;
    private byte[] soundDataArray_B1;
    private PlayerListener listener;
    private short[] soundDataArray_S1;
    private AudioTrack audioTrack;

    public void startAfterRecording(ByteArrayOutputStream baos){
        soundDataArray_B1 = baos.toByteArray();	//alles im array[] speichern ;
        super.start();
    }

    @Override
    public void run(){
        runAfterRec();
    }

    private void runAfterRec(){
        Log.i(TAG, TAG+" runAfterRec() is called ");
        CHANNEL_CODE = AudioFormat.CHANNEL_OUT_MONO;
        soundDataArray_S1 = new short[soundDataArray_B1.length / SAMPLESIZEINBYTES];
        ByteBuffer.wrap(soundDataArray_B1).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(soundDataArray_S1);
        soundDataArray_B1 = null;
        initAudioTracks();
    }

    private void initAudioTracks() {
        try{
            if(soundDataArray_S1 != null){
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, NATIVESAMPLERATE,
                        CHANNEL_CODE , BIT_DEPTH_CODE,
                        (soundDataArray_S1.length) * SAMPLESIZEINBYTES , AudioTrack.MODE_STATIC);
            }
        }
        catch(Exception e){
            Log.e(TAG, TAG+" initAudioTrack() new AudioTrack()" +" ERROR");
        }
        listener.notifyOfInitComplete();
    }
    
    public void playNow(){
        Log.i(TAG, TAG + " playNow() ");
        
        //Zur Absicherung gegen GarbageCollector wenn Activity lost focus
        if(audioTrack == null ){
            Log.e(TAG, TAG+"  ERROR 1 ");            initAudioTracks();
        }
        else if(audioTrack.getState() != AudioTrack.STATE_INITIALIZED){
            Log.e(TAG, TAG+"  ERROR 2 ");            initAudioTracks();
        }

        if(audioTrack != null && audioTrack.getPlayState()!= AudioTrack.PLAYSTATE_PAUSED) {
            audioTrack.reloadStaticData();
            audioTrack.play();
        }else{
            Log.e(TAG, TAG+"  ERROR 3 ");
        }

    }



    public void stopNow() {
        Log.i(TAG, TAG + " stopNow() ");

        //Zur Absicherung gegen GarbageCollector wenn Activity lost focus
        if (audioTrack == null) {
            if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                audioTrack.stop();
            }
        }
    }



    public void setListener(LogicHandler listener) {
        this.listener = listener;

    }
}
