package edu.virginia.dtc.APCservice.DataManagement;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import edu.virginia.dtc.APCservice.IOMain;
import edu.virginia.dtc.APCservice.Database.IITDatabaseManager;
import edu.virginia.dtc.APCservice.Database.ThreadSafeArrayList;
import edu.virginia.dtc.APCservice.Server.IITServerConnector;
import edu.virginia.dtc.SysMan.Controllers;
import edu.virginia.dtc.SysMan.Event;
import edu.virginia.dtc.SysMan.Params;

/**
 * SubBolusCreator is a class to manage the insulin request messages to DiAS system
 * It allows boluses greater than 6U
 * It prepares the data to be send to IIT server
 * 
 * @author Caterina Lazaro
 * @version 1.0, February 2016 
 */
public class SubBolusCreator {
	//Maximum amount of bolus to send
	private final static double max_bolus_sent = 6;
	
	
	//Time to wait before sending the next subbolus
	private final static int _WAIT_TIME = 61000;
	
	//Database manager:
	IITDatabaseManager mDB;
	//Bolus values
	private final static String _BOLUS_TABLE_NAME = "bolus_table";
	private final static String[] bolusColumns = {"time_stamp", "bolus_commanded", "command_num", "total_units_insulin" };
	//Basal values
	private final static String _BASAL_TABLE_NAME = "bolus_table";
	private final static String[] basalColumns = {"time_stamp", "changed", "basal_rate_value" };
	
	public SubBolusCreator(IITDatabaseManager db){
		mDB = db;
		
		//Init bolus and basal tables
		mDB.createTable(_BOLUS_TABLE_NAME, bolusColumns[0], new ArrayList<String>(Arrays.asList(bolusColumns)));
		mDB.createTable(_BASAL_TABLE_NAME, basalColumns[0], new ArrayList<String>(Arrays.asList(basalColumns)));
	}


	/**
	 * handleBolusValue - given the total insuling it will
	 * Handle boluses: correction bolus, divide it according to the max bolus accepted,
	 * send the subboluses to DiAS service and prepare the messages to be send to IIT server
	 * Handle basal rates: request basal
	 * @param correction - insulin bolus
	 * @param new_rate - differential rate boolean
	 * @param diff_rate - value of diff rate 
	 * @param asynchronous 
	 * @param cr - content resolver to be used in the sent messages
	 * @param ctx - context
	 * @return
	 */
	public  List<Map<String, String>> handleInsulinValue(boolean bol_correction, double correction, boolean do_rate, boolean new_rate, double diff_rate, boolean asynchronous, ContentResolver cr, Context ctx){
		List<Map<String, String>> rValues = new ArrayList<Map<String, String>> ();
		if(bol_correction && do_rate){
			//Call another function
		}
		else if(bol_correction){
			rValues = handleBolusValue( correction,  asynchronous,  cr,  ctx);
		}
		else if (do_rate){
			rValues = handleBasalRateValue( new_rate, diff_rate,  asynchronous,  cr,  ctx);

		}
		
		return rValues;

	}
	/**
	 * Handle a new basal rate value
	 * @param new_diff - whether it is a new basal rate value
	 * @param d_rate - basal rate value
	 * @param asynchronous
	 * @param cr - ContentResolver
	 * @param ctx - Context
	 * @return
	 */
	public  List<Map<String, String>> handleBasalRateValue(boolean new_diff, double d_rate, boolean asynchronous, ContentResolver cr, Context ctx){
		Message response = Message.obtain(null, Controllers.APC_PROCESSING_STATE_NORMAL, 0, 0);;

		//Send last message
		processInsulinCommand(ctx, 0, 0, 0 , response, "Basal_rate", cr, false,  false, new_diff,   d_rate,  asynchronous);
		processInsulinCommand(ctx, 0, 0, 0, response, "Basal_rate", cr, false,  true, false,   0,  asynchronous);

		//IIT values
		preparedIITInsulinRateValues(new_diff, d_rate);
		
		//Prepare for IIT server
		List<Map<String, String>> iTable = mDB.getNotUpdatedValues(_BASAL_TABLE_NAME, basalColumns,
				 IITDatabaseManager.upDateColumn, IITDatabaseManager.updatedStatusNo);
		
		//Add table name:
		List<Map<String, String>> rTable = new ArrayList<Map<String, String>>();
		for (Map<String, String> sample: iTable){
			sample.put("table_name", _BASAL_TABLE_NAME);
			rTable.add(sample);
		}
				
		return rTable;
	}

