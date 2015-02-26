package com.siddharthbhatt.voice_recog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




public class Settings extends Activity {

    private EditText localEmergency;
    private EditText smsbox;
    private CheckBox recordAudio;
    private CheckBox sendSMScheckBox;
    private TextView contactName1;
    private TextView contactName2;
    private TextView serviceStatus;
    private Button contactPicker1;
    private Button contactPicker2;
    private Button serviceToggle;
    private Button saveButton;

    private Boolean recordAudioBoolean = true;
    private Boolean sendSMSBoolean = true;
    private String LocalEmergency="100";
    private String smsContent="I Need help urgently. This is an automated message.";
    private String ContactName1="Not Selected";
    private String ContactName2="Not Selected";
    private String contactNumber1="";
    private String contactNumber2="";
    private String servicestatus="Stopped";

    private String one;
    private String name;
    private String phoneNumber;
    private String contactId;
    private static final String MyPREFERENCES = "MyPrefs" ;
    public static final int PICK_CONTACT = 1;
    SharedPreferences sharedpreferences;


    protected void setAssests(){

        //locate and fix assets
        localEmergency = (EditText) findViewById(R.id.localEmergencyNumber);
        smsbox = (EditText) findViewById(R.id.smsBox);
        recordAudio = (CheckBox) findViewById(R.id.recordAudiocheckBox);
        sendSMScheckBox = (CheckBox) findViewById(R.id.sendSMScheckBox);
        contactName1 = (TextView) findViewById(R.id.ContactName1);
        contactName2 = (TextView) findViewById(R.id.ContactName2);
        serviceStatus = (TextView) findViewById(R.id.serviceStatus);
        contactPicker1 = (Button) findViewById(R.id.contactPicker1);
        contactPicker2 = (Button) findViewById(R.id.contactPicker2);
        serviceToggle = (Button) findViewById(R.id.toggleServiceButton);
        saveButton = (Button) findViewById(R.id.saveButton);
    }

    public void writeToPref(){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("localEmergency", LocalEmergency);
        editor.putString("contactName1", ContactName1);
        editor.putString("contactName2", ContactName2);
        editor.putString("contactNumber1", contactNumber1);
        editor.putString("contactNumber2", contactNumber2);
        editor.putString("smsContent", smsContent);
        editor.putString("serviceStatus", servicestatus);
        editor.putString("recordAudioBoolean", Boolean.toString(recordAudioBoolean));
        editor.putString("sendSMSBoolean", Boolean.toString(sendSMSBoolean));
        editor.commit();
    }

    protected void loadPref(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedpreferences.contains("localEmergency")){
            this.LocalEmergency = sharedpreferences.getString("localEmergency","100");
        }

        if(sharedpreferences.contains("contactName1")){
            this.ContactName1 = sharedpreferences.getString("contactName1","Not Selected");
        }

        if(sharedpreferences.contains("contactName2")){
            this.ContactName2 = sharedpreferences.getString("contactName2","Not Selected");
        }

        if(sharedpreferences.contains("contactNumber1")){
            this.contactNumber1 = sharedpreferences.getString("contactNumber1","");
        }

        if(sharedpreferences.contains("contactNumber2")){
            this.contactNumber2 = sharedpreferences.getString("contactNumber2","");
        }

        if(sharedpreferences.contains("smsContent")){
            this.smsContent = sharedpreferences.getString("smsContent","I Need help urgently. This is an automated message.");
        }

        if(sharedpreferences.contains("serviceStatus")){
            this.servicestatus = sharedpreferences.getString("serviceStatus","Stopped");
        }

        if(sharedpreferences.contains("recordAudioBoolean")){
            this.recordAudioBoolean = Boolean.parseBoolean(sharedpreferences.getString("recordAudioBoolean","true"));
        }

        if(sharedpreferences.contains("sendSMSBoolean")){
            this.sendSMSBoolean = Boolean.parseBoolean(sharedpreferences.getString("sendSMSBoolean","true"));
        }

    }

    protected void loadDatatoView(){

        if (!LocalEmergency.equals("")){

            localEmergency.setText(LocalEmergency);
        }

        if (!ContactName1.equals("")){

            contactName1.setText(ContactName1);
        }

        if (!ContactName2.equals("")){

            contactName2.setText(ContactName2);
        }

        if(!smsContent.equals("")){
            smsbox.setText(smsContent);
        }

        if(!servicestatus.equals("")){
            serviceStatus.setText(servicestatus);
        }

        if(!recordAudioBoolean.equals("")){
            recordAudio.setChecked(recordAudioBoolean);
        }

        if(!sendSMSBoolean.equals("")){
            sendSMScheckBox.setChecked(sendSMSBoolean);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setAssests();
        loadPref();
        loadDatatoView();

        localEmergency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LocalEmergency = localEmergency.getText().toString();
            }
        });

        smsbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                smsContent = smsbox.getText().toString();
            }
        });

        serviceToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the service and then set the variable
                if(servicestatus.equals("Stopped")){
                    startService();
                    servicestatus = "Running";
                }else{
                    stopService();
                    servicestatus = "Stopped";
                }
                //refresh the UI components to update the status
                loadDatatoView();
            }
        });

        recordAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recordAudioBoolean = true;
                    Toast.makeText(getApplicationContext(),"App will record audio when listening \"help\". Please click 'save' button", Toast.LENGTH_LONG).show();
                }else{
                    recordAudioBoolean = false;
                    Toast.makeText(getApplicationContext(),"App will NOT record audio when listening \"help\".  Please click 'save' button", Toast.LENGTH_LONG).show();
                }
            }
        });

        sendSMScheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendSMSBoolean = true;
                    Toast.makeText(getApplicationContext(),"App will send SMS when listening \"help\". Please click 'save' button", Toast.LENGTH_LONG).show();
                }else{
                    sendSMSBoolean = false;
                    Toast.makeText(getApplicationContext(),"App will NOT send SMS when listening \"help\".  Please click 'save' button", Toast.LENGTH_LONG).show();
                }
            }
        });


        contactPicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                one="one";

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);

            }
        });

        contactPicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                one="two";

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                writeToPref();
                Toast.makeText(getApplicationContext(), getString(R.string.Saved), Toast.LENGTH_LONG).show();
                loadDatatoView();
                finish();
            }
        });


    }//onCreate over

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT)
        {
            getContactInfo(data);
            //class variables now have the data

            if(one.equals("one")){
                ContactName1 = name;
                contactNumber1 = phoneNumber;
            }else if (one.equals("two")){
                ContactName2 = name;
                contactNumber2 = phoneNumber;
            }

            String a = "You picked : "+name+", "+phoneNumber+".";
            Toast.makeText(getApplicationContext(),a, Toast.LENGTH_LONG).show();
        }


    }//onActivityResult over


    protected void getContactInfo(Intent intent)
    {

        Cursor cursor =  getContentResolver().query(intent.getData(), null, null, null, null);
        while (cursor.moveToNext())
        {
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
            while (phones.moveToNext())
            {
                this.name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                this.phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phones.close();

        }  //while (cursor.moveToNext())
        cursor.close();
    }//getContactInfo

    public void startService() {
        startService(new Intent(getBaseContext(), EmergencyListenerService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), EmergencyListenerService.class));
    }

}//class over
