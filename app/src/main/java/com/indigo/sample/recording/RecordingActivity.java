package com.indigo.sample.recording;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tschuchort.soundshare.soundshare.R;


/**
 * Created by jvollmer on 26.11.2017.
 */

public class RecordingActivity extends AppCompatActivity implements RecorderListener{

    private static final String TAG = RecordingActivity.class.getSimpleName();
    private StatusProvider status;
    private LogicHandler logicHandler;
    private Button record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        status = StatusProvider.getInstance();
        logicHandler = LogicHandler.getInstance(this);
        logicHandler.setListener(this);

        record = findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v (TAG, "onClick");
                if(!status.getProgressBlock()) {
                    status.setProgressBlock(true);

                    // start Recording
                    logicHandler.startRecording();
                    record.setPressed(true);

                }else{
                    // stop Recording
                    if(status.getRecordingBlock()){
                        // start Recording
                        logicHandler.stopRecording();
                    }
                }
            }
        });
    }


    @Override
    public void notifyRecordingFinished() {
        Log.i(TAG, TAG+"notifyRecordingFinished ");
        status.setRecordingBlock(false);


        
        // update ViewStatus
        record.setPressed(false);
    }
}
