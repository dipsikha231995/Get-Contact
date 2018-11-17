package com.example.amar.getcontact;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLog {
    String query;
    String timeStamp;
    Date date;

    public MyLog(String s, String t) {
        this.query = s;
        this.timeStamp = t;

        // convert timestamp to Date object
        try {
            this.date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(t);
        } catch (Exception e) {
            //
        }
    }

    public String toString() {
        return this.query + "\n" + this.timeStamp;
    }
}
