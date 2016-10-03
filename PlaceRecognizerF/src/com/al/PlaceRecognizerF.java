package com.al;

//This program is used for Place Recognition


import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.GeomagneticField;
import android.text.format.DateFormat;
import android.util.*;
import android.net.wifi.*;
import android.content.BroadcastReceiver;
import android.database.Cursor;
import android.location.*;
import android.app.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class PlaceRecognizerF extends Activity implements SensorEventListener {
	private static final String TAG = "PlaceRecognizerF";
	private static final int SCAN_WIFI_COUNT = 25; // How many Wifi SSID to record from the scanned list
	private static boolean m_blnRecognizeStatus = false; // true: Recognizing; false: Stopped 
	
	private static final int DATA_TYPE_SENSOR = 1;
	private static final int DATA_TYPE_GPS = 4;
	private static final int DATA_TYPE_WIFI = 5;
	
	private static int SENSOR_EVENT_NULL = 0;
	private static int SENSOR_EVENT_LIGHT = 5;
	private static int SENSOR_EVENT_GPS = 9;
	private static int SENSOR_EVENT_WIFI = 10;
		
	private SensorManager m_smPR = null;
		
	/* Sensor is available or not */
	private static boolean m_blnLightPresent = false;
	
	private static int m_nLightMode = SensorManager.SENSOR_DELAY_NORMAL;
	
	private Location m_location;
	
	private static long m_lnPreviousTime = 0;
	
	//Main Screen
	private TextView m_tvRecognizedPlace;
	
	private RadioGroup m_rdgpEvaluateRecognition;
	private RadioButton m_rdRecognitionCorrect, m_rdRecognitionWrong, m_rdRecognitionUnknown;
	private Button m_btnConfirmEvaluation;

	private RadioGroup m_rdgpOutInStore;
	private RadioButton m_rdOutStore, m_rdInStore;
	
	private Button m_btnRecognize;
	private TextView m_tvShowInfo;
	private Button m_btnDownloadMapScreen;	
	private String m_sRecognizedResult = "";
	
	private Spinner m_spnSelectGTPlace;
	private ArrayAdapter<String> m_adpPlaceName;
	///////////////////////////////
	
	//Download Map Screen
	private EditText m_etGpsLat;
	private EditText m_etGpsLong;
	private ProgressBar m_pgbDownloadProgressIndicator;
	private TextView m_tvShowDownloadProgress;
	private Button m_btnBack;
	private Button m_btnDownloadMap;
	/////////////////////////////////////
	
	
	private static final int RECOGNITION_UNKNOWN = 0;
	private static final int RECOGNITION_CORRECT = 1;
	private static final int RECOGNITION_WRONG = 2;
	
	private static final int NULL_STORE = 0;
	private static final int OUT_STORE = 1;
	private static final int IN_STORE = 2;
	
	private static int m_nOutInStore = NULL_STORE;
	
	private double m_fGpsLat = 0.0f;
	private double m_fGpsLong = 0.0f;
	private List<AL_APInfo> m_lstUserDetectedAPInfo = new ArrayList<AL_APInfo>();

	private static final String m_sGTPlaceNameListFile = "PlaceNameList.csv";
	private static final String m_sOutStoreEvaluationResultFile = "EvaluationResult_OutStore.csv";
	private static final String m_sInStoreEvaluationResultFile = "EvaluationResult_InStore.csv";
	
	private static final String m_sPlaceApDatabaseFileName = "PlaceApDatabase.csv";

	
	//private static final String m_sCorrectPlaceAPInfoFile = "CorrectedPlaceAPInfo.csv";
	private String m_sGTPlaceNameListFullPathFile;
	private String m_sOutStoreEvaluationResultFullPathFile;
	private String m_sInStoreEvaluationResultFullPathFile;
	
	private String m_sCorrectPlaceAPInfoFullPathFile;
	private FileWriter m_fwEvaluationResult = null;
	private FileWriter m_fwCorrectPlacAPInfo = null;
		
	private PlaceRecognizerF m_actHome = this;
	private ResolveInfo m_riHome;

	private static int m_nWiFiScanCount = 0;
	
	private boolean m_blnWifiSignalEnabled = false; // true: Wifi signal is enabled
	private boolean m_blnGPSSignalEnabled = false; // true: GPS signal is enabled
	
	private WifiManager m_mainWifi = null;
	private WifiReceiver m_receiverWifi = null;
	List<ScanResult> m_lstWifiList; // Wifi List
	private Thread m_wifiScanThread = null;
	private static final int WIFI_SCAN_INTERVAL = 2000;   //ms

	private LocationManager m_locManager = null;
	private String m_sGPSProvider = LocationManager.GPS_PROVIDER; //GPS provider
			
	private static float m_fLight = 0.0f;
	
	private WakeLock m_wakeLock;
	
	private AL_PlaceRecognizer m_placeRecognizer = new AL_PlaceRecognizer();
	private AL_Util m_util = new AL_Util();
	
	private List<AL_PlaceAPInfo> m_lstPlaceAPInfo = null;
	
	private AL_DBManager m_DBManager = null;
	
	private List<String> m_lstGTPlaceName = new ArrayList<String>();
	
	
	
	private class DownloadMap extends AsyncTask<Void, Void, Void> {
//		private String m_sURL = "http://130.126.136.127/datastore/golive/autolabel_download.php";
		private String m_sURL = "http://130.126.136.95/datastore/golive/autolabel_download.php";

		//		private final String m_sURL = "http://130.126.136.127/datastore/golive/autolabel_upload.php";
//		private final String m_sURL = "http://130.126.136.127/datastore/golive/aaa.php";
//		private final String m_sURL = "http://synrg3.csl.illinois.edu/datastore/golive/aaa.php";
//		private final String m_sURL = "http://130.126.136.95/datastore/golive/aaa.php";
	
//		private static final String url = "jdbc:mysql://130.126.136.127:3306/autolabel";  
//		private static final String user = "Smokers";  
//		private static final String password = "";  
		
		private HttpClient m_httpClt = null;
		private HttpPost m_httpPost = null;
		private double m_fGpsLat = 0.0f;
		private double m_fGpsLong = 0.0f;
		
		public DownloadMap(double fGpsLat, double fGpsLong) {
			m_fGpsLat = fGpsLat;
			m_fGpsLong = fGpsLong;
			m_httpClt = new DefaultHttpClient();
			m_httpPost = new HttpPost(m_sURL);
		}
/*		
		private void downloadMapAA() {
			Log.w(TAG, "downloadMap.............A..........to download.........");
			try {  
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con = DriverManager.getConnection(url, user, password);  
				Statement st = con.createStatement();  
				ResultSet rs = st.executeQuery("SELECT * FROM tmpTbl");  
				while(rs.next()) { 
					Log.w(TAG, "downloadMap.............AAAAAA..................");
					Log.w(TAG, "-------------" + rs.getString("placename"));  
				}  
				
				con.close();
			} catch (Exception e) { 
				Log.w(TAG, "-------------" + e.toString());  
			}  			
		}
*/		
		
		private void downloadMap() {
	    	FileReader fr;
	    	BufferedReader br;
	    	String sLine = "";
	    	String[] fieldsData;
	    	String[] fields;
	    	BufferedReader in = null; 
	    	List<AL_PlaceAPInfo> lstPlaceAPInfo = new ArrayList<AL_PlaceAPInfo>();
	    	String sColumnName_MAC = "";
	    	String sColumnName_LbRSS = "";
	    	String sColumnName_UbRSS = "";
	    	String sColumnName_MeanRSS = "";
	    	String sColumnName_Order = "";
	    	
	    	String sMAC = "";
	    	String sLbRSS = "";
	    	String sUbRSS = "";
	    	String sMeanRSS = "";
	    	String sOrder = "";
	    	
	    	String sPlaceName = "";
	    		    	
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("gpslat", Double.valueOf(m_fGpsLat).toString()));
				nameValuePairs.add(new BasicNameValuePair("gpslong", Double.valueOf(m_fGpsLong).toString()));
				
				m_httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
								
//				HttpResponse response = m_httpClt.execute(m_httpPost);
				//Download Map data from Server MySQL
				String responseBody = m_httpClt.execute(m_httpPost, responseHandler);
				
				Log.w(TAG, "############################################");
				
				if (responseBody.compareToIgnoreCase("empty") == 0) {
					Log.w(TAG, "-----------------!!!!!!!!!!!---------Out!");
					return;
				}
				
				Log.w(TAG, "-------------!!!!------------------" + responseBody);
				JSONObject json = new JSONObject(responseBody);
				JSONArray jArray = json.getJSONArray("placeaps");
								
				for (int i=0; i<jArray.length(); i++) {
					JSONObject e = jArray.getJSONObject(i);
	                String s = e.getString("placeap");
	                JSONObject jObject = new JSONObject(s);
	                
	                AL_PlaceAPInfo placeAPInfo = new AL_PlaceAPInfo();
	                
	                sPlaceName = jObject.getString("placename");
	                
	                Log.w(TAG, "-----Placename:  " + sPlaceName);
					System.out.println("------------------------------------" + sPlaceName);

	                placeAPInfo.setPlaceName(sPlaceName);
	                placeAPInfo.setGpsLat(jObject.getDouble("gpslat"));
	                placeAPInfo.setGpsLong(jObject.getDouble("gpslong"));
	                
	                List<AL_APInfoForDB> lstAPInfoForDB = new ArrayList<AL_APInfoForDB>();
	                
	                for (int k=1; k<=m_util.COMPARED_AP_COUNT; k++) {
	        	    	sColumnName_MAC = "mac" + k;
	        	    	sColumnName_LbRSS = "lbrss" + k;
	        	    	sColumnName_UbRSS = "ubrss" + k;
	        	    	sColumnName_MeanRSS = "meanrss" + k;
	        	    	sColumnName_Order = "order" + k;
	                		        	    	
	        	    	sMAC = jObject.getString(sColumnName_MAC);
	        	    	
	        	    	if (sMAC.length() > 0) {
		        	    	AL_APInfoForDB apInfoForDB = new AL_APInfoForDB();
	        	    		        	    	
		        	    	apInfoForDB.setMAC(sMAC);
		        	    	apInfoForDB.setLbRSS(jObject.getDouble(sColumnName_LbRSS));
		        	    	apInfoForDB.setUbRSS(jObject.getDouble(sColumnName_UbRSS));
		        	    	apInfoForDB.setMeanRSS(jObject.getDouble(sColumnName_MeanRSS));
		        	    	apInfoForDB.setOrder(jObject.getInt(sColumnName_Order));
		        	    	
		        	    	lstAPInfoForDB.add(apInfoForDB);
	        	    	} else {
	        	    		break;
	        	    	}
	                }
	                
	                placeAPInfo.setAPInfoForDB(lstAPInfoForDB);
	                
	                lstPlaceAPInfo.add(placeAPInfo);
				}
				
				if (m_DBManager != null) {
					//Store Downloaded Map Data into local SQLite
					m_DBManager.add(lstPlaceAPInfo);
					
					//Upldate m_lstPlaceAPInfo
					updatePlaceAPInfo();
				}
				
				//if (response != null) {
				//	HttpEntity resEntity = response.getEntity();
				//	if (resEntity != null) {
				//		String responseMsg = EntityUtils.toString(resEntity).trim();
				//		Log.w(TAG, "......Response: " + responseMsg); 
				//	}
				//}
				//HttpResponse response = httpClt.execute(httpPost);
				//HttpEntity resEntity = response.getEntity();
				//if(resEntity != null) {
				//	String responseMsg = EntityUtils.toString(resEntity).trim();
				//	System.out.println("......Response: " + responseMsg); 
				//}
			} catch (Exception e) {
				Log.w(TAG, "--===================-Error=================-----" + e.toString());
			}
					
		}
		
		
		@Override
		protected Void doInBackground(Void...params) {
			// TODO Auto-generated method stub
			
			downloadMap();
			return null;
		}
		
		protected void onProgressUpdate(Void... params) {

		}	
		
		protected void onPostExecute(Void result) {
			m_pgbDownloadProgressIndicator.setVisibility(View.GONE);
			m_tvShowDownloadProgress.setText("");
		}		
	
	}
	
	
	//Prepare Place AP Info local DB
	private void updatePlaceAPInfo() {
		//Set m_lstPlaceAPInfo from local db files or database
		
		if (m_DBManager == null) {
			m_DBManager = new AL_DBManager(this);
		}
		
		if (m_lstPlaceAPInfo != null) {
			m_lstPlaceAPInfo.clear();
		}
		
		if (m_DBManager != null) {
			m_lstPlaceAPInfo = m_DBManager.query();
		}
	}
	
	
	private void resetValues() {
		m_fGpsLat = 0.0f;
		m_fGpsLong = 0.0f;
		m_sRecognizedResult = "";
	}
		
    private class WifiReceiver extends BroadcastReceiver {
    	/* Record scanned Wifi information */
    	public void onReceive(Context c, Intent intent) {
    		int i,j, nCount,nRecordCount = 0;
    		WifiData wifiData = null;
    		WifiData tmpWifiData = null;
    		int nPos;
    		    		
    		m_lstWifiList = m_mainWifi.getScanResults();
    		
    		nCount = m_lstWifiList.size();
//    		iRecordCount = iCount > WIFI_COUNT ? WIFI_COUNT:iCount;
    		nRecordCount = nCount;

    		if (nRecordCount <= 0) return;
    		
    		List<WifiData> lstWifiData = new ArrayList<WifiData>();
    		
    		wifiData = new WifiData(m_lstWifiList.get(0).SSID, m_lstWifiList.get(0).BSSID, m_lstWifiList.get(0).level);
    		lstWifiData.add(wifiData); 
    		
    		for (i = 1; i < nRecordCount; i++) {
    			wifiData = new WifiData(m_lstWifiList.get(i).SSID, m_lstWifiList.get(i).BSSID, m_lstWifiList.get(i).level);
    			nPos = -1;
    			for (j=0; j < lstWifiData.size(); j++) {
    				tmpWifiData = lstWifiData.get(j);
    				if (m_lstWifiList.get(i).level > tmpWifiData.getSignalLevel()) {
    					nPos = j;
    					break;
    				}
    			}
    			
    			if (nPos == -1) {
    				lstWifiData.add(wifiData); 
    			} else {
    				lstWifiData.add(nPos, wifiData);
    			}
    		}
    		
        	SensorData senData = new SensorData(DATA_TYPE_WIFI, lstWifiData);
        	recordSensingInfo(senData);
        	   		
    	}
    }
	
    
    private void startWiFiScan() {
    	if (m_mainWifi != null) {
    		
	    	m_wifiScanThread = new Thread(new Runnable() {
											public void run() {
												scanWiFi();
											}
									},"Scanning WiFi Thread");
	
	    	m_wifiScanThread.start();
    	}
    }

    private void scanWiFi() {
    	while (m_wifiScanThread != null && m_mainWifi != null) {
    		m_mainWifi.startScan();
    		
      	  	try {
      	  		Thread.sleep(WIFI_SCAN_INTERVAL);
      	  	} catch (InterruptedException e) {
    		  
      	  	}
   		
    	}
   	
    }
    
    private void stopWiFiScan() {
    	m_wifiScanThread = null;
    	if (m_receiverWifi != null) {
    		unregisterReceiver(m_receiverWifi);
    	}
    }
  
    
    private void getGTPlaceName() {
    	File flFile;
    	FileReader fr;
    	BufferedReader br;
    	String sLine = "";
    	
    	m_lstGTPlaceName.clear();

    	m_lstGTPlaceName.add("");
    	m_lstGTPlaceName.add("null");
    	
    	
		flFile = new File(m_sGTPlaceNameListFullPathFile);
		if (flFile.exists()) {
			//Derive existing labels
			try{
				fr = new FileReader(m_sGTPlaceNameListFullPathFile);
				br = new BufferedReader(fr);
				
				while( (sLine = br.readLine()) != null) {
					m_lstGTPlaceName.add(sLine.trim());
					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}					
		}
    	
		m_adpPlaceName.notifyDataSetChanged();
    	
    }
    
	/* Set default widget status and setting */
    private void setDefaultStatus() {
    	m_nLightMode = SensorManager.SENSOR_DELAY_NORMAL;
 	    	
    	m_tvShowInfo.setText(getString(R.string.defaultinfo));
    	m_blnRecognizeStatus = false;
    	m_btnRecognize.setText(getString(R.string.btn_start));
    }

    	        
	/* Get GPS provider */
	private boolean getGPSProvider() {
		Location location = null;
		Criteria crit = new Criteria();
		float fLat, fLng, fAlt;
		// crit.setAccuracy(Criteria.ACCURACY_FINE);
		// crit.setAltitudeRequired(false);
		// crit.setBearingRequired(false);
		// crit.setCostAllowed(false);
		// crit.setPowerRequirement(Criteria.POWER_LOW);

		m_sGPSProvider = m_locManager.getBestProvider(crit, true); // false?
		if (m_sGPSProvider != null) {
			m_blnGPSSignalEnabled = true;
			location = m_locManager.getLastKnownLocation(m_sGPSProvider);
			if (location != null) {
				fLat = (float) (location.getLatitude());
				fLng = (float) (location.getLongitude());
				if (location.hasAltitude()) {
					fAlt = (float) (location.getAltitude());
				}
			}
			return true;
		} else {

			return false;
		}
	}
	
     
	private LocationListener m_locListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (location != null) {
				recordLocation(location);
			}
		}

		public void onProviderDisabled(String provider) {
			if (provider.equals(m_sGPSProvider)) {
				m_blnGPSSignalEnabled = false;
			}
		}

		public void onProviderEnabled(String provider) {
			if (provider.equals(m_sGPSProvider)) {
				m_blnGPSSignalEnabled = true;
				if (m_blnRecognizeStatus == false) {
				}
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (provider.equals(m_sGPSProvider)) {
				if (status == LocationProvider.OUT_OF_SERVICE) {
					m_blnGPSSignalEnabled = false;
				} else {
					m_blnGPSSignalEnabled = true;
					if (m_blnRecognizeStatus == false) {
					}
				}
			}
		}

	};
     
	
	// This function is used to save recognition evaluation result
	// (correct/wrong) into file for later analysis
	// In the evaluation result file, each line contains:
	// Evaluation result (1=correct, 0=wrong), timestamp, Lat, Long, Detected AP
	// List (MAC, RSS)
	private void saveEvaluateionResult(int nEvaluationResult, int nOutInStore) {
		String sLine = "";
		Date dtTmp = new Date();
		final String DATE_FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
		int i;
		String sGTPlaceName = "";
		
		int nCurrentGTPlaceNameIndex = m_spnSelectGTPlace.getSelectedItemPosition();
		if (nCurrentGTPlaceNameIndex == 0) return;
		
		sGTPlaceName = m_lstGTPlaceName.get(nCurrentGTPlaceNameIndex);
		
//		sLine = nEvaluationResult + "," + spdCurrentTime.format(dtTmp) + "," + m_fGpsLat + "," + m_fGpsLong;
//
//		for (i = 0; i < m_lstUserDetectedAPInfo.size(); i++) {
//			sLine = sLine + "," + m_lstUserDetectedAPInfo.get(i).getMAC() + "," + m_lstUserDetectedAPInfo.get(i).getRSS();
//		}
//
//		sLine = sLine + System.getProperty("line.separator");

		//Matching score, Recognized Place Name, Ground Truth Name, Evaluation Result
//		sLine = m_sRecognizedResult + "," + sGTPlaceName + "," + nEvaluationResult + System.getProperty("line.separator");
		
		//Evaluation Result, Matching score, Recognized Place Name, Ground Truth Name 
		sLine = nEvaluationResult + "," + m_sRecognizedResult + "," + sGTPlaceName;
		
		
		//Save detected AP information also
		//MAC, RSS
		if (m_lstUserDetectedAPInfo != null) {
			for (i=0; i<m_lstUserDetectedAPInfo.size(); i++) {
				sLine = sLine + "," + m_lstUserDetectedAPInfo.get(i).getMAC() + "," + m_lstUserDetectedAPInfo.get(i).getRSS();
			}
		}
		
		
		sLine = sLine + System.getProperty("line.separator");
				
		try {
			if (nOutInStore == OUT_STORE) {
				m_fwEvaluationResult = new FileWriter(m_sOutStoreEvaluationResultFullPathFile, true); // Append to the existing file
			} else {
				m_fwEvaluationResult = new FileWriter(m_sInStoreEvaluationResultFullPathFile, true); // Append to the existing file				
			}
			
			m_fwEvaluationResult.write(sLine);
			m_fwEvaluationResult.close();
		} catch (IOException e) {
			m_tvShowInfo.setText("Failed to access SD Card!");
			m_fwEvaluationResult = null;
			return;
		}

		return;
	}     
    
	
	private Button.OnClickListener m_btnConfirmEvaluationListener = new Button.OnClickListener() {
		public void onClick(View v) {
			
			if (m_tvRecognizedPlace.getText().toString().trim().length() == 0) return;

			int nCurrentGTPlaceNameIndex = m_spnSelectGTPlace.getSelectedItemPosition();
			if (nCurrentGTPlaceNameIndex == 0) {
				Toast.makeText(getApplicationContext(), "Please select Ground Truth", Toast.LENGTH_SHORT).show();
				return;
			}
						
			if (m_rdOutStore.isChecked() == false && m_rdInStore.isChecked() == false) {
				Toast.makeText(getApplicationContext(), "Please select Out/In Option", Toast.LENGTH_SHORT).show();
				return;
			} else if (m_rdOutStore.isChecked()) {
				m_nOutInStore = OUT_STORE;
			} else if (m_rdInStore.isChecked()) {
				m_nOutInStore = IN_STORE;
			}

			
			if (m_rdRecognitionCorrect.isChecked() == false && m_rdRecognitionWrong.isChecked() == false && m_rdRecognitionUnknown.isChecked() == false) {
				Toast.makeText(getApplicationContext(), "Please select option", Toast.LENGTH_SHORT).show();
				return;
			} else if (m_rdRecognitionCorrect.isChecked()) {
				saveEvaluateionResult(RECOGNITION_CORRECT, m_nOutInStore);
			} else if (m_rdRecognitionWrong.isChecked()) {
				saveEvaluateionResult(RECOGNITION_WRONG, m_nOutInStore);
			} else if (m_rdRecognitionUnknown.isChecked()) {
				saveEvaluateionResult(RECOGNITION_UNKNOWN, m_nOutInStore);
			}
			
			m_sRecognizedResult = "";
			
			m_tvRecognizedPlace.setText("");

			//Reset
//			m_spnSelectGTPlace.setSelection(0);
//			m_rdgpEvaluateRecognition.clearCheck();
//			m_rdgpOutInStore.clearCheck();

		}
	};     
    
	
	// Use the currently detected GPS, AP List and the correct place name to
	// update database;
	// If the place already exists, update its AP List;
	// If the place does not exist, add a new record
	private void updateLocalDb(String sCorrectPlaceName) {
/*
		AL_PlaceAPInfo placeAPInfo;

		if (sCorrectPlaceName == null || sCorrectPlaceName.length() == 0)
			return;

		if (m_lstUserDetectedAPInfo.size() == 0)
			return;

		placeAPInfo = new AL_PlaceAPInfo();

		placeAPInfo.setPlaceName(sCorrectPlaceName);
		placeAPInfo.setGpsLat(m_fGpsLat);
		placeAPInfo.setGpsLong(m_fGpsLong);
		placeAPInfo.setAPInfo(m_lstUserDetectedAPInfo);

		m_DBManager.add(placeAPInfo);   //Upload local DB

		if (m_lstPlaceAPInfo == null) {
			m_lstPlaceAPInfo = new ArrayList<AL_PlaceAPInfo>();
		}
		
		m_lstPlaceAPInfo.add(placeAPInfo);  //Also upate the cached Place AP Info
		
		return;
*/		
	}

	
	//This function is used to save the corrected place related information
	//In this file, the following information is record:
	// Correct place name, Gps Lat, Gps Long, MAC1, RSS1, MAC2, RSS2......
	// NO mean/std are calculated here
	//These files will be uploaded to the server later, 
	//and the server side application will process these files to get:
	//the mean/std (and correspondingly the Lower bound, Upper bound and Mean of the RSS for each MAC
	private void saveCorrectPlaceInfo(String sCorrectPlaceName) {
		String sLine = "";
		List<AL_APInfo> lstAPInfo = null;
		
		if (m_lstUserDetectedAPInfo == null) return;
		
		try {
			m_fwCorrectPlacAPInfo = new FileWriter(m_sCorrectPlaceAPInfoFullPathFile, true); // Append to the existing file
			
			sLine = sCorrectPlaceName + "," + m_fGpsLat + "," + m_fGpsLong;
			for (int i=0; i<m_lstUserDetectedAPInfo.size(); i++) {
				sLine = sLine + "," + m_lstUserDetectedAPInfo.get(i).getMAC() + "," + m_lstUserDetectedAPInfo.get(i).getRSS();
			}
			
			m_fwCorrectPlacAPInfo.write(sLine);
			m_fwCorrectPlacAPInfo.close();
		} catch (IOException e) {
			m_tvShowInfo.setText("Failed to access SD Card!");
			m_fwCorrectPlacAPInfo = null;
			return;
		}
		
	}
	
	
	private static String[] getFields(String sLine) {
		String[] fields;
		
		fields = sLine.split(",");
		
		return fields;
	}
	
	//Update the m_lstPlaceAPInfo
	private void updateCachedDB(String sCorrectPlaceName) {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		String[] fields = null;

		try {
			fr = new FileReader(m_sCorrectPlaceAPInfoFullPathFile);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				fields = getFields(sLine);
			
			}
		
			fr.close();
		} catch (Exception e) {
			
		}
		
	}
	
		
	/* Event listener for record button (Start/Stop) */
	private Button.OnClickListener m_btnRecognizeListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String sDataDir;
			File flDataFolder;
			boolean blnSensorSelected = false;
			boolean blnWifiSelected = false;
			String sShowInfo = "";

			if (m_blnRecognizeStatus == false) {

				if (m_blnWifiSignalEnabled == false && m_blnGPSSignalEnabled == false) {
					m_tvShowInfo.setText(getString(R.string.promptselect));
					return;
				}

				if (m_blnWifiSignalEnabled == true) {
					// Start scanning Wifi
					m_receiverWifi = new WifiReceiver();
					registerReceiver(m_receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					//m_mainWifi.startScan();
					
					startWiFiScan();
					// ##try {
					// ## Method startScanActiveMethod =
					// WifiManager.class.getMethod("startScanActive");
					// ## startScanActiveMethod.invoke(m_mainWifi);
					// ##} catch(Exception e) {
					// ## //
					// ##}
				}

				if (m_blnGPSSignalEnabled == true) {
					m_locManager.requestLocationUpdates(m_sGPSProvider, 0L, 0.0f, m_locListener);
				}

				// Check whether SD Card has been plugged in
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					sDataDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.PlaceRecognizerFolder);

					flDataFolder = new File(sDataDir);

					// Check whether /mnt/sdcard/PlaceRecognizer/ exists
					if (!flDataFolder.exists()) {
						// Does not exist, create it
						if (flDataFolder.mkdir()) {
							//m_sEvaluationResultFullPathFile = sDataDir + File.separator + m_sEvaluationResultFile;
							//m_sCorrectPlaceAPInfoFullPathFile = sDataDir + File.separator + m_sCorrectPlaceAPInfoFile;
						} else {
							// Failed to create
							m_tvShowInfo.setText("Failed to access SD Card!");
							return;
						}

					} else {
						//m_sEvaluationResultFullPathFile = sDataDir + File.separator + m_sEvaluationResultFile;
						//m_sCorrectPlaceAPInfoFullPathFile = sDataDir + File.separator + m_sCorrectPlaceAPInfoFile;
					}
				} else {
					// NO SD Card
					m_tvShowInfo.setText("Please insert SD Card!");
					return;
				}

				m_blnRecognizeStatus = true;

				m_btnRecognize.setText(getString(R.string.btn_stop));

				sShowInfo = "Running......";
				m_tvShowInfo.setText(sShowInfo);

				m_wakeLock.acquire();

			} else {
				m_blnRecognizeStatus = false;

				if (m_blnWifiSignalEnabled == true) {
					//unregisterReceiver(m_receiverWifi);
					stopWiFiScan();
				}

				if (m_blnGPSSignalEnabled) {
					m_locManager.removeUpdates(m_locListener);
				}

				m_tvShowInfo.setText(getString(R.string.defaultinfo));
				m_btnRecognize.setText(getString(R.string.btn_start));

				m_tvRecognizedPlace.setText("");
				
				resetValues();
				m_wakeLock.release();
			}
		}
	};     
     

	/* Check the availability of sensors, disable relative widgets */
	private void checkSensorAvailability() {
		List<Sensor> lstSensor = m_smPR.getSensorList(Sensor.TYPE_LIGHT);
		if (lstSensor.size() > 0) {
			m_blnLightPresent = true;
		} else {
			m_blnLightPresent = false;
		}
	}

	
	private BroadcastReceiver m_brcvWifiStateChangedReceiver = new BroadcastReceiver() {
		/* Monitor Wifi Enable/Disable status */
		public void onReceive(Context context, Intent intent) {
			int nExtraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

			if (nExtraWifiState == WifiManager.WIFI_STATE_ENABLED) {
				m_blnWifiSignalEnabled = true;
			} else if (nExtraWifiState == WifiManager.WIFI_STATE_DISABLED) {
				m_blnWifiSignalEnabled = false;
			}
		}
	};    
      
   
	public void show_main_screen() {
		setContentView(R.layout.place_recognizer);
		         
    	m_tvRecognizedPlace = (TextView)findViewById(R.id.RecognizedPlaceName);
    
    	m_rdgpEvaluateRecognition = (RadioGroup)findViewById(R.id.EvaluateRecognition);
    	m_rdRecognitionCorrect = (RadioButton)findViewById(R.id.RecognitionCorrect);
    	m_rdRecognitionWrong = (RadioButton)findViewById(R.id.RecognitionWrong);
    	m_rdRecognitionUnknown = (RadioButton)findViewById(R.id.RecognitionUnknown);

    	m_rdgpOutInStore = (RadioGroup)findViewById(R.id.OutInStore);
    	m_rdOutStore = (RadioButton)findViewById(R.id.OutStore);
    	m_rdInStore = (RadioButton)findViewById(R.id.InStore);

    	m_btnConfirmEvaluation = (Button)findViewById(R.id.ConfirmEvaluation);
    	m_btnConfirmEvaluation.setOnClickListener(m_btnConfirmEvaluationListener);
    	    	
        m_tvShowInfo = (TextView)findViewById(R.id.ShowInfo);  //Show Message to user
        m_tvShowInfo.setText(getString(R.string.defaultinfo));
        
        m_btnRecognize = (Button)findViewById(R.id.Recognize);
        m_btnRecognize.setText(getString(R.string.btn_start));
		 
        m_btnRecognize.setOnClickListener(m_btnRecognizeListener);
        
        m_btnDownloadMapScreen = (Button)findViewById(R.id.DownloadMapScreen);
        m_btnDownloadMapScreen.setOnClickListener(m_btnDownloadMapScreenListener);
         
    	m_spnSelectGTPlace = (Spinner)findViewById(R.id.spnSelectGTPlace);
        
    	m_adpPlaceName = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_lstGTPlaceName);
    	m_adpPlaceName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	m_spnSelectGTPlace.setAdapter(m_adpPlaceName);
    	
    	m_btnRecognize.setEnabled(false);
    	//m_tvRecognizedPlace.setText("DicksSportGoodsdangdianganagad");
    	
	}
	
	
	private Button.OnClickListener m_btnDownloadMapScreenListener = new Button.OnClickListener() {
		public void onClick(View v) {
			//show_mapdownload_screen();
			String sDatabaseFullPathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.PlaceRecognizerFolder) + File.separator + m_sPlaceApDatabaseFileName;

			m_lstPlaceAPInfo = m_util.getPlaceAPInfoByFile(sDatabaseFullPathFile);
			
			if (m_lstPlaceAPInfo == null) {
				m_btnRecognize.setEnabled(false);
			} else {
				m_btnRecognize.setEnabled(true);
			}
		}
	};
	
	
	private Button.OnClickListener m_btnBackListener = new Button.OnClickListener() {
		public void onClick(View v) {
			show_main_screen();
		}
	};
	
	
	private Button.OnClickListener m_btnDownloadMapListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String sGpsLat = "";
			String sGpsLong = "";
			double fGpsLat = 0.0f;
			double fGpsLong = 0.0f;
			
			sGpsLat = m_etGpsLat.getText().toString();
			sGpsLong = m_etGpsLong.getText().toString();
			
			if (sGpsLat.length() > 0 && sGpsLong.length() > 0) {
				fGpsLat = Double.valueOf(sGpsLat).doubleValue();
				fGpsLong = Double.valueOf(sGpsLong).doubleValue();
			}
			
			Log.w(TAG, "--------------Here start to download map!");
			m_tvShowDownloadProgress.setText("Downloading...");
			m_pgbDownloadProgressIndicator.setVisibility(View.VISIBLE);
			
			new DownloadMap(fGpsLat, fGpsLong).execute();
			
			//downloadMap(fGpsLat, fGpsLong);
		}
	};

	
	public void show_mapdownload_screen() {
		setContentView(R.layout.load_map);
				
		m_etGpsLat = (EditText)findViewById(R.id.GpsLat);
		m_etGpsLong = (EditText)findViewById(R.id.GpsLong);
				
		m_pgbDownloadProgressIndicator = (ProgressBar)findViewById(R.id.MapDownloadProgress);
		
		m_tvShowDownloadProgress = (TextView)findViewById(R.id.ShowDownloadStatus);
		
		m_btnBack = (Button)findViewById(R.id.Back);
		m_btnBack.setOnClickListener(m_btnBackListener);
		
		m_btnDownloadMap = (Button)findViewById(R.id.DownloadMap);
		m_btnDownloadMap.setOnClickListener(m_btnDownloadMapListener);
		
		m_tvShowDownloadProgress.setText("");
		
		m_etGpsLat.setText("40.141843");
		m_etGpsLong.setText("-88.242750");
				
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	int i;
    	Location location = null;
     	
    	super.onCreate(savedInstanceState);

    	m_smPR = (SensorManager) getSystemService(SENSOR_SERVICE);

        PackageManager pm = getPackageManager();
        m_riHome = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),0);

        checkSensorAvailability();
        
        show_main_screen();
        
		m_mainWifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if (m_mainWifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
			m_blnWifiSignalEnabled = true;
		} else {
			m_blnWifiSignalEnabled = false;
		}
		
		this.registerReceiver(m_brcvWifiStateChangedReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		
		m_locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		m_blnGPSSignalEnabled = m_locManager.isProviderEnabled(m_sGPSProvider);
				
		if ((m_locManager != null) && (m_blnGPSSignalEnabled == true)) {
			location = m_locManager.getLastKnownLocation(m_sGPSProvider);
			if (location != null) {
				float fLat = (float)(location.getLatitude());
				float fLng = (float)(location.getLongitude());
				if (location.hasAltitude()) {
					float fAlt = (float)(location.getAltitude());
				}
			}
		}
		        		
        /* No sensor is installed, disable other widgets and show information to user */
		//if ((m_blnLightPresent == false)) {
			
		if (m_blnWifiSignalEnabled == false && m_blnGPSSignalEnabled == false) {
			m_btnRecognize.setEnabled(false);
			m_tvShowInfo.setText(getString(R.string.no_sensorWiFiGPS));
		}
		//}
		
		/*
		m_smPR.unregisterListener(m_actHome, m_smPR.getDefaultSensor(Sensor.TYPE_LIGHT));
		m_smPR.registerListener(m_actHome, m_smPR.getDefaultSensor(Sensor.TYPE_LIGHT),m_nLightMode);
		*/
		
		Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,  Settings.System.WIFI_SLEEP_POLICY_NEVER);
		
		m_sGTPlaceNameListFullPathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.PlaceRecognizerFolder) + File.separator + m_sGTPlaceNameListFile;
		m_sOutStoreEvaluationResultFullPathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.PlaceRecognizerFolder) + File.separator + m_sOutStoreEvaluationResultFile;
		m_sInStoreEvaluationResultFullPathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.PlaceRecognizerFolder) + File.separator + m_sInStoreEvaluationResultFile;
				
		//m_sCorrectPlaceAPInfoFullPathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.PlaceRecognizerFolder) + File.separator + m_sCorrectPlaceAPInfoFile;
		
		getGTPlaceName();
		 
		m_DBManager = new AL_DBManager(this);
		
		updatePlaceAPInfo();
		

    	PowerManager pwrManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	m_wakeLock = pwrManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    	IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    	filter.addAction(Intent.ACTION_SCREEN_OFF);
    	
    	registerReceiver(m_ScreenOffReceiver,filter);
			
   	
    }
    
    
    public BroadcastReceiver m_ScreenOffReceiver = new BroadcastReceiver() {
    	
    	public void onReceive(Context context, Intent intent) {
    		
    		if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
    			return;
    		}
    		    		
    		if (m_blnRecognizeStatus == true) {
    			    			
    			if (m_blnWifiSignalEnabled == true) {
    	    		if (m_receiverWifi != null) {
    	    			unregisterReceiver(m_receiverWifi);
    	    			registerReceiver(m_receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    	 				//m_mainWifi.startScan();
    	 				startWiFiScan();
    	    		}
    			}
    			
     			if (m_blnGPSSignalEnabled == true) {
     				if (m_locManager != null) {
     					m_locManager.removeUpdates(m_locListener);
     					m_locManager.requestLocationUpdates(m_sGPSProvider, 0L, 0.0f, m_locListener);
     				}
     			}
	
    		}
    			
    	}
    	
    };

    
    public void startActivitySafely(Intent intent) {
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	try {
    		startActivity(intent);
    	} catch (ActivityNotFoundException e) {
    		Toast.makeText(this, "unable to open", Toast.LENGTH_SHORT).show();
    	} catch (SecurityException e) {
    		Toast.makeText(this, "unable to open", Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		//Show MAIN app without finishing current activity
    		ActivityInfo ai = m_riHome.activityInfo;
    		Intent startIntent = new Intent(Intent.ACTION_MAIN);
    		startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    		startIntent.setComponent(new ComponentName(ai.packageName,ai.name));
    		startActivitySafely(startIntent);
    		return true;
    	} else {
    		return super.onKeyDown(keyCode, event);
    	}
    }
    
    
    protected void onResume() {
    	super.onResume();

    	if (m_locManager != null) {
    		m_blnGPSSignalEnabled = m_locManager.isProviderEnabled(m_sGPSProvider);
    		if (m_blnGPSSignalEnabled) {
    			if (m_blnRecognizeStatus == false) {
    			}
    		} else {
    		}
    	} else {
    		m_blnGPSSignalEnabled = false;
    	}
    }
        
    protected void onStop() {
    	super.onStop();    	
    }
    
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        
        m_DBManager.closeDB();  
    }     
    
    public void recordLocation(Location location) {
    	SensorData senData = new SensorData(DATA_TYPE_GPS, location);
    	recordSensingInfo(senData);
    }
    
    public void onSensorChanged(SensorEvent event) {
    	SensorData senData = new SensorData(DATA_TYPE_SENSOR, event);
    	recordSensingInfo(senData);    	
    }
    
    public void recordSensingInfo(SensorData senData) {
	  	int i;
		int nSensorDataType;
		
	    if (m_blnRecognizeStatus == false) { //Stopped
	      	return;
	    }
			
		nSensorDataType = senData.getSensorDataType();
		
		if (nSensorDataType == DATA_TYPE_SENSOR) {
			SensorEvent event;
			
			event = senData.getSensorEvent();
			
			synchronized(this) {
	    		switch (event.sensor.getType()){
				    					    		
		    		case Sensor.TYPE_LIGHT:
		    			// Ambient light level in SI lux units 
		    			m_fLight = event.values[0];
		    			break;
	    		}
	    	}
		} else if (nSensorDataType == DATA_TYPE_GPS){
			Location locationGps;
			locationGps = senData.getGpsLocation();
			
			if (locationGps != null) {

				m_location = new Location(locationGps);

				m_fGpsLat = locationGps.getLatitude();
				m_fGpsLong = locationGps.getLongitude();
												
			}
		} else if (nSensorDataType == DATA_TYPE_WIFI) {
			List<WifiData> lstWifiData = senData.getListWifiData();
			int nWifiCnt = Math.min(SCAN_WIFI_COUNT, lstWifiData.size());
			int nUsedCnt = 0;
			Date dtTmp = new Date();
			final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);

			if (nWifiCnt == 0) return;
			
			m_tvShowInfo.setText(" " + spdCurrentTime.format(dtTmp) + ":   Detected #AP = " + nWifiCnt); 
			 
			m_lstUserDetectedAPInfo.clear();
			List<AL_APInfo> lstAPInfo = new ArrayList<AL_APInfo>();
			List<AL_APInfo> lstSortedAPInfo = new ArrayList<AL_APInfo>();
			
			AL_APInfo[] arrApInfo = new AL_APInfo[nWifiCnt];
			
			for (i=0; i< nWifiCnt; i++) {
				arrApInfo[i] = new AL_APInfo();
				arrApInfo[i].setMAC(lstWifiData.get(i).getBSSID());
				arrApInfo[i].setRSS(lstWifiData.get(i).getSignalLevel());
				
				lstAPInfo.add(arrApInfo[i]);
			}
			
			lstSortedAPInfo = m_util.sortAPInfo(lstAPInfo);

			//Only part of the APs are used (top 10)
			nUsedCnt = Math.min(AL_Util.COMPARED_AP_COUNT, nWifiCnt);
			
			for (i=0; i<nUsedCnt; i++) {
				m_lstUserDetectedAPInfo.add(lstSortedAPInfo.get(i));
			}
			
			AL_UserDetectedInfo userDetectedInfo = new AL_UserDetectedInfo();
			userDetectedInfo.setGpsLat(m_fGpsLat);
			userDetectedInfo.setGpsLong(m_fGpsLong);
			userDetectedInfo.setAPInfo(m_lstUserDetectedAPInfo);
			
			m_sRecognizedResult = m_placeRecognizer.recognizePlace(userDetectedInfo, m_lstPlaceAPInfo);
			
			
			if (m_sRecognizedResult.length() > 0) {
				String[] fields = null;
				fields = m_sRecognizedResult.split(",");
				//m_tvRecognizedPlace.setText(m_sRecognizedResult);
				m_tvRecognizedPlace.setText(fields[1]);
			}
	
		}
		
    }

    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}   
	

	
   
}









