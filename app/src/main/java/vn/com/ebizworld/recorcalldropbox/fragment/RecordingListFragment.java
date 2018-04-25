package vn.com.ebizworld.recorcalldropbox.fragment;


import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import vn.com.ebizworld.recorcalldropbox.R;
import vn.com.ebizworld.recorcalldropbox.adapter.RecordingAdapter;
import vn.com.ebizworld.recorcalldropbox.models.Recording;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingListFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerViewRecordings;
    private ArrayList<Recording> recordingArraylist;
    private RecordingAdapter recordingAdapter;
    private TextView textViewNoRecordings;

    public RecordingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_recording_list, container, false);
        initViews(view);
        fetchRecordings();
        return view;
    }

    private void initViews(View view) {
        recordingArraylist = new ArrayList<Recording>();
        /** setting up the toolbar  **/
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Recording List");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        /** enabling back button ***/
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** setting up recyclerView **/
        recyclerViewRecordings = (RecyclerView) view.findViewById(R.id.recyclerViewRecordings);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerViewRecordings.setHasFixedSize(true);

        textViewNoRecordings = (TextView) view.findViewById(R.id.textViewNoRecordings);

    }

    private void fetchRecordings() {

        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        if( files!=null ){

            for (int i = 0; i < files.length; i++) {

                Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" + fileName;

                Recording recording = new Recording(recordingUri,fileName,false);
                recordingArraylist.add(recording);
            }

            textViewNoRecordings.setVisibility(View.GONE);
            recyclerViewRecordings.setVisibility(View.VISIBLE);
            setAdaptertoRecyclerView();

        }else{
            textViewNoRecordings.setVisibility(View.VISIBLE);
            recyclerViewRecordings.setVisibility(View.GONE);
        }

    }

    private void setAdaptertoRecyclerView() {
        recordingAdapter = new RecordingAdapter(getContext(),recordingArraylist);
        recyclerViewRecordings.setAdapter(recordingAdapter);
    }

}
