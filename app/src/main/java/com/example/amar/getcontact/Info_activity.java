package com.example.amar.getcontact;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class Info_activity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info_layout);

        setTitle("About GET CONTACT");

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        snackbar = Snackbar.make(coordinatorLayout,"Designed by Amarjyoti Gautam & Dipsikha Phukan",Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.CYAN);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.innovation1,0,0,0);
        textView.setGravity(Gravity.CENTER);
        snackbar.show();
    }

}
