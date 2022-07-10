package com.example.codengine.printerapp.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.codengine.printerapp.R;

public class AppUtils {
    public void showAlertDialogwithMessage(Context context, String message) {

        CardView btnOk;
        TextView txtMsg;
        ImageView imgClose;

        final AlertDialog.Builder dilaog =
                new AlertDialog.Builder(context, R.style.CustomDialogs);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.no_data_availavle, null);
        dilaog.setView(view);
        final AlertDialog subjectAlertDialog = dilaog.create();

        btnOk = (CardView) view.findViewById(R.id.btnOk);
        txtMsg = (TextView) view.findViewById(R.id.txtMsg);
        imgClose = view.findViewById(R.id.imgClose);
        imgClose.setVisibility(View.VISIBLE);
        txtMsg.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectAlertDialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectAlertDialog.dismiss();
            }
        });

        dilaog.setCancelable(true);
        subjectAlertDialog.setCancelable(true);
        subjectAlertDialog.show();
    }
}
