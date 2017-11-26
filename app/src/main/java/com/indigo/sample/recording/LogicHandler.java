package com.indigo.sample.recording;

import android.content.Context;
import android.util.Log;

/**
 * Created by jvollmer on 26.11.2017.
 */

public class LogicHandler implements RecorderListener, PlayerListener{


    private static LogicHandler instance;
    private final Context context;
    private static final String TAG = LogicHandler.class.getSimpleName();
    private final StatusProvider status;
    private RecorderThread rec;
    private RecorderListener listener;
    private Player player;

    /**
     * Konstruktor
     * @param context
     */
    private LogicHandler(Context context){
        this.context= context;
        Log.i(TAG, TAG+"contruction ");
        status = StatusProvider.getInstance();
    }

    /**
     * @return instance of this Handler
     */
    public static LogicHandler getInstance(Context context){
        if (instance == null){
            instance =  new LogicHandler(context);
        }
        return instance;
    }

    public void startRecording() {
        Log.i(TAG, TAG+"startRecording ");
        rec = new RecorderThread(context);
        rec.setListener(this); // add ourselves as a listener
        rec.start();

    }

    public void stopRecording() {
        Log.i(TAG, TAG+"stopRecording ");
        rec.stopRecording();
    }

    @Override
    public void notifyRecordingFinished() {
        Log.i(TAG, TAG+"notifyRecordingFinished ");

        // save Audio
        player = new Player();
        player.setListener(instance);
        player.startAfterRecording(rec.getByteArrayOutputStream());
        listener.notifyRecordingFinished();


    }

    public void setListener(RecorderListener listener) {
        this.listener = listener;
    }

    @Override
    public void notifyOfInitComplete() {
        player.playNow();
    }

    public void stopPlaying() {
        Log.i(TAG, TAG+"stopPlaying ");
        player.stopNow();
    }
}
