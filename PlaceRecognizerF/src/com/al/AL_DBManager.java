package com.al;

//This class is used to operate local database

import java.util.ArrayList;  
import java.util.List;  
  
import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  

public class AL_DBManager {
	private static final String TABLE_NAME = "PlaceAPInfo";
	private AL_DBHelper m_helper;  
    private SQLiteDatabase m_db;  
    
    private static final int MAP_RANGE = 200;  //meter
    private static final long EARTH_RADIUS = 6371000; //meter
    
    private static final double LAT_GAP = 0.0018;  //=200 meters
    private static final double LONG_GAP = 0.0021;  //=200 meters
   
    public AL_DBManager(Context context) {  
    	m_helper = new AL_DBHelper(context);  
        m_db = m_helper.getWritableDatabase();  
    }  
      
    //The AP information should have been ordered based on RSS (i.e. the order field should be meaningful)
    public void add(AL_PlaceAPInfo placeAPInfo) {
    	String sSQL = "";
    	int i;
    	String sPlaceName = "";
    	double fGpsLat = 0.0f;
    	double fGpsLong = 0.0f;
    	String sRangeCondition = "";
    	String sColumnList = "_id,placename,gpslat,gpslong,mac1,lbrss1,ubrss1,meanrss1,order1,mac2,lbrss2,ubrss2,meanrss2,order2,mac3,lbrss3,ubrss3,meanrss3,order3,mac4,lbrss4,ubrss4,meanrss4,order4,mac5,lbrss5,ubrss5,meanrss5,order5,mac6,lbrss6,ubrss6,meanrss6,order6,mac7,lbrss7,ubrss7,meanrss7,order7,mac8,lbrss8,ubrss8,meanrss8,order8,mac9,lbrss9,ubrss9, meanrss9,order9,mac10,lbrss10,ubrss10,meanrss10,order10";
    	sColumnList = sColumnList + ",mac11,lbrss11,ubrss11,meanrss11,order11,mac12,lbrss12,ubrss12,meanrss12,order12,mac13,lbrss13,ubrss13,meanrss13,order13,mac14,lbrss14,ubrss14,meanrss14,order14,mac15,lbrss15,ubrss15,meanrss15,order15,mac16,lbrss16,ubrss16,meanrss16,order16,mac17,lbrss17,ubrss17,meanrss17,order17,mac18,lbrss18,ubrss18,meanrss18,order18,mac19,lbrss19,ubrss19, meanrss19,order19,mac20,lbrss20,ubrss20,meanrss20,order20";
    	    	
    	
    	if (placeAPInfo == null) return;
    	
    	int nAPCnt = placeAPInfo.getAPInfoForDB().size();

    	//String[] sParams = new String[4 + nAPCnt*5];
    	String[] sParams = new String[3 + nAPCnt*5];
    	
    	//sParams[0] = Integer.valueOf(AL_Util.RECORD_NEW).toString();
    	sPlaceName = placeAPInfo.getPlaceName();
    	sParams[0] = sPlaceName;
    	fGpsLat = placeAPInfo.getGpsLat();
    	sParams[1] = Double.valueOf(fGpsLat).toString();
    	fGpsLong = placeAPInfo.getGpsLong();
    	sParams[2] = Double.valueOf(fGpsLong).toString();
    	
//    	sRangeCondition = " AND gpslat IS NOT NULL AND gpslong IS NOT NULL AND LENGTH(gpslat)> 0 AND LENGTH(gpslong) > 0 AND " + 
//				EARTH_RADIUS + "* 2 * ATAN2(SQRT(SIN((gpslat - ("+ fGpsLat + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslat - (" + fGpsLat + ")) *" +  Math.PI + "/180/2.0) + COS((" + fGpsLat + ") *" +  Math.PI + "/180) * COS(gpslat *" +  Math.PI + "/180) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0)), SQRT(1 - (SIN((gpslat - (" + fGpsLat + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslat - (" + fGpsLat + ")) *" +  Math.PI + "/180/2.0) + COS((" + fGpsLat + ") *" +  Math.PI + "/180) * COS(gpslat *" +  Math.PI + "/180) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0)))) <=" + MAP_RANGE;
    	
    	sRangeCondition = " AND gpslat IS NOT NULL AND gpslong IS NOT NULL AND LENGTH(gpslat)> 0 AND LENGTH(gpslong) > 0 AND " +
    			"ABS(gpslat - (" + fGpsLat + ")) <= " + LAT_GAP + " AND ABS(gpslong - (" + fGpsLong + ")) <= " + LONG_GAP;
    	
    	for (i=0; i<nAPCnt; i++) {
    		sParams[i*5+3] = placeAPInfo.getAPInfoForDB().get(i).getMAC();
    		sParams[i*5+4] = Double.valueOf(placeAPInfo.getAPInfoForDB().get(i).getLbRSS()).toString();    		
    		sParams[i*5+5] = Double.valueOf(placeAPInfo.getAPInfoForDB().get(i).getUbRSS()).toString();
    		sParams[i*5+6] = Double.valueOf(placeAPInfo.getAPInfoForDB().get(i).getMeanRSS()).toString();
    		sParams[i*5+7] = Integer.valueOf(placeAPInfo.getAPInfoForDB().get(i).getOrder()).toString();
    	}
    	
    	for (i=nAPCnt; i<AL_Util.COMPARED_AP_COUNT; i++) {
    		sParams[i*5+3] = "";
    		sParams[i*5+4] = "";
    		sParams[i*5+5] = "";
    		sParams[i*5+6] = "";
    		sParams[i*5+7] = "";
    	}
    	
    	m_db.beginTransaction();
    	
    	//Need to check whether the inserted record already exists
        try {    
        	
        	//sSQL = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE placename = ?";
        	//String[] sTmpParams = new String[1];
        	//sTmpParams[0] = sPlaceName;
        	//m_db.execSQL(sSQL, sTmpParams);
        	
        	//sSQL = "INSERT INTO " + TABLE_NAME + " VALUES (null, ?, ?, ?, ? ";
        	//sSQL = "INSERT INTO " + TABLE_NAME + " VALUES (null, ?, ?, ? ";
//        	sSQL = "INSERT OR REPLACE INTO " + TABLE_NAME + " (" + sColumnList + ") VALUES ((SELECT _id from " + TABLE_NAME + " where placename = '" + sPlaceName + "'), ?, ?, ? ";
        	sSQL = "INSERT OR REPLACE INTO " + TABLE_NAME + " (" + sColumnList + ") VALUES ((SELECT _id from " + TABLE_NAME + " where placename = '" + sPlaceName + "'" + sRangeCondition + "), ?, ?, ? ";
        	
        	for (i=0; i<AL_Util.COMPARED_AP_COUNT; i++) {
        		sSQL = sSQL + ",?,?,?,?,?";
        	}
        	
        	sSQL = sSQL + ")";
                    	
        	m_db.execSQL(sSQL, sParams);  
              
            m_db.setTransactionSuccessful();  
        } finally {  
        	m_db.endTransaction();     
        }  
    }     
    
    
    public void add(List<AL_PlaceAPInfo> lstPlaceAPInfo) {
    	String sSQL = "";
    	String sPlaceName = "";
    	String sRangeCondition = "";
    	double fGpsLat = 0.0f;
    	double fGpsLong = 0.0f;
    	
    	int i,j;
    	String sColumnList = "_id,placename,gpslat,gpslong,mac1,lbrss1,ubrss1,meanrss1,order1,mac2,lbrss2,ubrss2,meanrss2,order2,mac3,lbrss3,ubrss3,meanrss3,order3,mac4,lbrss4,ubrss4,meanrss4,order4,mac5,lbrss5,ubrss5,meanrss5,order5,mac6,lbrss6,ubrss6,meanrss6,order6,mac7,lbrss7,ubrss7,meanrss7,order7,mac8,lbrss8,ubrss8,meanrss8,order8,mac9,lbrss9,ubrss9, meanrss9,order9,mac10,lbrss10,ubrss10,meanrss10,order10";
    	sColumnList = sColumnList + ",mac11,lbrss11,ubrss11,meanrss11,order11,mac12,lbrss12,ubrss12,meanrss12,order12,mac13,lbrss13,ubrss13,meanrss13,order13,mac14,lbrss14,ubrss14,meanrss14,order14,mac15,lbrss15,ubrss15,meanrss15,order15,mac16,lbrss16,ubrss16,meanrss16,order16,mac17,lbrss17,ubrss17,meanrss17,order17,mac18,lbrss18,ubrss18,meanrss18,order18,mac19,lbrss19,ubrss19, meanrss19,order19,mac20,lbrss20,ubrss20,meanrss20,order20";
    	    	
    	if (lstPlaceAPInfo == null) return;

    	m_db.beginTransaction();
    	
    	try {
	    	for (AL_PlaceAPInfo placeAPInfo:lstPlaceAPInfo) {
	    	
		    	int nAPCnt = placeAPInfo.getAPInfoForDB().size();
		
		    	//String[] sParams = new String[4 + nAPCnt*5];
		    	String[] sParams = new String[3 + nAPCnt*5];
		    	
		    	//sParams[0] = Integer.valueOf(AL_Util.RECORD_NEW).toString();
		    	sPlaceName = placeAPInfo.getPlaceName();
		    	sParams[0] = sPlaceName;
		    	fGpsLat = placeAPInfo.getGpsLat();
		    	sParams[1] = Double.valueOf(fGpsLat).toString();
		    	fGpsLong = placeAPInfo.getGpsLong();
		    	sParams[2] = Double.valueOf(fGpsLong).toString();
		    	
//		    	sRangeCondition = " AND gpslat IS NOT NULL AND gpslong IS NOT NULL AND LENGTH(gpslat)> 0 AND LENGTH(gpslong) > 0 AND " + 
//						EARTH_RADIUS + "* 2 * ATAN2(SQRT(SIN((gpslat - ("+ fGpsLat + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslat - (" + fGpsLat + ")) *" +  Math.PI + "/180/2.0) + COS((" + fGpsLat + ") *" +  Math.PI + "/180) * COS(gpslat *" +  Math.PI + "/180) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0)), SQRT(1 - (SIN((gpslat - (" + fGpsLat + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslat - (" + fGpsLat + ")) *" +  Math.PI + "/180/2.0) + COS((" + fGpsLat + ") *" +  Math.PI + "/180) * COS(gpslat *" +  Math.PI + "/180) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0) * SIN((gpslong - (" + fGpsLong + ")) *" +  Math.PI + "/180/2.0)))) <=" + MAP_RANGE;
		    	sRangeCondition = " AND gpslat IS NOT NULL AND gpslong IS NOT NULL AND LENGTH(gpslat)> 0 AND LENGTH(gpslong) > 0 AND " +
		    			"ABS(gpslat - (" + fGpsLat + ")) <= " + LAT_GAP + " AND ABS(gpslong - (" + fGpsLong + ")) <= " + LONG_GAP;
		    	
		    	for (i=0; i<nAPCnt; i++) {
		    		sParams[i*5+3] = placeAPInfo.getAPInfoForDB().get(i).getMAC();
		    		sParams[i*5+4] = Double.valueOf(placeAPInfo.getAPInfoForDB().get(i).getLbRSS()).toString();    		
		    		sParams[i*5+5] = Double.valueOf(placeAPInfo.getAPInfoForDB().get(i).getUbRSS()).toString();
		    		sParams[i*5+6] = Double.valueOf(placeAPInfo.getAPInfoForDB().get(i).getMeanRSS()).toString();
		    		sParams[i*5+7] = Integer.valueOf(placeAPInfo.getAPInfoForDB().get(i).getOrder()).toString();
		    	}
		    	
		    	for (i=nAPCnt; i<AL_Util.COMPARED_AP_COUNT; i++) {
		    		sParams[i*5+3] = "";
		    		sParams[i*5+4] = "";
		    		sParams[i*5+5] = "";
		    		sParams[i*5+6] = "";
		    		sParams[i*5+7] = "";
		    	}
		    	
		    	//Need to check whether the inserted record already exists
		          
	        	//sSQL = "INSERT INTO " + TABLE_NAME + " VALUES (null, ?, ?, ?, ? ";
	        	//sSQL = "INSERT INTO " + TABLE_NAME + " VALUES (null, ?, ?, ? ";
//	        	sSQL = "INSERT OR REPLACE INTO " + TABLE_NAME + " (" + sColumnList + ") VALUES ((SELECT _id from " + TABLE_NAME + " where placename = '" + sPlaceName + "'), ?, ?, ? ";
	        	sSQL = "INSERT OR REPLACE INTO " + TABLE_NAME + " (" + sColumnList + ") VALUES ((SELECT _id from " + TABLE_NAME + " where placename = '" + sPlaceName + "'" + sRangeCondition + "), ?, ?, ? ";
	        	
	        	for (i=0; i<AL_Util.COMPARED_AP_COUNT; i++) {
	        		sSQL = sSQL + ",?,?,?,?,?";
	        	}
	        	
	        	sSQL = sSQL + ")";
	                    	
	        	m_db.execSQL(sSQL, sParams);
	        	
	    	}   
	    	
	        m_db.setTransactionSuccessful();  
	         
    	} finally {  
        	m_db.endTransaction();     
        }    

    }
    
