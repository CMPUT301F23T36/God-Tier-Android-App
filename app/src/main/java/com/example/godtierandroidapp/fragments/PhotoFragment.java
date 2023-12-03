package com.example.godtierandroidapp.fragments;

import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.godtierandroidapp.R;

public class PhotoFragment extends DialogFragment {

    View v;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void startCamera();
        void selectDelete();
        void selectGallery();
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        boolean img = getArguments().getBoolean("image");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (!img) {
            this.v = getLayoutInflater().inflate(R.layout.add_photo_fragment,null);
            return builder.setView(v)
                    .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            listener.startCamera();
                        }
                    })
                    .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            listener.selectGallery();
                        }
                    })
                    .setNeutralButton("Cancel", null)
                    .create();
        }else {
            this.v = getLayoutInflater().inflate(R.layout.delete_photo_fragment,null);
            return builder
                    .setView(v)
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.selectDelete();
                        }
                    })
                    .setNeutralButton("Cancel", null)
                    .create();
        }
    }
}
