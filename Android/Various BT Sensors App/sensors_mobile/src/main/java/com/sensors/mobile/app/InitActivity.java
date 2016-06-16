package com.sensors.mobile.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.Database.DataStoring;
import com.sensors.mobile.app.MultipleCommunication.MainActivityMultiple;
import com.sensors.mobile.app.zephyr.MainActivityZephyr;
import com.sensors.mobile.app.Dexcom.G4DevKitTestAppActivity;


import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class InitActivity extends Activity implements AdapterView.OnItemSelectedListener {
	//Responses
	private  String secondaryID;
	private  String userId;
	private  EditText editUserId;
	private  EditText editSecondaryID;
	private String selectedDevice;
	private TextView secondaryInput;

	private Class c;
	private int actType;

	public static boolean startStoring;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);

		startStoring = false;

		editUserId = (EditText)findViewById(R.id.patientId);
		editSecondaryID = (EditText)findViewById(R.id.deviceId);
		actType = 1;

		secondaryInput = (TextView)findViewById(R.id.secondaryLabel);

	      /*Obtaining the handle to act on the DONE button for the PATIENT*/
		Button btnPatient = (Button) findViewById(R.id.ButtonPatient);

		btnPatient.setOnClickListener(new OnClickListener() {
			@Override
			  /*Functionality to act if the button DONE is touched*/
			public void onClick(View v) {
				//Get info
				userId = editUserId.getText().toString();
				//Move mouse to next text edit
			}
		});

		  /*Obtaining the handle to act on the DONE button for the ZEPHYR*/
		Button btnZephyr = (Button) findViewById(R.id.ButtonZephyr);

		btnZephyr.setOnClickListener(new OnClickListener() {
			@Override
			  /*Functionality to act if the button DONE is touched*/
			public void onClick(View v) {
				//Get info
				secondaryID = editSecondaryID.getText().toString();

				//Move mouse to next text edit
			}
		});

		  /* Selecting One Device Menu */
		// Spinner element
		Spinner spinner = (Spinner) findViewById(R.id.spinner);

		// Spinner click listener
		spinner.setOnItemSelectedListener(this);

		// Spinner Drop down elements
		List<String> categories = new ArrayList<String>();

		categories.add("zephyr");
		categories.add("BodyMedia");
		//TODO Deleted
		categories.add("DexCom");

		categories.add("ALL");



		// Creating adapter for spinner
		ArrayAdapter <String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);

		  /*Obtaining the handle to act on the GO*/
		Button btnGo = (Button) findViewById(R.id.ButtonGo);
		btnGo.setOnClickListener(new OnClickListener() {
			@Override
			  /*Functionality to act if the button DONE is touched*/
			public void onClick(View v) {
				//Get data in case DONE button was not pressed
				//Check that Patient ID and zephyr are not null
				userId = editUserId.getText().toString();
				secondaryID = editSecondaryID.getText().toString();
				if (secondaryID != null && userId != null && selectedDevice !=null &&secondaryID != "" && userId != ""
						&& selectedDevice !="") {
					//Go to the zephyr main activity
					//Pass the values of zephyr and patient
					changeActivity(c);

				} else {
					userId = editUserId.getText().toString();
					secondaryID = editSecondaryID.getText().toString();
				}
			}

		});
	}





	public void changeActivity(Class c){
		System.out.println("Device initActivity: "+secondaryID);
		Intent intent = new Intent(this, c);
		intent.putExtra("TABLE_ID", userId);
		switch(actType) {
			case 1:
				intent.putExtra("DEVICE_SERIAL_NUM", secondaryID);
				break;
			case 2:
				intent.putExtra("EXPERIMENT_ID", secondaryID);
				break;

		}
		startActivity(intent);



	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// On selecting a spinner item
		String item = parent.getItemAtPosition(position).toString();
		selectedDevice = item;
		if (item.equals("zephyr")){
			c = MainActivityZephyr.class;
			actType = 1;
			secondaryInput.setText("Insert device serial number:");
		}else if (item.equals ("BodyMedia")){
			c = MainActivityBM.class;
			actType = 2;
			secondaryInput.setText("Insert Experiment ID:");

		}else if(item.equals("DexCom")){
			c = G4DevKitTestAppActivity.class;
			actType = 3;

		}else if(item.equals("ALL")) {

			//By default: go to zephyr
			c = MainActivityMultiple.class;
			actType = 4;

		}else{
			//By default: go to zephyr
			c = MainActivityZephyr.class;


		}
		// Showing selected spinner item
		Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