    //Delete all records their place name = sPlaceName
    public void delete(String sPlaceName) {
    	String sSQL = "";
    	
    	m_db.beginTransaction();
    	
    	try {
    		m_db.delete(TABLE_NAME, "placename = ?", new String[]{sPlaceName} );
    	} finally {  
        	m_db.endTransaction();     
        }  
    	
    }
    

    public List<AL_PlaceAPInfo> query() {
    	String sSQL = "";
    	int i;
    	String sMAC_ColumnName = "";
    	String sLbRSS_ColumnName = "";
    	String sUbRSS_ColumnName = "";
    	String sMeanRSS_ColumnName = "";    	
    	String sOrder_ColumnName = "";
    	List<AL_PlaceAPInfo> lstPlaceAPInfo = new ArrayList<AL_PlaceAPInfo>();
    	boolean bHasRecord = false;
    	
    	sSQL = "SELECT * FROM " + TABLE_NAME;
    	Cursor c = m_db.rawQuery(sSQL, null);
    	
    	while (c.moveToNext()) {
    		AL_PlaceAPInfo placeAPInfo = new AL_PlaceAPInfo();
    		
    		placeAPInfo.setPlaceName(c.getString(c.getColumnIndex("placename")));
    		placeAPInfo.setGpsLat(c.getDouble(c.getColumnIndex("gpslat")));
    		placeAPInfo.setGpsLong(c.getDouble(c.getColumnIndex("gpslong")));
    		
    		List<AL_APInfoForDB> lstAPInfoForDB = new ArrayList<AL_APInfoForDB>();
    		
    		AL_APInfoForDB[] arrAPInfoForDB = new AL_APInfoForDB[AL_Util.COMPARED_AP_COUNT];
    		
    		for (i=1; i<=AL_Util.COMPARED_AP_COUNT; i++) {
    			arrAPInfoForDB[i-1] = new AL_APInfoForDB();
    			
    			sMAC_ColumnName = "mac" + i;
    			sLbRSS_ColumnName = "lbrss" + i;
    			sUbRSS_ColumnName = "ubrss" + i;
    			sMeanRSS_ColumnName = "meanrss" + i;    			
    			sOrder_ColumnName = "order" + i;
    			
    			arrAPInfoForDB[i-1].setMAC(c.getString(c.getColumnIndex(sMAC_ColumnName)));
    			arrAPInfoForDB[i-1].setLbRSS(c.getDouble(c.getColumnIndex(sLbRSS_ColumnName)));
    			arrAPInfoForDB[i-1].setUbRSS(c.getDouble(c.getColumnIndex(sUbRSS_ColumnName)));
    			arrAPInfoForDB[i-1].setMeanRSS(c.getDouble(c.getColumnIndex(sMeanRSS_ColumnName)));
    			arrAPInfoForDB[i-1].setOrder(c.getInt(c.getColumnIndex(sOrder_ColumnName)));
    			
    			lstAPInfoForDB.add(arrAPInfoForDB[i-1]);
        		
        		bHasRecord = true;
    		}
    
    		placeAPInfo.setAPInfoForDB(lstAPInfoForDB);
    		
    		lstPlaceAPInfo.add(placeAPInfo);
    	}
    	
    	c.close();
    	 
    	if (bHasRecord == false) {
    		return null;
    	} else {
    		return lstPlaceAPInfo;
    	}
    }
    
    
    public void closeDB() {
    	m_db.close();
    }

}
