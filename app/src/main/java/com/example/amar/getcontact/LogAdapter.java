package com.example.amar.getcontact;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    List<MyLog> log_list;
    Context context;

    CoordinatorLayout coordinatorLayout;

    public LogAdapter(List<MyLog> log_list, Context context, CoordinatorLayout cordinatorLayout) {

        this.log_list = log_list;
        this.context = context;
        this.coordinatorLayout = cordinatorLayout;
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
        String data = log_list.get(pos).toString();
        logViewHolder.tview.setText(data);
        logViewHolder.log_icon.setImageResource(R.drawable.chat);
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

            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Get the position of the item that was clicked.
                    final int mPosition = getLayoutPosition();

                    final String data = log_list.get(mPosition).toString();

                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Clear log history?" , Snackbar.LENGTH_INDEFINITE)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // removing data from set
                                    MainActivity.logs.remove(data.replace("\n", "@"));

                                    if(MainActivity.logs.isEmpty())
                                    {
                                        MainActivity.textempty.setText("No log available!");
                                        MainActivity.image_empty.setImageResource(R.drawable.contact);
                                    }

                                    // removing data from list
                                    log_list.remove(mPosition);

                                    notifyDataSetChanged();

                                    //writing to shared preference file
                                    MainActivity.saveLogs();

                                    Toast.makeText(context, "Log cleared!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    snackbar.show();

                    return true;
                }
            });
        }
    }
}