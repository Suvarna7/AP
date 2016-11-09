package edu.virginia.dtc.APCservice.Database;

import android.content.Context;
import edu.virginia.dtc.APCservice.DataManagement.SensorsManager;

public class ACKThread extends Thread{
	
	private String last_updated;
	private String status;
	private IITDatabaseManager myDB;
	private Context dbContext;
	
	public ACKThread(IITDatabaseManager db, Context ctx){
		myDB = db;
		dbContext = ctx;
		last_updated = "";
		status = "";
	}
	
	public void run(){
		//Do the cummulative ACK
		try {
			myDB.ackSyncStatusAllPrevious(dbContext, SensorsManager._EMPATICA_TABLE_NAME,
					status, last_updated);
			
		}catch (Exception e){
			System.out.println("Exception when sync from USB: "+e);
		}
	}
	
	public void setLastUpdated(String update){
		last_updated = update;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

}
