package com.siddharthbhatt.voice_recog;

import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity{
    //voice recognition and general variables

    //variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;

    //ListView for displaying suggested words
    private ListView wordList;

    //Log tag for output information
    private final String LOG_TAG = "SpeechRepeatActivity";//***enter your own tag here***

    //TTS variables

    //variable for checking TTS engine data on user device
    private int MY_DATA_CHECK_CODE = 0;

    //Text To Speech instance
    private TextToSpeech repeatTTS;

    private Button settingButton;
    private Button voice_button;
    private TextView textView;

    /**
     * Instruct the app to listen for user speech input
     */
    private void listenToSpeech() {

        //start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //message to display while listening
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
        //set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

        //start listening
        startActivityForResult(listenIntent, VR_REQUEST);
    }

    /**
     * onActivityResults handles:
     *  - retrieving results of speech recognition listening
     *  - retrieving result of TTS data check
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check speech recognition result
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK)
        {
            //store the returned word list as an ArrayList
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //set the retrieved list to display in the ListView using an ArrayAdapter
            wordList.setAdapter(new ArrayAdapter<String>(this, R.layout.word, suggestedWords));
            //call the function to process words
            process(suggestedWords);
        }

        //tss code here

        //call superclass method
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingButton = (Button) findViewById(R.id.settingsButton);
        textView = (TextView) findViewById(R.id.textView);
        voice_button = (Button) findViewById(R.id.voice_button);
        wordList = (ListView) findViewById(R.id.word_list);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        //find out whether speech recognition is supported
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
            //speech recognition is supported - detect user button clicks
            voice_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //listen for results
                    listenToSpeech();
                }
            });
        }
        else
        {
            //speech recognition not supported, disable button and output message
            voice_button.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }
    }

    public void process(ArrayList suggestedWords){


        for (int i = 0; i < suggestedWords.size(); i++) {

            if(suggestedWords.get(i).toString().contains("hello")) {
                Toast.makeText(this, "Hello to you too !", Toast.LENGTH_LONG).show();
            }else if(suggestedWords.get(i).toString().contains("help")){
                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:" + "*121#"));
                startActivity(dial);
            }else if(suggestedWords.get(i).toString().contains("ring")){
                Uri callring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), callring);
                r.play();
            }

        }

    }//method over


}//class over
