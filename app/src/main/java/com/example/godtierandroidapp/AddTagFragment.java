package com.example.godtierandroidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class AddTagFragment extends DialogFragment {
    private EditText tag_name;
    private Button tag_clear;
    private OnFragmentInteractionListener listener;
    private Context context;
    private ItemDetailsView itemDetailsView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.itemDetailsView = (ItemDetailsView) context;
        if (context instanceof OnFragmentInteractionListener) {
            this.listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + "Listener has not been implemented");
        }
    }

    public static AddTagFragment newInstance(Serializable tag_list) {
        Bundle args = new Bundle();
        args.putSerializable("tag_list", tag_list);

        AddTagFragment fragment = new AddTagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        void onConfirmPressed(Tag newTag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tag_fragment_layout, null);
        tag_name = view.findViewById(R.id.tag_name);
        tag_clear = view.findViewById(R.id.clear_button);
        tag_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                tag_name.setText("");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add Tags")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!tag_name.getText().toString().isEmpty()) {
                            Tag newTag = new Tag(tag_name.getText().toString());
                            listener.onConfirmPressed(newTag);
                        }
                    }
                }).create();
        }
    }
