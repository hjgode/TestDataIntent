package com.example.testdataintent;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    static String TAG="TESDATAINTENT";
    Context context=this;
    TextView txtAction, txtCategory, txtPackage, txtClass, txtData, txtLog;
    Button btnSendIntent;
    static int sdkVersion;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sdkVersion=android.os.Build.VERSION.SDK_INT;

        txtAction=(TextView)findViewById(R.id.editTextAction);
        txtCategory=(TextView)findViewById(R.id.editTextCategory);
        txtPackage= (TextView)findViewById(R.id.editTextPackage);
        txtClass=(TextView)findViewById(R.id.editTextClassname);
        txtLog=(TextView)findViewById(R.id.txtLog);
        txtLog.setMovementMethod(new ScrollingMovementMethod());

        txtData=(TextView)findViewById(R.id.textView);

        btnSendIntent=(Button)findViewById(R.id.btnSend);
        btnSendIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                if(!txtCategory.getText().toString().isEmpty() && txtCategory.getText().toString().length()>0)
                    intent.addCategory(txtCategory.getText().toString());

                if(!txtAction.getText().toString().isEmpty()&& txtAction.getText().toString().length()>0)
                    intent.setAction(txtAction.getText().toString());   // com.wavelink.intent.action.EMDK.SEND or com.wavelink.intent.action.BARCODE

                if(!txtClass.getText().toString().isEmpty() && !txtPackage.getText().toString().isEmpty() &&
                        txtClass.getText().toString().length()>0 && txtPackage.getText().toString().length()>0)
                    intent.setClassName(txtPackage.getText().toString(), txtClass.getText().toString());

                if(!txtPackage.getText().toString().isEmpty() && txtPackage.getText().toString().length()>0)
                    intent.setPackage(txtPackage.getText().toString());

                // ##### symbol has extra com.symbol.datawedge.data_string, com.symbol.datawedge.label_type and com.symbol.datawedge.source
                intent.putExtra(getResources().getString(R.string.datawedge_intent_key_data), txtData.getText().toString());
                addLog("Sending Intent "+
                        txtAction.getText().toString()+"/"+
                        txtCategory.getText().toString()+"/"+
                        txtPackage.getText().toString()+"/"+
                        txtClass.getText().toString()+": "+
                        txtData.getText().toString()+"/");
                mysendBroadcast(intent);
            }
        });

    }

    @Override
    protected void onResume() {

        super.onResume();
        Toast.makeText(getApplicationContext(), "onResumed called", Toast.LENGTH_LONG).show();

        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sData = intent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
                addLog("received: " + sData);
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(getResources().getString(R.string.actionBARCODECustom)));
    }

    @Override
    protected void onPause() {

        super.onPause();
        Toast.makeText(getApplicationContext(), "onPause called", Toast.LENGTH_LONG).show();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("txtAction", txtAction.getText().toString());
        savedInstanceState.putString("txtCategory", txtCategory.getText().toString());
        savedInstanceState.putString("txtPackage", txtPackage.getText().toString());
        savedInstanceState.putString("txtClass", txtClass.getText().toString());
        savedInstanceState.putString("txtData", txtData.getText().toString());

        savedInstanceState.putString("txtLog", txtLog.getText().toString());
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        txtAction.setText(savedInstanceState.getString("txtAction"));
        txtCategory.setText(savedInstanceState.getString("txtCategory"));
        txtPackage.setText(savedInstanceState.getString("txtPackage"));
        txtClass.setText(savedInstanceState.getString("txtClass"));
        txtData.setText(savedInstanceState.getString("txtData"));

        txtLog.setText(savedInstanceState.getString("txtLog"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.use_EMDK){
            txtAction.setText(getResources().getString(R.string.actionEMDK));
            unregisterReceiver(broadcastReceiver);
            context.registerReceiver(broadcastReceiver, new IntentFilter(getResources().getString(R.string.actionEMDK)));

            txtClass.setText(getResources().getText(R.string.intentClassname));
            txtCategory.setText(getResources().getText(R.string.intentCategory));
            txtPackage.setText(getResources().getText(R.string.intentPackage));

        }

        if (item.getItemId()==R.id.use_Barcode){
            txtAction.setText(getResources().getString(R.string.actionBARCODE));
            unregisterReceiver(broadcastReceiver);
            context.registerReceiver(broadcastReceiver, new IntentFilter(getResources().getString(R.string.actionBARCODE)));

            txtClass.setText("");
            txtCategory.setText(getResources().getText(R.string.intentCategory));
            txtPackage.setText("");
        }

        if (item.getItemId()==R.id.use_Custom){
            txtAction.setText(getResources().getString(R.string.actionBARCODECustom));
            unregisterReceiver(broadcastReceiver);
            context.registerReceiver(broadcastReceiver, new IntentFilter(getResources().getString(R.string.actionBARCODECustom)));

            txtClass.setText(getResources().getText(R.string.intentClassnameCustom));
            txtCategory.setText(getResources().getText(R.string.intentCategoryCustom));
            txtPackage.setText(getResources().getText(R.string.intentPackageCustom));

        }

        return super.onOptionsItemSelected(item);
    }

    private int sendImplicitBroadcast(Context ctxt, Intent i) {
        int iRet=0;
        PackageManager pm=ctxt.getPackageManager();
        List<ResolveInfo> matches=pm.queryBroadcastReceivers(i, 0);
        if(matches.isEmpty()) {
            addLog("no match found for package name");
        }
        for (ResolveInfo resolveInfo : matches) {
            Intent explicit=new Intent(i);
            ComponentName cn=
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            explicit.setComponent(cn);
            addLog("explicit intent for cn="+cn.getPackageName());
            ctxt.sendBroadcast(explicit);
            iRet++;
        }
        return iRet;
    }

    private  void mysendBroadcast(final Intent intent){
        int i;
        //hide ourself
        startActivity(getPackageManager().getLaunchIntentForPackage("com.wavelink.velocity"));// new Intent().setClassName("com.wavelink.velocity", "te.SplashActivity"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                sendDelayedBroadcast(intent);
            }
        }, 5000);
        /*
        if(sdkVersion<26) {
            sendBroadcast(intent);
            Log.d(TAG, "using sendBroadcast");
        }else {
            Log.d(TAG, "using implict Broadcast");
            //for Android O above "gives W/BroadcastQueue: Background execution not allowed: receiving Intent"
            //either set targetSDKversion to 25 or use implicit broadcast
            if(sendImplicitBroadcast(getApplicationContext(), intent)==0) {
                addLog("Trying normal boradcast...");
                sendBroadcast(intent);
            }
            addLog("sendBroadcastAsForegroundUser...");
            sendBroadcastAsForegroundUser(context, intent);
        }
*/
    }

    void sendDelayedBroadcast(Intent intent){
        class delayedClass implements Runnable{
            Intent m_intent;
            delayedClass(Intent in) { m_intent=in; };
            public void run(){
                if(sdkVersion<26) {
                    sendBroadcast(m_intent);
                    Log.d(TAG, "sendDelayedBroadcast using sendBroadcast");
                }else {
                    Log.d(TAG, "sendDelayedBroadcast using implict Broadcast");
                    //for Android O above "gives W/BroadcastQueue: Background execution not allowed: receiving Intent"
                    //either set targetSDKversion to 25 or use implicit broadcast
                    if(sendImplicitBroadcast(getApplicationContext(), m_intent)==0) {
                        addLog("sendDelayedBroadcast Trying normal boradcast...");
                        sendBroadcast(m_intent);
                    }
                }

            }
        }
    }

    /**
     * Wrapper for sendBroadcastAsUser
     *
     * @param context
     *            the context
     * @param intent
     *            the intent
     * @param user
     *            the user handle
     */
