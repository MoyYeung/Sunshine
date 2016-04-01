package com.example.moyyang.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moyyang.sunshine.app.Utility;
import com.example.moyyang.sunshine.app.data.WeatherContract;
import com.example.moyyang.sunshine.app.data.WeatherContract.WeatherEntry;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public static final String FORECAST_SHARE_HASHTAG = "#sunshineApp";
    private String mForecastStr;

    private ShareActionProvider mShareActionProvider;


    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = new String[]{
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;


    private ImageView mIconView;
    private TextView  mHighTempView;
    private TextView mLowTempView;
    private TextView mDescView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private  TextView mFriendlyDateView;
    private TextView mDateView;


    public DetailActivityFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

      inflater.inflate(R.menu.detailfragment,menu);

        MenuItem menuItem  = menu.findItem(R.id.action_share);

        mShareActionProvider =(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mForecastStr != null){
            mShareActionProvider.setShareIntent(createForecastSharedIntent());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDateView =(TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView =(TextView)rootView.findViewById(R.id.detail_day_textview);
        mHighTempView =(TextView)rootView.findViewById(R.id.detail_high_textview);
        mLowTempView =(TextView)rootView.findViewById(R.id.detail_low_textview);
        mDescView =(TextView)rootView.findViewById(R.id.detail_forecast_textview);
        mIconView =(ImageView)rootView.findViewById(R.id.detail_icon);
        mHumidityView= (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        mWindView =(TextView)rootView.findViewById(R.id.detail_wind_textview);
        mPressureView=(TextView)rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }


    private  Intent createForecastSharedIntent(){
        Intent sharedIntent = new Intent(Intent.ACTION_SEND);

        sharedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT );

        sharedIntent.setType("text/plain");

        sharedIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr+FORECAST_SHARE_HASHTAG);
        return sharedIntent;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent =getActivity().getIntent();

        if(intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(),intent.getData(),DETAIL_COLUMNS,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

       if(null != data && data.moveToFirst()) {
           int weatherID = data.getInt(COL_WEATHER_CONDITION_ID);

           mIconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherID));

           Long date = data.getLong(COL_WEATHER_DATE);

           String friendDateText = Utility.getFriendlyDayString(getActivity(), date);
           String dateText = Utility.getFormattedMonthDay(getActivity(), date);
           mFriendlyDateView.setText(friendDateText);
           mDateView.setText(dateText);

           boolean isMetric = Utility.isMetric(getActivity());

           String weatherDescription = data.getString(COL_WEATHER_DESC);
           mDescView.setText(weatherDescription);

           String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
           mHighTempView.setText(high);

           String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
           mLowTempView.setText(low);

           float humidity = data.getFloat(COL_WEATHER_HUMIDITY);  //? why 0?
           mHumidityView.setText(getString(R.string.format_humidity,humidity));

           float windSpeedStr =data.getFloat(COL_WEATHER_WIND_SPEED);
           float windDirStr= data.getFloat(COL_WEATHER_DEGREES);
           mWindView.setText(Utility.getFormattedWind(getActivity(),windSpeedStr,windDirStr));

           float pressure =data.getFloat(COL_WEATHER_PRESSURE);
           mPressureView.setText(getString(R.string.format_pressure,pressure));


            //We still need this for the share intent
            mForecastStr = String.format("%s - %s - %s/%s", dateText, weatherDescription, high, low);


       }



        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createForecastSharedIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
