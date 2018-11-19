package com.example.amar.getcontact;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static String KEY_LOG = "log";
    public static final String EXTRA_REMOVE_PIN = "REMOVE PIN";

    private static final int SMS_REQUST_CODE = 12;
    EditText op;
    EditText np;
    EditText cp;
    AlertDialog.Builder change_pin;
    AlertDialog dialog;
    String userSetPin;
    Object[] log_data = null;
    static Set<String> logs = null;
    static SharedPreferences savedPrefs;
    LogAdapter logAdapter;
    RecyclerView rv;
    public static TextView textempty;
    public static ImageView image_empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv_list);
        textempty = findViewById(R.id.textEmpty);
        image_empty = findViewById(R.id.img_empty);

        //reading shared preference file
        savedPrefs = getSharedPreferences(SetPinActivity.pinSharedPrefFile, MODE_PRIVATE);
        logs = savedPrefs.getStringSet(KEY_LOG, null);

        Log.d("MY_APP", "logs null ? " + (logs == null));


        if(logs == null || logs.isEmpty())
        {
            textempty.setText("No log available!");
            image_empty.setImageResource(R.drawable.contact);

        }

        else if (logs != null) {

            image_empty.setImageResource(0);


            Log.d("MY_APP", "stored data: " + logs.toString());

            //converting the shared pref set into string array
            log_data = logs.toArray();


            // List of Log objects
            List<MyLog> myLogs = new ArrayList<>();

            for (Object var : log_data) {

                String[] t = ((String)var).split("@");

                myLogs.add(new MyLog(t[0], t[1]));
            }


            // sort the list
            Collections.sort(myLogs, new Comparator<MyLog>() {
                @Override
                public int compare(MyLog o1, MyLog o2) {
                    return o1.date.compareTo(o2.date);
                }
            });

            //Log.d("my_data", myLogs.toString());


            logAdapter = new LogAdapter(myLogs, getApplicationContext(), (CoordinatorLayout)findViewById(R.id.coordinator_layout));
            rv.setAdapter(logAdapter);
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }





        //
        userSetPin = savedPrefs.getString(SetPinActivity.PIN_KEY, "");


        //checking whether pin is set or not
        if (userSetPin.isEmpty()) {

            Intent intent = new Intent(this, SetPinActivity.class);

            startActivity(intent);

            finish();
        }

        // ask for the necessary permissions
        askPermissoin();


        if (getIntent().getBooleanExtra(SetPinActivity.EXTRA_PIN_SET, false)) {

            Snackbar snackbar = Snackbar.make(findViewById(R.id.layout), "PIN SET SUCCESSFULLY!", Snackbar.LENGTH_LONG);
            View snackView = snackbar.getView();
            TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.snackTextSuccess));
            snackbar.show();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action1, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.pin:

                userSetPin = savedPrefs.getString(SetPinActivity.PIN_KEY, "");
                Log.d("MY_APP", "old pin: " + userSetPin);

                change_pin = new AlertDialog.Builder(MainActivity.this);
                View v = getLayoutInflater().inflate(R.layout.changepin_layout, null);
                op = v.findViewById(R.id.old_pin);
                np = v.findViewById(R.id.new_pin);
                cp = v.findViewById(R.id.con_pin);

                v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (isValidPin(np.getText().toString())) {

                            if (op.getText().toString().equals(userSetPin)) {


                                if (np.getText().toString().equals(cp.getText().toString())) {

                                    SharedPreferences.Editor editor = SetPinActivity.pinPreference.edit();
                                    editor.putString(SetPinActivity.PIN_KEY, np.getText().toString());
                                    editor.apply();
                                    //Toast.makeText(MainActivity.this, "PIN CHANGED!", Toast.LENGTH_SHORT).show();
                                    Toast toast = Toast.makeText(MainActivity.this,"PIN CHANGED!",Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    TextView text = view.findViewById(android.R.id.message);
                                    text.setTextColor(Color.GREEN);
                                    toast.show();

                                } else {

                                    //Toast.makeText(MainActivity.this, "PIN DID NOT MATCHED!", Toast.LENGTH_SHORT).show();
                                    Toast toast = Toast.makeText(MainActivity.this,"PIN DID NOT MATCHED!",Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    TextView text = view.findViewById(android.R.id.message);
                                    text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                                    toast.show();
                                }
                            } else {

                                //Toast.makeText(MainActivity.this, "PROVIDE A VALID OLD PIN!", Toast.LENGTH_SHORT).show();
                                Toast toast = Toast.makeText(MainActivity.this,"PROVIDE A VALID OLD PIN!",Toast.LENGTH_LONG);
                                View view = toast.getView();
                                TextView text = view.findViewById(android.R.id.message);
                                text.setTextColor(Color.YELLOW);
                                toast.show();
                            }

                        } else {
                           // Toast.makeText(MainActivity.this, "PIN MUST CONTAIN FOUR DIGITS ONLY!", Toast.LENGTH_SHORT).show();
                            Toast toast = Toast.makeText(MainActivity.this,"PIN MUST CONTAIN FOUR DIGITS ONLY!",Toast.LENGTH_LONG);
                            View view = toast.getView();
                            TextView text = view.findViewById(android.R.id.message);
                            text.setTextColor(Color.YELLOW);
                            toast.show();
                        }


                        if (dialog != null) {
                            dialog.dismiss();
                        }

                    }
                });

                v.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (dialog != null) {
                            dialog.dismiss();
                        }

                    }
                });

                change_pin.setView(v);
                dialog = change_pin.create();
                dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;


            case R.id.info:

                Intent info_activity = new Intent(this, Info_activity.class);
                startActivity(info_activity);
                break;

            case R.id.remove:

                SharedPreferences.Editor editor = savedPrefs.edit();

                // clear the pin
                editor.remove(SetPinActivity.PIN_KEY);
                editor.apply();

                Intent intent1;
                intent1 = new Intent(this, SetPinActivity.class);
                intent1.putExtra(EXTRA_REMOVE_PIN, true);
                startActivity(intent1);

                finish();
                break;

        }

        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void askPermissoin() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS},
                    SMS_REQUST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.SMS_REQUST_CODE) {

            if (grantResults.length > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    showSnack("Allow Read SMS and Contact Access Permission");
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    showSnack("Allow  Read SMS permission");
                } else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {

                    showSnack("Allow  Contact Access Permission");
                }
            }
        }
    }

    private void showSnack(String msg) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.layout), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Go to Settings", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send user to the Settings Activity
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

        snackbar.show();

    }

    private boolean isValidPin(String pin) {
        String PIN_PATTERN = "[0-9]{4}";

        return Pattern.compile(PIN_PATTERN).matcher(pin).matches();
    }

    public static void saveLogs() {

        Log.d("MY_APP", logs.toString());

        // save the new set in pref
        SharedPreferences.Editor editor = savedPrefs.edit();
        editor.putStringSet(KEY_LOG, logs);
        editor.apply();
    }

}
