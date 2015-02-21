package com.siddharthbhatt.voice_recog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.provider.Contacts.PhonesColumns.NUMBER;


public class Settings extends Activity {

    private EditText number1;
    private EditText number2;
    private EditText number3;
    private Button saveButton;
    private Button contactPicker;
    private static final String MyPREFERENCES = "MyPrefs" ;
    public static final int PICK_CONTACT = 1;
    SharedPreferences sharedpreferences;

    private SettingsClass settings = SettingsClass.getInstance();
    private String name;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        number1 = (EditText) findViewById(R.id.number1);
        number2 = (EditText) findViewById(R.id.number2);
        number3 = (EditText) findViewById(R.id.number3);
        saveButton = (Button) findViewById(R.id.saveButton);
        contactPicker = (Button) findViewById(R.id.contactPicker);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        if (sharedpreferences.contains("no1")){

            number1.setText(Integer.toString(sharedpreferences.getInt("no1", 123)));
        }
        if (sharedpreferences.contains("no2")){

            number2.setText(Integer.toString(sharedpreferences.getInt("no2", 1234)));
        }
        if (sharedpreferences.contains("no3")){

            number3.setText(Integer.toString(sharedpreferences.getInt("no3", 12345)));
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("no1", Integer.parseInt(number1.getText().toString()));
                editor.putInt("no2", Integer.parseInt(number2.getText().toString()));
                editor.putInt("no3", Integer.parseInt(number3.getText().toString()));

                //editor.putString("no1", number1.getText().toString());
                //editor.putString("no2", number2.getText().toString());
                //editor.putString("no3", number3.getText().toString());
                editor.commit();

                Toast.makeText(getApplicationContext(),getString(R.string.Saved), Toast.LENGTH_LONG).show();
                finish();

            }
        });


        contactPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
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
            // Your class variables now have the data, so do something with it.
        }

        String a = "You picked : "+name+", "+phoneNumber+".";
        Toast.makeText(getApplicationContext(),a, Toast.LENGTH_LONG).show();
    }//onActivityResult over


    protected void getContactInfo(Intent intent)
    {

        Cursor cursor =  getContentResolver().query(intent.getData(), null, null, null, null);
        while (cursor.moveToNext())
        {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ( hasPhone.equalsIgnoreCase("1"))
                hasPhone = "true";
            else
                hasPhone = "false" ;

            if (Boolean.parseBoolean(hasPhone))
            {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                while (phones.moveToNext())
                {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
        }  //while (cursor.moveToNext())
        cursor.close();
    }//getContactInfo

}//class over
