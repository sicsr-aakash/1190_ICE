package com.siddharthbhatt.voice_recog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MyService extends Service {

    public static AudioManager mAudioManager;
    public SpeechRecognizer speech = null;
    public Intent recognizerIntent;
    public String LOG_TAG = "VoiceRecognitionActivity";
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    public MediaRecorder myAudioRecorder;
    public String outputFile = null;

    public boolean mIsListening;
    public volatile boolean mIsCountDownOn;
    public boolean mIsStreamSolo;
    public final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;

    public MyService() {
    }

    @Override
    public void onRebind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        speech.stopListening();
        stopAudioRecording();
        Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/emergency.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(new recogListener());

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE ,this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudioRecording();
        speech.stopListening();
        Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"Service Started", Toast.LENGTH_LONG).show();
        speech.startListening(recognizerIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public static class IncomingHandler extends Handler
    {
        private WeakReference<MyService> mtarget;

        IncomingHandler(MyService target)
        {
            mtarget = new WeakReference<MyService>(target);
        }

        @Override
        public void handleMessage(Message msg)
        {
            final MyService target = mtarget.get();

            switch (msg.what)
            {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        // turn off beep sound
                        if (!target.mIsStreamSolo)
                        {
                            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
                            target.mIsStreamSolo = true;
                        }
                    }
                    if (!target.mIsListening)
                    {
                        target.speech.startListening(target.recognizerIntent);
                        target.mIsListening = true;
                        //Log.d(TAG, "message start listening"); //$NON-NLS-1$
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    if (target.mIsStreamSolo)
                    {
                        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
                        target.mIsStreamSolo = false;
                    }
                    target.speech.cancel();
                    target.mIsListening = false;
                    //Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
                    break;
            }
        }

    }//IncomingHandler over

    // Count down timer for Jelly Bean work around
    public CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onFinish()
        {
            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try
            {
                mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {

            }
        }
    };

    public class recogListener implements RecognitionListener {

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true;
                mNoSpeechCountDown.start();

            }
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onError(int error) {

            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try
            {
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {
                Log.v("Error",e.getMessage());
            }

        }//method over

        @Override
        public void onResults(Bundle results) {

            ArrayList<String> lol = new ArrayList<String>();
            Set keyset = results.keySet();

            Iterator iterator = keyset.iterator();
            while(iterator.hasNext()){
                lol.add(results.getString(iterator.next().toString()));
            }
            process(lol);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

    }// recogListener class over

    public void process(ArrayList suggestedWords){

        Log.d("CapturedWord","Got a word");
        Iterator iterator = suggestedWords.iterator();
        /*
        while(iterator.hasNext()){
            Toast.makeText(getApplicationContext(), iterator.next().toString(), Toast.LENGTH_SHORT).show();
        }
        Log.d("CapturedWord","Done with iterator");
        */

        if(!suggestedWords.isEmpty()){
            Toast.makeText(getApplicationContext(), suggestedWords.size(), Toast.LENGTH_SHORT).show();
        }


        /*
        Uri callring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), callring);

        for (int i = 0; i < suggestedWords.size(); i++) {

            Log.d("CapturedWord","The word is :"+suggestedWords.get(i).toString());

            if(suggestedWords.get(i).toString().contains("hello")) {
                Toast.makeText(getApplicationContext(), "Hello to you too !", Toast.LENGTH_LONG).show();
            }else if(suggestedWords.get(i).toString().contains(sharedpreferences.getString("helpWord","help"))){

                makeCall();

                //send SMS if it is enabled
                if(Boolean.parseBoolean(sharedpreferences.getString("sendSMSBoolean","true"))) {
                    sendSMSMessage();
                }

                //start audio recording if it is enabled
                if(Boolean.parseBoolean(sharedpreferences.getString("recordAudioBoolean", "true"))) {
                    startAudioRecording();
                }

            }else if(suggestedWords.get(i).toString().contains("ring")){
                r.play();
            }else if(suggestedWords.get(i).toString().contains("stop")){
                r.stop();
            }

        }*/

    }//method over

    public void makeCall() {
        Log.i("Make call", "");

        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        String no = "tel:"+sharedpreferences.getString("localEmergency","");
        phoneIntent.setData(Uri.parse(no));
        try {
            startActivity(phoneIntent);
            //finish();
            Log.i("Finished making a call.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),
                    "Call failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }//makeCall() over

    public void sendSMSMessage() {
        Log.i("Send SMS", "");

        String phoneNo1 = sharedpreferences.getString("contactNumber1", "");
        String name1 = sharedpreferences.getString("ContactName1","");
        String phoneNo2 = sharedpreferences.getString("contactNumber2", "");
        String name2 = sharedpreferences.getString("ContactName2","");
        String str = "SMS sent to "+name1+" and "+name2;
        String message = sharedpreferences.getString("smsContent","I Need help urgently. This is an automated message.");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo1, null, message, null, null);
            smsManager.sendTextMessage(phoneNo2, null, message, null, null);
            Toast.makeText(getApplicationContext(),str,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }//sendSMSMessage() over

    public void startAudioRecording(){
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

    }//startAudioRecording() over

    public void stopAudioRecording(){
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;
        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                Toast.LENGTH_LONG).show();
    }//stopAudioRecording() over


}//MyService class over
