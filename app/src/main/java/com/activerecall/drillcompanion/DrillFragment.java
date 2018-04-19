package com.activerecall.drillcompanion;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrillFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    // private variables
    private static final String TAG = "DrillFragment";
    private ArrayList<Technique> drillTechniques;
    private int position = 0;
    private TextToSpeech textToSpeech;
    private CountDownTimer countDownTimer = null;
    private long timerMilliseconds = 5000;
    private long millisLeft;
    private boolean useTimer = true;
    private boolean timerRunning = false;
    private boolean hasStarted = false;

    // public "extra" tags
    public String TECHNIQUE_ARRAY = "technique_array";
    public String TIMER_MILLISECONDS = "milliseconds";
    public String USE_TIMER = "use_timer";

    //methods
    public DrillFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        drillTechniques = TechniqueSet.SohnPpaeKi.getOrderedTechniques();

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_drill, container, false);

        // Set button listen event
        layout.findViewById(R.id.next_button).setOnClickListener(this);
        layout.findViewById(R.id.pause_button).setOnClickListener(this);

        // Set up text to speech
        textToSpeech = new TextToSpeech(layout.getContext(),this);

        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //load saved instance state
        if(savedInstanceState != null) {
            timerMilliseconds = savedInstanceState.getLong(TIMER_MILLISECONDS, 0);
            millisLeft = savedInstanceState.getLong("millisLeft",0);
            useTimer = savedInstanceState.getBoolean(USE_TIMER, timerMilliseconds > 0);
            timerRunning = savedInstanceState.getBoolean("timerRunning", false);
            hasStarted = savedInstanceState.getBoolean("hasStarted", false);
            position = savedInstanceState.getInt("position", 0);
            drillTechniques = savedInstanceState.getParcelableArrayList(TECHNIQUE_ARRAY);
        }
    }

    private void setUpView() {
        updateTechniqueTextView();
        updateNextButton();
        if(hasStarted && useTimer){
            getView().findViewById(R.id.pause_button).setVisibility(View.VISIBLE);
            if(!timerRunning) {
                ((Button)getView().findViewById(R.id.pause_button)).setText(R.string.resume);
            }
            setTimerText(millisLeft);
        } else {
            getView().findViewById(R.id.pause_button).setVisibility(View.INVISIBLE);
        }

        if(timerRunning) {
            startTimer(millisLeft);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.i(TAG, "Saved Instance State save start");

        super.onSaveInstanceState(savedInstanceState);

        try {
            savedInstanceState.putLong(TIMER_MILLISECONDS, timerMilliseconds);
            savedInstanceState.putLong("millisLeft", millisLeft);
            savedInstanceState.putBoolean(USE_TIMER, useTimer);
            savedInstanceState.putBoolean("timerRunning", timerRunning);
            savedInstanceState.putBoolean("hasStarted", hasStarted);
            savedInstanceState.putInt("position", position);
            savedInstanceState.putParcelableArrayList(TECHNIQUE_ARRAY, drillTechniques);
        } catch (Exception e){
            Log.e(TAG, "Error saving Instance State");
        }

        Log.i(TAG, "Saved Instance State save complete");

    }

    @Override
    public void onStart(){
        super.onStart();
        setUpView();
    }

    private void updateTechniqueTextView() {
        if(drillTechniques != null
                && hasStarted){
            setTechniqueNameView(drillTechniques.get(position).getWrittenName());
        } else {
            setTechniqueNameView(getString(R.string.ready));
        }
    }

    private void setTechniqueNameView(String name){
        View view = getView();
        if(view != null) {
            TextView techniqueName = view.findViewById(R.id.technique_name);
            techniqueName.setText(name);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_button:
                nextTechnique();
                break;
            case R.id.pause_button:
                onClickPausePlay();
                break;
        }
    }

    private void onClickPausePlay() {

        Button button = getView().findViewById(R.id.pause_button);
        if(hasStarted && useTimer) {
            //Pause Button Click
            if(timerRunning){
                button.setText(getString(R.string.resume));
                if(textToSpeech != null && textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                }
                if(countDownTimer != null) {
                    countDownTimer.cancel();
                }
                timerRunning = false;
            }
            //Resume Button Click
            else {
                button.setText(getString(R.string.pause));
                setTimerText(timerMilliseconds);
                sayTechnique();
            }
        }
    }

    private void updatePausePlayButtonText(){
        Button button = getView().findViewById(R.id.pause_button);
    }

    private void nextTechnique() {

        if(hasStarted) {
            position = (++position) % drillTechniques.size();

            if(position == 0){
                drillTechniques = TechniqueSet.SohnPpaeKi.getRandomTechniques();
            }
        } else {
            hasStarted = true;
            getView().findViewById(R.id.pause_button).setVisibility(View.VISIBLE);
        }

        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        Button pauseButton = getView().findViewById(R.id.pause_button);
        pauseButton.setText(getString(R.string.pause));

        updateNextButton();
        if(useTimer) {
            setTimerText(timerMilliseconds);
        }
        sayTechnique();
    }

    private void updateNextButton() {
        Button button = getView().findViewById(R.id.next_button);

        if(!hasStarted){
            button.setText(R.string.start_drill);
        } else if(position >= drillTechniques.size() - 1) {
            button.setText(R.string.restart_button);
        } else {
            button.setText(R.string.next_technique);
        }
    }

    private void setTimerText(long milliseconds) {
        TextView time_view = getView().findViewById(R.id.time_view);
        time_view.setText(String.valueOf(milliseconds / 1000));
    }

    private void sayTechnique() {
        Technique tempTech = drillTechniques.get(position);
        updateTechniqueTextView();

      /*  HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sayTechnique");
        textToSpeech.speak(tempTech.getPronunciation(), TextToSpeech.QUEUE_FLUSH, stringStringHashMap);*/

        Bundle bundle = new Bundle();
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sayTechnique");
        textToSpeech.speak(tempTech.getPronunciation(), TextToSpeech.QUEUE_FLUSH, bundle, "sayTechnique");
    }

    private void startTimer() {
        startTimer(timerMilliseconds);
    }

    private void startTimer(final long milliseconds)
    {
        Log.v("StartTimer", "Countdown timer reached");
        timerRunning = true;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(milliseconds, 250) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.i(TAG, "Milliseconds left: " + millisUntilFinished/1000);
                        millisLeft = millisUntilFinished;
                        setTimerText(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        Log.i(TAG, "Next Technique");
                        millisLeft = 0;
                        nextTechnique();
                        timerRunning = false;
                    }
                }.start();
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = textToSpeech.getLanguage();
            textToSpeech.setLanguage(locale);

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    if(useTimer) {
                        startTimer();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onStop(String utteranceId, boolean interrupted){
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });
        } else if (status == TextToSpeech.ERROR){
            Toast.makeText(getContext(),"Text to speech not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
