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
    private ArrayList<Integer> tagList = new ArrayList<>();
    private ArrayList<Tag> listOfTagsObjects= new ArrayList<>();
    private ArrayList<String> tagStrings = new ArrayList<>();
    private TextView tag_text_view;
    private boolean[] selectedTags;
    private EditText tag_name;
    private Button tag_create;
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
        void onConfirmPressed(ArrayList<Tag> tag_list);
        void onConfirmPressed();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tag_fragment_layout, null);
        tag_name = view.findViewById(R.id.tag_name);
        tag_create = view.findViewById(R.id.create_tag);
        tag_clear = view.findViewById(R.id.clear_tag);
        tag_text_view = view.findViewById((R.id.tag_list));
        if (getArguments() != null) {
            listOfTagsObjects = (ArrayList<Tag>) getArguments().getSerializable("tag_list");
            assert listOfTagsObjects != null;
        }
        for(int i=0; i<listOfTagsObjects.size();++i){
            tagStrings.add(listOfTagsObjects.get(i).getName());
        }
        selectedTags = new boolean[tagStrings.size()];



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add/Edit Tags")
                .setMultiChoiceItems(tagStrings.toArray(new String[tagStrings.size()]),selectedTags,new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            tagList.add(i);

                            Collections.sort(tagList);
                        } else {
                            tagList.remove(Integer.valueOf(i));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i=0;i<tagList.size();++i){
                            stringBuilder.append(tagStrings.get(tagList.get(i)));
                            if (i != tagList.size() -1) {
                                stringBuilder.append(", ");
                            }
                        }
                        tag_text_view.setText(stringBuilder.toString());
                        listener.onConfirmPressed();
                    }
                }).create();
        }
    }