	/**
	 * 
	 * @param correction
	 * @param asynchronous
	 * @param cr
	 * @param ctx
	 * @return
	 */
	public  List<Map<String, String>> handleBolusValue(double correction, boolean asynchronous, ContentResolver cr, Context ctx){
		//Depending on bolus_corr value, we will send N messages
		//Divide the bolus in groups of 6 (MAX_CORR = 6)

		//Number of messages with bolus = 6:
		int N =  0;
		//Bolus value for the last message to send
		double rest = 0;
		//Whether there is bolus or not
		boolean bolusResp = true;

		if (correction != 0){
			/*Double  n_prev= (correction/max_bolus_sent);
			N = Integer.valueOf(n_prev.intValue());*/
			N = (int)(correction/max_bolus_sent);
			rest = correction%max_bolus_sent;
			bolusResp = true;

		}else{
			bolusResp = false;
		}
		Message response = Message.obtain(null, Controllers.APC_PROCESSING_STATE_NORMAL, 0, 0);;
		
		//Send the messages:
		for (int i = 0; i < N; i ++){
			
			/* Values for the first N messages: recommended 6 units
			   1. Prepare the sub bolus to be sent to Dias Service
			   2. Value will be save in IIT server too */
			
			//Send the desired subbolus
			processInsulinCommand(ctx, correction, max_bolus_sent, i, response, "Sub_bolus", cr,  bolusResp,  false, false,  0,  asynchronous);
			
			//IIT values
			preparedIITInsulinBolusValues(correction, max_bolus_sent, i);
			
			//Wait before sending next message
			try {
				//30 seconds - Roche needs that much
				Thread.sleep(_WAIT_TIME);
				
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}catch(Exception e){
				System.out.println("Something went wrong with thread.sleep");
			}
		}
		
		//Send last message
		processInsulinCommand(ctx, correction, rest, N, response, "Sub_bolus", cr, bolusResp,  false, false, 0,  asynchronous);
		preparedIITInsulinBolusValues(correction, rest, N);

		//IIT values
		//args.add(preparedIITInsulinBolusValues(correction, rest, N));
		//Prepare for IIT server
		List<Map<String, String>> iTable = mDB.getNotUpdatedValues(_BOLUS_TABLE_NAME, bolusColumns,
				 IITDatabaseManager.upDateColumn, IITDatabaseManager.updatedStatusNo);
		
		//Add table name:
		List<Map<String, String>> rTable = new ArrayList<Map<String, String>>();
		for (Map<String, String> sample: iTable){
			sample.put("table_name", _BOLUS_TABLE_NAME);
			rTable.add(sample);
		}
		
		//Return values to send to IIT
		return rTable;

	}
	
	/* *************************************************************
	 * FUNCTIONS TO HANDLE BOLUS REQUESTS
	 */


	/**
	 * Process sub bolus - commands small bolus and send this value to IIT server
	 * @param total - bolus total amount the algorithm recommends
	 * @param bolus - sub bolus amount
	 * @param num - order of the sub bolus
	 * @param iArgs - List were data is organized
	 * @param responseBundle - params of the bundle to send bolus command
	 * @param response - message to command bolus
	 * @param FUNC_TAG - to use in debug
	 */

							

	private  Message processInsulinCommand(Context context, double total, double bolus, int num, Message response, String FUNC_TAG, ContentResolver cr,
											boolean bolusResp,boolean rateResp, boolean new_rate, double diff_rate, boolean asynchronous){
		//Initial parameters
		Message result = response;
		Bundle responseBundle;

		//Values for 6 U messages 
		responseBundle = new Bundle();
		responseBundle.putBoolean("doesBolus", bolusResp);
		responseBundle.putBoolean("doesRate", rateResp);
		// We add it later? 
		responseBundle.putDouble("recommended_bolus", bolus);
		responseBundle.putBoolean("new_differential_rate", new_rate);
		responseBundle.putDouble("differential_basal_rate", diff_rate);
		responseBundle.putDouble("IOB", 0.0);
		responseBundle.putBoolean("asynchronous", asynchronous);

		//Log to I/O Test
		if (Params.getBoolean(cr, "enableIO", false)) {
			Bundle b = new Bundle();
			b.putString(	"description", 
					" SRC:  APC"+
							" DEST: DIAS_SERVICE"+
							" -"+FUNC_TAG+"-"+
							" APC_PROCESSING_STATE_NORMAL"+
							" doesBolus="+responseBundle.getBoolean("doesBolus")+
							" doesRate="+responseBundle.getBoolean("doesRate")+
							" recommended_bolus="+responseBundle.getDouble("recommended_bolus")+
							" new_differential_rate="+responseBundle.getBoolean("new_differential_rate")+
							" differential_basal_rate="+responseBundle.getDouble("differential_basal_rate")+
							" IOB="+responseBundle.getDouble("IOB")
					);
			Event.addEvent(context, Event.EVENT_SYSTEM_IO_TEST, Event.makeJsonString(b), Event.SET_LOG);
		}      
		
		// Add responseBundle in DiAsService
		result.setData(responseBundle);

		//Send response to DiAS service
		IOMain.sendResponse(result);
		
		
		//Return the list to send to IIT
		return result;

	}
	
	private  Map<String, String>  preparedIITInsulinRateValues(boolean new_rate, double rate_value){
		if (rate_value>=0){
			//Add to database
			ThreadSafeArrayList<String> mList = new ThreadSafeArrayList<String>();
			String time = IITServerConnector.getCurrentTime();
			mList.set("'"+time+"'");
			mList.set(""+new_rate);
			mList.set(""+rate_value);
			mDB.updateDatabaseTable(_BASAL_TABLE_NAME, mList, true);
			Map<String, String> iTable = new HashMap<String, String>();
			//Insulin values
			//iTable.clear();
			iTable.put(basalColumns[1],""+new_rate);
			iTable.put(basalColumns[2],""+rate_value);
			//Table name and time
			iTable.put("table_name", _BASAL_TABLE_NAME);
			iTable.put(basalColumns[0], time);

			return iTable;
		}else{
			return null;
		}
	}

	/**
	 * preparedIITInsulinValues - Prepare the messages to be send to IIT with
	 * bolus information
	 * @param total - correction bolus
	 * @param bolus - sub bolus accepted by DiAS (<6U)
	 * @param num - bolus position in the total amount
	 * @return
	 */
	
	private  void  preparedIITInsulinBolusValues(double total, double bolus, double num){
		//TODO Save values in local database
		
		//Prepare insulin values for IIT
		if (total>= 0 || bolus >=0){
			String time = IITServerConnector.getCurrentTime();

			//Add to database
			ThreadSafeArrayList<String> mList = new ThreadSafeArrayList<String>();
			mList.set("'"+time+"'");
			mList.set(""+bolus);
			mList.set(""+num);
			mList.set(""+total);
			mDB.updateDatabaseTable(_BOLUS_TABLE_NAME, mList, true);
			
			

		}else{
		}
	}

}
