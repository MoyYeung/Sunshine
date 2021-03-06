package com.example.moyyang.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //意图
            Intent intent =new Intent(this,SettingsActivity.class); //进入动作
            //启动活动
            startActivity(intent);
            return true;
        }
        if(id ==R.id.action_map){
            onPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void onPreferredLocationInMap(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String location =pref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                               .appendQueryParameter("q", location)
                             .build();
        Intent intent  =new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else{
            Log.e(LOG_TAG,"unable to start the map");
        }
    }





}
