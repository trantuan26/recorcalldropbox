package vn.com.ebizworld.recorcalldropbox.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import vn.com.ebizworld.recorcalldropbox.R;
import vn.com.ebizworld.recorcalldropbox.fragment.AccountFragment;
import vn.com.ebizworld.recorcalldropbox.fragment.RecordingListFragment;
import vn.com.ebizworld.recorcalldropbox.fragment.RecoreFragment;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int RECORD_AUDIO_REQUEST_CODE = 12345;
    private FragmentManager fragmentManager;
    private final static String APP_KEY = "kmcdp45dae8l1vo";
    private final static String APP_SECRET = "kbgmew97s3ihpe0";
    String accessToken = "TyTz4EYa_CAAAAAAAAABGpEA-KZpjkdiIEOPMGExV2pjLa8-_22w2wukbi3sGzeO";


    private DropboxAPI<AndroidAuthSession> mDBApi;


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

        initialize_session();

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

    @Override
    protected void onStart() {
        super.onStart();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        String[] permissions = new String[]{
                android.Manifest.permission.RECORD_AUDIO
                , android.Manifest.permission.READ_EXTERNAL_STORAGE
                , android.Manifest.permission.READ_PHONE_STATE
                , android.Manifest.permission.PROCESS_OUTGOING_CALLS
                , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                , android.Manifest.permission.INTERNET
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

    protected void initialize_session(){

        // store app key and secret key
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, accessToken);
        //Pass app key pair to the new DropboxAPI object.
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        Log.d(TAG, "initialize_session: "+mDBApi);

        uploadFiles();
    }

    /**
     * Callback register method to execute the upload method
     * @param
     */

    public void uploadFiles(){

        new Upload().execute();
    }


    /**
     *  Asynchronous method to upload any file to dropbox
     */
    public class Upload extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){

        }

        protected String doInBackground(String... arg0) {

            DropboxAPI.Entry response = null;

            try {

                // Define path of file to be upload
                File file = new File("./sdcard/lenovoid-log.txt");
                FileInputStream inputStream = new FileInputStream(file);



                //put the file to dropbox
                response = mDBApi.putFile("lenovoid.txt", inputStream,
                        file.length(), null, null);
                Log.e("DbExampleLog", "The uploaded file's rev is: " + response.rev);

            } catch (Exception e){

                e.printStackTrace();
            }

            return response.rev;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.isEmpty() == false){
                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                Log.e("DbExampleLog", "The uploaded file's rev is: " + result);
            }
        }
    }

    protected void onResume() {
        super.onResume();



    }

}
