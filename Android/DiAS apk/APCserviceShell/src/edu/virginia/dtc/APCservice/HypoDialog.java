package edu.virginia.dtc.APCservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;

public class HypoDialog extends Activity {
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
		 	//Get carbs and type values
		    Intent intent = getIntent();
		 	int carb_amount = 0;
		 	String type = "NONE";
		 	try{
		 		type = intent.getStringExtra("alarm_type");
		 		carb_amount = intent.getIntExtra("carbs", 0);
		 	}catch(Exception e){
		 		System.out.println("Exception while extracting alarm values");
		 	}
		 	
		 	
		 	
	        super.onCreate(savedInstanceState);
	        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        alertDialog.setTitle("Hypo Alarm: "+ type+" alarm");
	        alertDialog.setMessage(" Consume " +carb_amount+" g of hydrocarbs");
	        alertDialog.setCancelable(true);
	        
	        //Vibrate
	        Vibrator vibrator;
	        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	        vibrator.vibrate(500);

	        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            @Override
	            public void onCancel(DialogInterface dialog) {
	               
	                

	                //Start service  again

	                //Finish the activity
	                finish();
	            }

	    });
	    //alertDialog.setIcon(R.drawable.icon);

	    alertDialog.show();


	    }

}
