package com.example.findfind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class MapActivity extends Activity {

    private static ArrayList<String> LIST=new ArrayList<String>();
    //private static ArrayList<String> LIST2=new ArrayList<String>();
    String[] inputArray;
    String delimiter = ", ";
    String input;
    private double Lat, Lon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		String S = getIntent().getStringExtra("index");
		Log.e("aaa", S);

		int index = 0;

		try {
			index = Integer.parseInt(S);
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse " + nfe);
		}

		try {
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, "location.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				LIST.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Log.e("aaa", LIST.get(index-1));
		String Loc_tmp = LIST.get(index-1);
		String[] Loc = Loc_tmp.split("\\s+");
		Log.e("aaa", "Location is:" + Loc[0] + ", " + Loc[1]);
		
		try {
			Lat = Double.parseDouble(Loc[0]);
			Lon = Double.parseDouble(Loc[1]);
			
			Log.e("aaa", "Location is:" + Lat + ", " + Lon);
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse " + nfe);
		}
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		LatLng sydney = new LatLng(Lat, Lon);

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

		map.addMarker(new MarkerOptions().title("Building" + S)
				.snippet("The most populous city in Earth.").position(sydney));

		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
