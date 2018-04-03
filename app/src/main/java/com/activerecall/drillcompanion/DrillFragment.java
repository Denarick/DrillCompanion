package com.activerecall.drillcompanion;


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrillFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    private ArrayList<Technique> drillTechniques;
    private int position = 0;
    private TextToSpeech textToSpeech;

    public DrillFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        drillTechniques = TechniqueSet.SohnPpaeKi.getOrderedTechniques();

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_drill, container, false);

        // Set button listen event
        layout.findViewById(R.id.next_button).setOnClickListener(this);

        // Set up text to speech
        textToSpeech = new TextToSpeech(layout.getContext(),this);

        return layout;

    }

    @Override
    public void onStart(){
        super.onStart();
        if(drillTechniques != null){
            updateTechniqueNameView(drillTechniques.get(position).getWrittenName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_button:
                onClickNext();
                break;
        }
    }

    private void onClickNext() {
        Button button = getView().findViewById(R.id.next_button);
        if(button.getText() != getString(R.string.start_drill)) {
            position = (++position) % drillTechniques.size();
        }

        if (position >= drillTechniques.size() - 1) {
            button.setText(R.string.restart_button);
            sayTechnique();
            drillTechniques = TechniqueSet.SohnPpaeKi.getRandomTechniques();
        } else {
            button.setText(R.string.next_technique);
            sayTechnique();
        }
    }

    private void sayTechnique() {
        Technique tempTech = drillTechniques.get(position);
        updateTechniqueNameView(tempTech.getWrittenName());

        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

        HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
        stringStringHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, tempTech.getPhoneticName());
        textToSpeech.speak(tempTech.getPhoneticName().toString(), TextToSpeech.QUEUE_ADD, stringStringHashMap);
    }

    private void updateTechniqueNameView(String name){
        View view = getView();
        if(view != null) {
            TextView techniqueName = view.findViewById(R.id.technique_name);
            techniqueName.setText(name);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    // Log.d("MainActivity", "TTS finished");
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });
        }
    }

    @Override
    public void onDestroy(){
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
}
