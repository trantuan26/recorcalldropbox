package vn.com.ebizworld.recorcalldropbox.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.com.ebizworld.recorcalldropbox.R;
import vn.com.ebizworld.recorcalldropbox.fragment.AccountFragment;
import vn.com.ebizworld.recorcalldropbox.fragment.RecordingListFragment;
import vn.com.ebizworld.recorcalldropbox.fragment.RecoreFragment;

public class MainActivity extends AppCompatActivity {

    private int RECORD_AUDIO_REQUEST_CODE = 12345;
    private FragmentManager fragmentManager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    RecoreFragment recoreFragment = new RecoreFragment();
                    transaction.replace(R.id.content, recoreFragment).addToBackStack(null).commit();
                    return true;

                case R.id.navigation_dashboard:
                    RecordingListFragment recordingListFragment = new RecordingListFragment();
                    transaction.replace(R.id.content, recordingListFragment).addToBackStack(null).commit();
                    return true;

                case R.id.navigation_notifications:
                    AccountFragment accountFragment = new AccountFragment();
                    transaction.replace(R.id.content, accountFragment).addToBackStack(null).commit();
                    return true;

            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RecoreFragment recoreFragment = new RecoreFragment();
        transaction.add(R.id.content, recoreFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        String[] permissions = new String[]{
                android.Manifest.permission.RECORD_AUDIO
                , android.Manifest.permission.READ_EXTERNAL_STORAGE
                , android.Manifest.permission.READ_PHONE_STATE
                , android.Manifest.permission.PROCESS_OUTGOING_CALLS
                , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    RECORD_AUDIO_REQUEST_CODE);
        }

    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            int count = 0;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    count++;
                }
            }
            if (grantResults.length == count) {
                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }

    }

}
