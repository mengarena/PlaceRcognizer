package com.al;

import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper; 

public class AL_DBHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "PlaceAPInfo.db"; 
	private static final String TABLE_NAME = "PlaceAPInfo";
    private static final int DATABASE_VERSION = 1;  
      
    public AL_DBHelper(Context context) {  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {
    	//recordmark is used to identify it is an old record (i.e. downloaded from server) or a new record (recorded by the end-user)
    	String sTableFields = "placename TEXT, gpslat REAL, gpslong REAL, " +     
    						"mac1 TEXT, lbrss1 REAL, ubrss1 REAL, meanrss1 REAL, order1 INTEGER,  " +
    						"mac2 TEXT, lbrss2 REAL, ubrss2 REAL, meanrss2 REAL, order2 INTEGER,  " +
    						"mac3 TEXT, lbrss3 REAL, ubrss3 REAL, meanrss3 REAL, order3 INTEGER,  " +
    						"mac4 TEXT, lbrss4 REAL, ubrss4 REAL, meanrss4 REAL, order4 INTEGER,  " +
    						"mac5 TEXT, lbrss5 REAL, ubrss5 REAL, meanrss5 REAL, order5 INTEGER,  " +
    						"mac6 TEXT, lbrss6 REAL, ubrss6 REAL, meanrss6 REAL, order6 INTEGER,  " +
    						"mac7 TEXT, lbrss7 REAL, ubrss7 REAL, meanrss7 REAL, order7 INTEGER,  " +
    						"mac8 TEXT, lbrss8 REAL, ubrss8 REAL, meanrss8 REAL, order8 INTEGER,  " +
       						"mac9 TEXT, lbrss9 REAL, ubrss9 REAL, meanrss9 REAL, order9 INTEGER,  " +
       						"mac10 TEXT, lbrss10 REAL, ubrss10 REAL, meanrss10 REAL, order10 INTEGER, "  +
							"mac11 TEXT, lbrss11 REAL, ubrss11 REAL, meanrss11 REAL, order11 INTEGER,  " +
							"mac12 TEXT, lbrss12 REAL, ubrss12 REAL, meanrss12 REAL, order12 INTEGER,  " +
							"mac13 TEXT, lbrss13 REAL, ubrss13 REAL, meanrss13 REAL, order13 INTEGER,  " +
							"mac14 TEXT, lbrss14 REAL, ubrss14 REAL, meanrss14 REAL, order14 INTEGER,  " +
							"mac15 TEXT, lbrss15 REAL, ubrss15 REAL, meanrss15 REAL, order15 INTEGER,  " +
							"mac16 TEXT, lbrss16 REAL, ubrss16 REAL, meanrss16 REAL, order16 INTEGER,  " +
							"mac17 TEXT, lbrss17 REAL, ubrss17 REAL, meanrss17 REAL, order17 INTEGER,  " +
							"mac18 TEXT, lbrss18 REAL, ubrss18 REAL, meanrss18 REAL, order18 INTEGER,  " +
							"mac19 TEXT, lbrss19 REAL, ubrss19 REAL, meanrss19 REAL, order19 INTEGER,  " +
							"mac20 TEXT, lbrss20 REAL, ubrss20 REAL, meanrss20 REAL, order20 INTEGER";
    	
    	//String sSQL = "DROP TABLE " + TABLE_NAME;
        //db.execSQL(sSQL);  

    	String sSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + sTableFields + " )";
    	
        db.execSQL(sSQL);  
    	
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  

    }  

}
