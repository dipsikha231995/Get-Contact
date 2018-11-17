package com.example.amar.getcontact;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SMSReceiver extends BroadcastReceiver {

    private static final String pdu_type = "pdus";
    private SmsMessage[] msgs;
    private String destinationAddress = "";
    private String smsBody = "";


    private String KEY_LOG = "log";


    private Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    private String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ?";

    private String[] args = null;

    private StringBuilder myString = new StringBuilder();


    public String pinSharedPrefFile = "com.example.amar.sharedpref";
    private static final String TAG = "GetContact";

    private String[] cols = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET
    };


    String pattern = "dd/MM/yyyy hh:mm:ss a";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences(pinSharedPrefFile,
                Context.MODE_PRIVATE);

        String savedPin = prefs.getString(SetPinActivity.PIN_KEY, null);



        if (intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")) {

            Toast.makeText(context, "SMS received", Toast.LENGTH_SHORT).show();

            //Toast.makeText(context,savedPin, Toast.LENGTH_SHORT).show();

            Bundle bundle = intent.getExtras();

            String format = bundle.getString("format");

            Object[] pdus = (Object[]) bundle.get(pdu_type);

            if (pdus != null) {

                msgs = new SmsMessage[pdus.length];

                for (int i = 0; i < msgs.length; i++) {

                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);

                    destinationAddress += msgs[i].getOriginatingAddress();
                    smsBody += msgs[i].getMessageBody();
                }

                // Splitting incoming message

                String message = smsBody;

                // Split message into segments
                String[] segments = message.split(" ");


                Log.d(TAG, Arrays.toString(segments));


                // Grabing the segments

                String name = "";


                for (int i = 0; i < (segments.length - 1); i++) {
                    name = name + segments[i] + " ";
                }

                name = name.trim();

                String pin = segments[segments.length - 1];

                Log.d("pin: ", pin);

                Log.d("search query: ", name);


                //Toast.makeText(context, name, Toast.LENGTH_SHORT).show();


                if (!name.isEmpty()) {

                    args = new String[]{name + "%"};

                    if (savedPin.equals(pin)) {

                        // do we have the READ_CONTACT permission??
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED) {

                            Cursor cursor =
                                    context.getContentResolver().query(uri, cols, selection, args, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);


                            if (cursor != null && cursor.getCount() > 0) {

                                Set<String> logs = prefs.getStringSet(KEY_LOG, null);

                                if (logs == null) {
                                    // create a new set
                                    logs = new HashSet<>();
                                }

                                // add a new log

                                // info : contact no, query, timestamp

                                // log format
                                // maa@123456789@16/11/2018 01:40:33 PM


                                String data = name + ":" + destinationAddress + "@" + simpleDateFormat.format(new Date());

                                logs.add(data);

                                saveLogs(prefs, logs);


                                // store unique nos
                                List<String> c_phone = new ArrayList<>();

                                while (cursor.moveToNext()) {

                                    String s1 = cursor.getString(0).trim();
                                    String s2 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    // replace all white spaces within the Phone number
                                    s2 = s2.replaceAll("\\s+", "");


                                    // String s3 = cursor.getString(2);

                               /* if (s3.indexOf("com.google") == 0) {

                                }*/


                                    if (!c_phone.contains(s2)) {
                                        c_phone.add(s2);

                                        myString.append(s1 + " : " + s2 + "\n");
                                    }


                                }

                                sendSMS(destinationAddress, myString.toString());
                            } else {
                                String msg = "Oops! Contact not available!";
                                sendSMS(destinationAddress, msg);
                                Log.e(TAG, "No Contact Found");
                            }
                        } else {

                            Toast.makeText(context, "Contact access permisson is OFF", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        }
    }


    private void saveLogs(SharedPreferences prefs, Set<String> logs) {

        Log.d("MY_APP", logs.toString());

        SharedPreferences.Editor editor = prefs.edit();

        editor.putStringSet(KEY_LOG, logs);

        editor.apply();
    }


    private void sendSMS(String sender, String message) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(sender, null, message, null, null);

        Log.d(TAG, sender + " : " + message);

        //Log.d(TAG, String.valueOf(message.isEmpty()));
    }
}