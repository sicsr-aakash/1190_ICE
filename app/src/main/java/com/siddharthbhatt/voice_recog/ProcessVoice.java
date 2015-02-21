package com.siddharthbhatt.voice_recog;

/**
 * Created by siddharthbhatt on 10/02/15.
 */
public class ProcessVoice {

    private String EmergencyWord;
    private long[] EmegencyContact;

    public void setEmergencyWord(String a){
        this.EmergencyWord = a;
    }

    public String getEmergencyWord(){
        return this.EmergencyWord;
    }

    public void setEmegencyContact(long[] arr){
        this.EmegencyContact = arr;
    }

    public long[] getEmegencyContact(){
        return this.EmegencyContact;
    }

    private static ProcessVoice ourInstance = new ProcessVoice();

    public static ProcessVoice getInstance() {
        return ourInstance;
    }

    private ProcessVoice() {
    }
}
