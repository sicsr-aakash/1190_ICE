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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




public class Settings extends Activity {

    private EditText localEmergency;
    private TextView contactName1;
    private TextView contactName2;
    private Button contactPicker1;
    private Button contactPicker2;
    private Button saveButton;

    private String LocalEmergency;
    private String ContactName1;
    private String ContactName2;
    private String contactNumber1;
    private String contactNumber2;

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
        contactName1 = (TextView) findViewById(R.id.ContactName1);
        contactName2 = (TextView) findViewById(R.id.ContactName2);
        contactPicker1 = (Button) findViewById(R.id.contactPicker1);
        contactPicker2 = (Button) findViewById(R.id.contactPicker2);
        saveButton = (Button) findViewById(R.id.saveButton);
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

    }

    public void writeToPref(){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("localEmergency", LocalEmergency);
        editor.putString("contactName1", ContactName1);
        editor.putString("contactName2", ContactName2);
        editor.putString("contactNumber1", contactNumber1);
        editor.putString("contactNumber2", contactNumber2);
        editor.commit();

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

}//class over
