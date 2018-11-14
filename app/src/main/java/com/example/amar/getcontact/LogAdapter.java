package com.example.amar.getcontact;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    List<Object> log_list;
    Context context;

    public LogAdapter(List<Object> log_list, Context context) {

        this.log_list = log_list;
        this.context = context;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_layout, viewGroup, false);


        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LogViewHolder logViewHolder, final int pos) {

        final String data = (String) log_list.get(pos);

        String new_data;

        new_data = data.replace("@", "\n");

        Log.d("my_app", new_data);

        logViewHolder.tview.setText(new_data);
        logViewHolder.log_icon.setImageResource(R.drawable.chat);

        logViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Snackbar snackbar = Snackbar.make(logViewHolder.layout, data, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Delete", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // removing data from set
                                MainActivity.logs.remove(data);

                                // removing data from list
                                log_list.remove(data);

                                notifyItemRemoved(pos);

                                //writing to shared preference file
                                MainActivity.saveLogs();
                            }
                        });

                snackbar.show();
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return log_list.size();
    }


    public class LogViewHolder extends RecyclerView.ViewHolder {

        ImageView log_icon;
        TextView tview;
        View layout;

        public LogViewHolder(@NonNull final View itemView) {
            super(itemView);
            log_icon = itemView.findViewById(R.id.myimage);
            tview = itemView.findViewById(R.id.mytext);
            layout = itemView;
        }
    }
}