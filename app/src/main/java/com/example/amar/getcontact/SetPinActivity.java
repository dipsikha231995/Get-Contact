package com.example.amar.getcontact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.amar.getcontact.MainActivity.*;

public class SetPinActivity extends AppCompatActivity {

    public static final String EXTRA_PIN_SET = "PIN SET";
    public static final String pinSharedPrefFile = "com.example.amar.sharedpref";
    public static final String PIN_KEY = "pin";
    public static SharedPreferences pinPreference;
    EditText userPin, conPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpin_activity);

        // hide the action bar
        getSupportActionBar().hide();


        userPin = findViewById(R.id.pin);
        conPin = findViewById(R.id.c_pin);

        pinPreference = getSharedPreferences(pinSharedPrefFile, MODE_PRIVATE);


        if (getIntent().getBooleanExtra(MainActivity.EXTRA_REMOVE_PIN, false)) {

            Snackbar snackbar = Snackbar.make(findViewById(R.id.setpinLayout), "PIN REMOVED!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    public void savePin(View view) {

        if (isValidPin(userPin.getText().toString())) {

            if (userPin.getText().toString().equals(conPin.getText().toString())) {

                SharedPreferences.Editor editor = pinPreference.edit();
                editor.putString(PIN_KEY, userPin.getText().toString());
                editor.apply();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(EXTRA_PIN_SET, true);

                startActivity(intent);

                finish();
            } else {

                Snackbar snackbar2 = Snackbar.make(findViewById(R.id.setpinLayout), "PIN NOT MATCHED", Snackbar.LENGTH_LONG);
                snackbar2.show();

            }
        } else {

            Snackbar snackbar3 = Snackbar.make(findViewById(R.id.setpinLayout), "PIN MUST CONTAIN FOUR DIGITS ONLY", Snackbar.LENGTH_LONG);
            snackbar3.show();

        }
    }


    public boolean isValidPin(String pin) {
        String PIN_PATTERN = "[0-9]{4}";

        return Pattern.compile(PIN_PATTERN).matcher(pin).matches();
    }
}
