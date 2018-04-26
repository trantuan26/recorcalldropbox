package vn.com.ebizworld.recorcalldropbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "CallReceiver";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("intent " , intent.getAction().toString());

        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCallStateChanged: Incoming Call Ringing");

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    String log = "Outgoing Call Started";
                    Toast.makeText(context,  log, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCallStateChanged: " +log);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss

                    String log = "Ringing but no pickup" + savedNumber + " Call time " + callStartTime +" Date " + new Date() ;
                    Toast.makeText(context,  log, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCallStateChanged: " +log);
                }
                else if(isIncoming){
                    String log = "Incoming " + savedNumber + " Call time " + callStartTime ;
                    Toast.makeText(context,  log, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCallStateChanged: " +log);

                }
                else{
                    String log = "outgoing " + savedNumber + " Call time " + callStartTime +" Date " + new Date() ;
                    Toast.makeText(context,  log, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCallStateChanged: " +log);
                }

                break;
        }
        lastState = state;
    }
}