//    public static void sendBroadcastAsUser(Context context, Intent intent, UserHandle user) {
//        Log.d(TAG, "sendBroadcastAsUser, user: " + user.hashCode());
//        context.sendBroadcastAsUser(intent, user);
//        // only for system apps:     <uses-permission android:name="android.permission.INTERACT_ACROSS_USERS" />
//    }

    /**
     * Sends a broadcast as the foreground user
     *
     * @param context
     *            the context
     * @param intent
     *            the intent
     */
    public static void sendBroadcastAsForegroundUser(Context context, Intent intent) {
        Log.d(TAG, "sendBroadcastAsForegroundUser");
//        UserHandle user = getForegroundUserHandle(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Only controls those registered in manifest for Android O
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> matches = pm.queryBroadcastReceivers(intent, 0);
            if (matches != null && matches.size() >= 1) {
                for (ResolveInfo resolveInfo : matches) {
                    Intent explicit = new Intent(intent);
                    ComponentName cn = new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

                    explicit.setComponent(cn);
                    //context.sendBroadcastAsUser(explicit, user); // would need system permission     <uses-permission android:name="android.permission.INTERACT_ACROSS_USERS" />
                    context.sendBroadcast(explicit);

                }
            }
        }

        //Finally send to those registered at runtime and also be compatible with old versions
        //context.sendBroadcastAsUser(intent, user); // would need system permission     <uses-permission android:name="android.permission.INTERACT_ACROSS_USERS" />
        context.sendBroadcast(intent);
    }

    void addLog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("IntentTest", msg);
                String old=txtLog.getText().toString();
                old+="\n"+msg;
                txtLog.setText(old);
            }
        });
    }

}
