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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class AddTagFragment extends DialogFragment {
    private RecyclerView tag_list_recycler;
    private ArrayList<Tag> tag_list;
    private TagListViewAdapter tagAdapter;
    private EditText tag_name;
    private Button tag_create;
    private Button tag_clear;
    private OnFragmentInteractionListener listener;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tag_fragment_layout, null);
        tag_name = view.findViewById(R.id.tag_name);
        tag_create = view.findViewById(R.id.create_tag);
        /*
        CURRENTLY DOES NOT WORK

        tag_clear = view.findViewById(R.id.clear_tag);
        if (getArguments() != null) {
            tag_list = (ArrayList<Tag>) getArguments().getSerializable("tag_list");
            assert tag_list != null;
        }
        tag_list_recycler = view.findViewById(R.id.tag_list);
        tagAdapter = new TagListViewAdapter(context, tag_list);
        tag_list_recycler.setAdapter(tagAdapter);
        */

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add/Edit Tags")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirmPressed();
                    }
                }).create();
        }
    }
