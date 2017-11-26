package com.tschuchort.soundshare.soundshare;

/**
 * Created by jvollmer on 26.11.2017.
 */

public class StatusProvider {


    private static StatusProvider instance;
    private boolean progressBlock;



    private boolean recordingBlock;

    public static synchronized StatusProvider getInstance(){
        if (instance == null){
            instance =  new StatusProvider();
        }
        return instance;
    }


    /**
     *  Konstruktor !
     */
    private StatusProvider() {

    }

    public void setProgressBlock(boolean progressBlock) {
        this.progressBlock = progressBlock;
    }
    public boolean getProgressBlock() {
        return progressBlock;
    }

    public void setRecordingBlock(boolean recordingBlock) {
        this.recordingBlock = recordingBlock;
    }

    public boolean getRecordingBlock() {
        return recordingBlock;

    }
}
