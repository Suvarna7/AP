package com.empatica.sample.USB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

public class UsbReceiver extends BroadcastReceiver{
	 private static USBHost uHost;
	 

	 public UsbReceiver(){
		 
	 }
	 /* public UsbReceiver(Context ctx){
		 this.ctx=ctx;
		 
		IntentFilter usbFilter = new IntentFilter();
		 usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		 usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		 ctx.registerReceiver(this, usbFilter);
		 
	     Toast.makeText(ctx, "!! USB STARTED", Toast.LENGTH_LONG).show();

	 }*/
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    //System.out.println("BroadcastReceiver Event");

			String action = intent.getAction();
			System.out.println("BroadcastReceiver Event: Empatica");

			if (action !=null) {
				if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
					// Toast.makeText(context, "!! USB ATTACHED", Toast.LENGTH_LONG).show();

					System.out.println("BroadcastReceiver USB Connected");

				} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
					// Toast.makeText(context, "!! USB DETTACHED", Toast.LENGTH_LONG).show();

					System.out.println("BroadcastReceiver USB Disconnected");
				} else if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(action)) {
					if (uHost != null)
						if( !uHost.isConnected()) {
							// Toast.makeText(context, "!! USB DISCONNECTED", Toast.LENGTH_LONG).show();
							uHost.updateConnectedStatus("CONNECT", "START USB CONNECTION - Press CONNECT USB", true);
							uHost.disconnectUSBHost();


						} else {
							uHost.connected = false;
						}


				} else if ("android.intent.action.ACTION_POWER_CONNECTED".equals(action)) {
					if (uHost != null && !uHost.isConnected()) {
						//Toast.makeText(context, "!! USB CONNECTED", Toast.LENGTH_LONG).show();
						uHost.updateConnectedStatus("USB CONNECT", "START USB CONNECTION - Press CONNECT USB", true);
						uHost.disconnectUSBHost();


					}

				}
			}

		
	}
	
	public static void addUsbHost(USBHost host){
		uHost = host;
	}
}