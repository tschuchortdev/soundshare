package com.indigo.sample.recording;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by jvollmer on 26.11.2017.
 */

public class RecorderThread extends Thread {


    private static final String TAG = RecorderThread.class.getSimpleName();


    private final Context context;
    private final StatusProvider status;
    private int NATIVESAMPLERATE = 44100; // native SampleRate
    private int BIT_DEPTH_CODE = AudioFormat.ENCODING_PCM_16BIT;
    private int CHANNEL_CODE = AudioFormat.CHANNEL_OUT_MONO;
    private final int MONOFRAMESIZEINBYTES 			= 2;	 // 2 Bytes für ein Sample da nur in Mono aufgenommen wird
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final int MINIMALLOOPFRAMES = 44100; // one sec = 44100frames
    private static final long RECORDING_TIME_LIMIT = 6000; // Millisec

    private final AudioManager audioManager;
    private AudioRecord audioRecord;
    private int bufferSizeIn;
    private byte[] transferBufferIn;

    private RecorderListener listener;
    private boolean stopRec;
    private boolean stopRecPressed;
    private boolean isRecording;
    private ByteArrayOutputStream baos;
    private int index;
    private int audioSizeTempLong;
    private int len;
    private long recordStartTime;

    /**
     *  Konstruktor  !
     */
    public RecorderThread(Context context){
        this.context= context;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        status = StatusProvider.getInstance();
        Log.i(TAG , TAG+" : super.start() OK! ");
    }

    @Override
    public synchronized void start() {
        startAudioRecording();
        super.start();
        
    }

    /**
     * Stops releases the Recorder, and gets a new one
     */
    private void startAudioRecording(){
        Log.i(TAG , TAG+" : startAudioRecording()" );
        try{

            // Android AudioRecorder is VERY picky! try release
            if (audioRecord != null && audioRecord.getState() != AudioRecord.STATE_UNINITIALIZED) {
                Log.i(TAG , TAG+" : audioRecord was initialized before" );
                if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                    audioRecord.stop();
                }

                try {
                    sleep(30);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "ERROR 1");
                }

                try{
                    Log.i(TAG , TAG+" : AudioRecord: try: release Recorder --->" );
                    audioRecord.release();
                    Log.i(TAG , TAG+" : AudioRecord: release Recorder = Ok!" );
                }
                catch(Exception e){
                    Log.e(TAG , TAG+ " ERROR" +" : AudioRecord: release Recorder failed!" );
                    e.printStackTrace();}
            }

            bufferSizeIn = AudioRecord.getMinBufferSize(
                    NATIVESAMPLERATE ,
                    CHANNEL_CODE,
                    ENCODING
                    );
            
            try{
				//try init new
                Log.i(TAG , TAG+" : AudioRecord: try: get new Recorder --->" );
                audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        NATIVESAMPLERATE,
                        CHANNEL_CODE,
                        ENCODING,
                        bufferSizeIn
                );
                Log.i(TAG , TAG+" : AudioRecord: get new Recorder = OK! " );
            }catch(Exception e){
                Log.e(TAG , TAG+ " ERROR" +" : AudioRecord: new AudioRecord failed!" );
                e.printStackTrace();}

            transferBufferIn = new byte[bufferSizeIn];


        }catch(Exception e){
            Log.e(TAG , TAG+ " ERROR" + " Init AudioRecorder failed!" );
        }

        try{
            Log.i(TAG , TAG+" AudioRecord: try: start recording --->" );
            audioRecord.startRecording();
            Log.i(TAG , TAG+"AudioRecord: start recording = OK!" );
        }catch(Exception e){
            Log.e(TAG , TAG+ " ERROR" +" : AudioRecord startRecording " + "failed!" );
            e.printStackTrace();
        }

        Log.i(TAG , TAG+" startAudioRecording() OK" );

    }

    @Override
    public void run(){
        Log.i(TAG , TAG+" : run()" );

        status.setRecordingBlock(true);
        recordStartTime = SystemClock.uptimeMillis();
        stopRec = false;
        stopRecPressed = false;
        isRecording = true;
        baos = new ByteArrayOutputStream();
        index = 0;
        try{

            while (stopRec == false)  // read from system in transferBufferIn
            {

                len = audioRecord.read(transferBufferIn, 0, bufferSizeIn);  //hier wird Zeit benötigt, bei großem Buffer bis zu 400mS!!!
                index++;
                audioSizeTempLong = bufferSizeIn * index;

                if(baos!=null){
                    // transferBuffer reicht bis VOR den Zeitpunkt des Rec-Buttons !!! 
                    // write to output stream
                    baos.write(transferBufferIn);
                    // give a Chance to skip to other Threads :
                    try{
                        Thread.sleep(2);       // This was good for quite a while!!!
                    }catch(InterruptedException interruptedException){
                        interruptedException.printStackTrace();
                        Log.e(TAG , TAG+" ERROR: AudioRecord  Thread.sleep() " + "failed!" );}
                }

                if (baos!= null && stopRecPressed == true){

                    if (audioSizeTempLong >= MINIMALLOOPFRAMES * 2 ){ 		// 2 bytes == 1frame
                        Log.i(TAG , TAG+" : stop Rec !!! at bufferindex " +index+" buffersize "+bufferSizeIn+ " audioSizeTempLong "+audioSizeTempLong);
                        Log.i(TAG , TAG+" : stop FIRST Rec !!!" );
                        stopRec = true;
                    }
                }

                if (baos!= null && index % 40 == 0 ){

                    if (SystemClock.uptimeMillis() - recordStartTime > RECORDING_TIME_LIMIT  ){
                        stopRec = true;
                        Log.v(TAG , TAG+" ++++++++++++++++++++ Recording time is BIG --> stopRecording +++++++++++++" );
                        Log.v(TAG , TAG+" +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
                    }

                }

            }//End of while Schleife


            Log.v(TAG , TAG+" +++++++++++++++++++++++  End of while loop  ++++++++++++++++++++++++++++" );
            listener.notifyRecordingFinished();  // notify after stopRecording();

        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG , TAG+" ERROR: AudioRecord recorder.read " + "failed!" );}

    }



    public void stopRecording() {
        Log.i(TAG , TAG+" : stopRecording()" );


    }


    public void setListener(LogicHandler listener) {
        this.listener = listener;
    }


    public ByteArrayOutputStream getByteArrayOutputStream() {
        return baos;
    }

}
