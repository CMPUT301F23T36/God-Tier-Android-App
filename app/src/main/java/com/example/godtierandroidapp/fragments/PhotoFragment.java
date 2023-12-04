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

/**
 * Provides a fragment that allows the user to delete a photo, or add an image from the user's
 * gallery or take a photo from the camera.
 * If instantiated with the boolean "image" argument as true, then it allows the user to delete the image.
 * Otherwise it does not, and instead prompts to choose between the gallery or the camera.
 *
 * @author Travis
 */
public class PhotoFragment extends DialogFragment {

    View v;
    private OnFragmentInteractionListener listener;

    /**
     * Callbacks for the various cases - On camera open, on image deletion, and on gallery open.
     */
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
            // Choose between Camera and Gallery
            v = getLayoutInflater().inflate(R.layout.add_photo_fragment,null);
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
        } else {
            // Prompt image deletion
            v = getLayoutInflater().inflate(R.layout.delete_photo_fragment,null);
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
