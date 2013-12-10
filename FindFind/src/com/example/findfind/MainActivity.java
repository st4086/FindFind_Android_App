package com.example.findfind;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.Toast;
//------------------------------the orb features-----------------------------------
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.*;

import org.opencv.core.Mat;

import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;

import org.opencv.features2d.*;

import org.opencv.highgui.Highgui;

/*------------------------------the google map library----------------
 import com.google.android.gms.maps.*;
 import com.google.android.gms.maps.model.*;
 import android.app.Activity;
 import android.os.Bundle;
 --*/

//-------------------------obt features -------------------------------------
public class MainActivity extends Activity {
	final static String DEBUG_TAG = "MakePhotoActivity";
	private static final int ALBUM_REQUEST_CODE = 1;
	private Bitmap bitmap;
	private Bitmap bitmap1;
	private Bitmap bitmap2;
	private Bitmap bitmap3;
	private ProgressDialog pd;
	private ImageView imageView;
	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;
	private Context context;
	private Button findButton;

	private int selected = 5;

	// private Camera camera;
	// private int cameraId = 0;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private Uri testUri;
	private int[] count;
	private int[] resultIndex;
	private LinearLayout layout1, layout2, layout3;
	private int FindingCode = 0;
	// private ImageView map;

	public static final int MEDIA_TYPE_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		
		
		
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
			Log.d("CVerror", "OpenCV library Init failure");
		}
		
		imageView = (ImageView) findViewById(R.id.preview);
		imageView1 = (ImageView) findViewById(R.id.result1);
		imageView2 = (ImageView) findViewById(R.id.result2);
		imageView3 = (ImageView) findViewById(R.id.result3);
		findButton = (Button) findViewById(R.id.button3);
		findButton.setEnabled(false);
		layout1 = (LinearLayout) findViewById(R.id.layout1);
		// layout2 = (LinearLayout) findViewById(R.id.layout2);
		layout3 = (LinearLayout) findViewById(R.id.layout3);
		// map = (ImageView) findViewById(R.id.map);
		// layout2.setVisibility(View.INVISIBLE);
		layout3.setVisibility(View.INVISIBLE);
	//	findButton.setVisibility(View.INVISIBLE);
		// map.setVisibility(View.INVISIBLE);
		/*
		 * imageView4 = (ImageView) findViewById(R.id.result4); imageView5 =
		 * (ImageView) findViewById(R.id.result5);
		 */
		
		count = new int[3];
		for (int i = 0; i < 3; i++)
			count[i] = 1;
		resultIndex = new int[3];
	}
	 @Override 
	    protected void onDestroy() {
	    	if (pd!=null) {
				pd.dismiss();
				findButton.setEnabled(true);
			}
	    	super.onDestroy();
	    }
	
	public void onClick_Button_Camera(View v) {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, "FindFind");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type, String filename) {
		return Uri.fromFile(getOutputMediaFile(type, filename));
	}

	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type, String filename) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		String state = Environment.getExternalStorageState();
		Log.e("aaa", state);

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				filename);
		// "FindFind");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("FindFind", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			Log.e("aaa", "check good");
			Log.e("aaa", mediaStorageDir.getPath());
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	private String getFilePathFromContentUri(Uri selectedVideoUri,
			ContentResolver contentResolver, int Request_code) {
		if (Request_code == MEDIA_TYPE_IMAGE) {
			String filePath;
			String[] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = contentResolver.query(selectedVideoUri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		}
		else if (Request_code == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
			return selectedVideoUri.getPath();
		}
		return null;

	}

	public void onClick_Button_Album(View v) {
		Log.e("aaa", "aaa");
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, ALBUM_REQUEST_CODE);
		// layout2.setVisibility(View.VISIBLE);
		// intent.putExtra(Intent.EXTRA_INTENT, intent)
	}

	public void buttonrsult1_click(View v) {
		count[0]++;
		if (count[0] == 5)
			count[0] = 1;
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/FindFindData/";
		String ImgR = "object0" + convert(resultIndex[0]) + ".view0" + count[0]
				+ ".png";

		// String ImgR4 =
		// "object0"+convert(tops.get(best).get(1))+".view04.png";

		File imgFile = new File(path + ImgR);

		bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		setImageToView(bitmap, imageView1);
	}

	public void buttonrsult2_click(View v) {
		count[1]++;
		if (count[1] == 5)
			count[1] = 1;
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/FindFindData/";
		String ImgR = "object0" + convert(resultIndex[1]) + ".view0" + count[1]
				+ ".png";

		// String ImgR4 =
		// "object0"+convert(tops.get(best).get(1))+".view04.png";

		File imgFile = new File(path + ImgR);

		bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		setImageToView(bitmap, imageView2);
	}

	public void buttonrsult3_click(View v) {
		count[2]++;
		if (count[2] == 5)
			count[2] = 1;
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/FindFindData/";
		String ImgR = "object0" + convert(resultIndex[2]) + ".view0" + count[2]
				+ ".png";

		// String ImgR4 =
		// "object0"+convert(tops.get(best).get(1))+".view04.png";

		File imgFile = new File(path + ImgR);

		bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		setImageToView(bitmap, imageView3);
	}

	
	  public void imageResult1_click(View v) { //this is which image location
	  Integer index = resultIndex[0]; 
	  
	  setMap(index);
	  
	  } 
	  public void imageResult2_click(View v) { //this is which image location
	  Integer index = resultIndex[1]; 
	  setMap(index);
	 
	 } 
	  public void imageResult3_click(View v) { //this is which image location
		  Integer index = resultIndex[2]; 
		  setMap(index); 
	}
	 
	  void setMap(int index)
	  {
		  String S = Integer.toString(index);
		  Intent intent = new Intent(this, MapActivity.class).putExtra("index", S);
          startActivity(intent);   

	  }
	/*
	 * //set the google map public void setMap(int index) { //read the location
	 * from the
	 * file-----------------------------------!!!!!!!!!!!!!!!!!!unfinished
	 * !!!!!!!!!!!!!!
	 * 
	 * Log.i("info","already in the setmap!!!!!"); //----------------- // Get a
	 * handle to the Map Fragment GoogleMap map1 = ((MapFragment)
	 * getFragmentManager().findFragmentById(R.id.map)).getMap();
	 * 
	 * LatLng sydney = new LatLng(-33.867, 151.206);
	 * 
	 * map1.setMyLocationEnabled(true);
	 * map1.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
	 * 
	 * map1.addMarker(new MarkerOptions() .title("Sydney")
	 * .snippet("The most populous city in Australia.") .position(sydney));
	 * map.setVisibility(View.VISIBLE); }
	 */
	 
	// click the button find and begin to find
	public void onClick_Button_Find(View view) {

		
		//------------------------------------------------------------------------
		Log.e("aaa", "findfind");

		Log.i("info", "in button find! the path is "
				+ getFilePathFromContentUri(testUri, this.getContentResolver(), FindingCode));
		beginToFind(getFilePathFromContentUri(testUri,
				this.getContentResolver(), FindingCode));

	}

	public void beginToFind(String testpath) {

		//----------------------------------dialog-------------------------
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				Log.i("info","in the dialog!!!!!!!!!!!!!!!!!!!!!!");
				pd = new ProgressDialog(context);
				pd.setTitle("Processing...");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}
				
			@Override
			protected Void doInBackground(Void... arg) {
				try {
					//Do something...
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (pd!=null) {
					pd.dismiss();
				
				}
			}
				
		};
		
		task.execute((Void[])null);
		
		
		
		
		
		
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor descriptor = DescriptorExtractor
				.create(DescriptorExtractor.ORB);
		DescriptorMatcher matcher = DescriptorMatcher
				.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/FindFindData/";

		LinkedList<Integer> smallest = new LinkedList<Integer>();
		smallest.add(9999999);
		smallest.add(0);
		// initialize the tops -- select best tops with the number of "selected"
		// Log.i("info", "map initialize: ");
		LinkedList<LinkedList<Integer>> tops = new LinkedList<LinkedList<Integer>>();
		for (int i = 0; i < selected; i++) {
			LinkedList<Integer> map = new LinkedList<Integer>();
			map.add(9999999);
			map.add(0);
			// Log.i("info","the tops "+i+" : the first is "+map.get(0)+". the second is "+map.get(1));
			tops.add(map);
		}
		Log.i("info", "" + tops.get(0).get(1));
		Integer minDist = 9999999;
		int objNum = 0;

		String Img1 = testpath;
		// String Img1 = "object0"+convert(index)+".view05.png";
		Log.i("info", "the test data is : " + Img1);
		// calculate the first image(test) descriptor
		Mat img1 = Highgui.imread(testpath);

		Mat descriptors1 = new Mat();
		MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

		detector.detect(img1, keypoints1);
		descriptor.compute(img1, keypoints1, descriptors1);
		Log.i("info", "the keypoint1 calculated height" + keypoints1.height());
		// for a specific test data, we iterate each train object--201 number of
		// objects
		for (Integer imgIndex = 1; imgIndex <= 20; imgIndex++) {
			Integer distanceAll = 0;
			// each train object has 4 train data
			int eachDistance = 0;

			for (Integer inNum = 1; inNum < 4; inNum++) {
				// String Img2 = "object0004.view03.png";
				String Img2 = "object0" + convert(imgIndex)
						+ ".view0".concat(inNum.toString()) + ".png";
				Log.i("info", "for the test data: " + Img1
						+ ". the train data observed is : " + Img2);
				// calculate the second image(train) descriptor
				Mat img2 = Highgui.imread(path + Img2);
				Mat descriptors2 = new Mat();
				MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

				detector.detect(img2, keypoints2);
				descriptor.compute(img2, keypoints2, descriptors2);

				// matcher should include 2 different image's descriptors
				MatOfDMatch matches1 = new MatOfDMatch();
				matcher.match(descriptors1, descriptors2, matches1);

				DMatch[] matches1Arr = matches1.toArray();

				List<DMatch> matchesL = new ArrayList<DMatch>();
				MatOfDMatch matches = new MatOfDMatch();
				// eachDistance=0;
				for (int i = 0; i < matches1Arr.length; i++) {
					matchesL.add(matches1Arr[i]);
					distanceAll += (int) matches1Arr[i].distance;
					eachDistance = (int) matches1Arr[i].distance;

				}
				matches.fromList(matchesL);

				if (eachDistance < smallest.get(0)) {
					smallest.set(0, eachDistance);
					smallest.set(1, imgIndex);
				}
			}// inNum(1~4) ends

			// find the minimum top selected number images
			// Log.i("info",
			// "the test index is "+Img1+"; and the train data index is "+imgIndex+". The distance is "+distanceAll);
			for (int i = 0; i < selected; i++) {
				// Log.i("info","checked tops "+
				// i+" and its distance is "+tops.get(i).get(0));
				if (tops.get(i).peek() > distanceAll) {
					// Log.i("info","new add the tops element: "+index+"; the distance is "+distanceAll);
					LinkedList<Integer> newmap = new LinkedList<Integer>();
					newmap.add(distanceAll);
					newmap.add(imgIndex);

					tops.remove(4);
					// Log.i("info","add in the position :"+i+"the former distance is "+distanceAll);

					tops.add(i, newmap);
					// Log.i("info","the after distance checked is "+tops.get(i).get(0));
					break;
				}
			}
		}
		// voted the best from the top selected number images
		int[] voted = new int[5];
		for (int i = 0; i < selected; i++) {
			voted[i] = 1;
			for (int j = 0; j < selected; j++) {
				if (tops.get(i).get(1) == tops.get(j).get(1))
					voted[i]++;
			}
		}

		int best = 0;

		int bestIndex = tops.get(best).get(1);
		Log.i("info", "Consider 4 images, The tested object "
				+ " : the testout image is : " + bestIndex
				+ ", the distance is : " + tops.get(best).get(0));
		Log.i("info", "the 2nd testout image is : " + tops.get(1).get(1));
		Log.i("info", "the 3rd testout image is : " + tops.get(2).get(1));
		Log.i("info", "the 4th testout image is : " + tops.get(3).get(1));
		Log.i("info", "the 5th testout image is : " + tops.get(4).get(1));
		Log.i("info",
				"Consider 1 images, the tested object  : the testout image is : "
						+ smallest.get(1) + ", the distance is : "
						+ smallest.get(0));

		// display the five images in the location
		String ImgR1 = "object0" + convert(tops.get(0).get(1)) + ".view01.png";
		String ImgR2 = "object0" + convert(tops.get(1).get(1)) + ".view02.png";
		String ImgR3 = "object0" + convert(tops.get(2).get(1)) + ".view03.png";
		// String ImgR4 =
		// "object0"+convert(tops.get(best).get(1))+".view04.png";
		// String ImgR5 =
		// "object0"+convert(tops.get(best).get(1))+".view02.png";
		for (int i = 0; i < 3; i++) {
			resultIndex[i] = tops.get(i).get(1);
		}
		File imgFile1 = new File(path + ImgR1);
		File imgFile2 = new File(path + ImgR2);
		File imgFile3 = new File(path + ImgR3);

		bitmap1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
		bitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
		bitmap3 = BitmapFactory.decodeFile(imgFile3.getAbsolutePath());
		setImageToView(bitmap1, imageView1);
		setImageToView(bitmap2, imageView2);
		setImageToView(bitmap3, imageView3);
		layout3.setVisibility(View.VISIBLE);
		imageView1.setVisibility(View.VISIBLE);
		imageView2.setVisibility(View.VISIBLE);
		imageView3.setVisibility(View.VISIBLE);
	
		// imageView1.setImageBitmap(bitmap1);

	}

	// set image to a particular view and scale the image to fit the image
	private void setImageToView(Bitmap bitmap, ImageView imageview) {

		Bitmap bmResized = getResizedBitmap(bitmap, imageview.getHeight(),
				imageview.getWidth());
		imageview.setImageBitmap(bmResized);

	}

	// resized the image
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;

	}

	private String convert(Integer index) {
		String innerzero = "00";
		if (index < 10)
			;
		else if (index >= 10 && index < 100)
			innerzero = "0";
		else
			innerzero = "";
		return innerzero + index;
	}

	public void displayImage(Uri imageUri, Bitmap bitmap, ImageView imageview) {
		InputStream stream = null;
		Uri selectedImage = imageUri;

		if (bitmap != null) {
			bitmap.recycle();
		}
		try {
			stream = getContentResolver().openInputStream(selectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bitmap = BitmapFactory.decodeStream(stream);

		// imageview.setImageBitmap(bitmap);
		setImageToView(bitmap, imageview);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputStream stream = null;
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if (data == null) {
					Log.e("aaa", "null data");
					// try{
					Uri selectedImage = fileUri;
					if (selectedImage == null)
						return;

					Toast.makeText(this,
							"Image saved to:\n" + selectedImage.getPath(),
							Toast.LENGTH_LONG).show();

					// displayImage(selectedImage, bitmap, imageView);

					if (bitmap != null) {
						bitmap.recycle();
					}
					try {
						stream = getContentResolver().openInputStream(
								selectedImage);
						bitmap = BitmapFactory.decodeStream(stream);

						imageView.setImageBitmap(bitmap);

						findButton.setEnabled(true);

						testUri = selectedImage;
						FindingCode = requestCode;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

				} else
					// Image captured and saved to fileUri specified in the
					// Intent
					Toast.makeText(this, "Image saved to:\n" + data.getData(),
							Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		// ------------------------------------------------------------find from
		// the album------------------------
		if (requestCode == ALBUM_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK)

			try {
				// recyle unused bitmaps
				if (bitmap != null) {
					bitmap.recycle();
				}
				stream = getContentResolver().openInputStream(data.getData());
				bitmap = BitmapFactory.decodeStream(stream);

				imageView.setImageBitmap(bitmap);

				findButton.setEnabled(true);
			//	findButton.setVisibility(View.INVISIBLE);
				testUri = data.getData();
				FindingCode = requestCode;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		// recyle unused bitmaps

		// displayImage(data.getData(), bitmap, imageView);
	}

	/*
	 * public void onClick_Button_Album(View v) { Log.e("aaa", "aaa"); Intent
	 * intent = new Intent(); intent.setType("image/*");
	 * intent.setAction(Intent.ACTION_GET_CONTENT);
	 * intent.addCategory(Intent.CATEGORY_OPENABLE);
	 * startActivityForResult(intent, ALBUM_REQUEST_CODE); //
	 * intent.putExtra(Intent.EXTRA_INTENT, intent) }
	 */

}
