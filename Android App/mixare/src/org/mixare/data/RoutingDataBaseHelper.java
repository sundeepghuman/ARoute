package org.mixare.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class RoutingDataBaseHelper extends SQLiteOpenHelper 
{
	//
	// CLASS AND INSTANCE DATA
	//
	//The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/org.mixare/databases/";
 
    private static String DB_NAME = "routing_data";
 
    private SQLiteDatabase mDataBase; 
 
    private final Context mContext;
	
	
	public RoutingDataBaseHelper(Context context) 
	{
		super(context, DB_NAME, null, 1);
		mContext = context;
	}

	public void createDataBase() throws IOException
	{
		 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist)
    	{
    		//do nothing - database already exist
    	}
    	else
    	{
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try 
        	{
    			copyDataBase();
    		} 
        	catch (IOException e) 
    		{
        		throw new Error("Error copying database");
        	}
    	}
    }
	
	public void openDataBase() throws SQLException
	{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
	
	private void copyDataBase() throws IOException
	{
    	//Open your local db as the input stream
    	InputStream myInput = mContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0)
    	{
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
	
	private boolean checkDataBase()
	{
    	SQLiteDatabase checkDB = null;
 
    	try
    	{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}
    	catch(SQLiteException e){}
 
    	if(checkDB != null)
    	{
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
	
	
	@Override
	public synchronized void close() 
	{
		if(mDataBase != null)
    		mDataBase.close();
    	
		super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
