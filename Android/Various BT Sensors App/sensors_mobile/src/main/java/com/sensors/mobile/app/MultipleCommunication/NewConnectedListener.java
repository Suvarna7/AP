package com.sensors.mobile.app.MultipleCommunication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import zephyr.android.BioHarnessBT.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewConnectedListener extends ConnectListenerImpl
{
	private boolean complete; 
	private String msg1;
	private String msg2;
	private String msg3;
	private String msg4;
	
	private Handler _aNewHandler;
	final int GP_MSG_ID = 0x20;
	final int BREATHING_MSG_ID = 0x21;
	final int ECG_MSG_ID = 0x22;
	final int RtoR_MSG_ID = 0x24;
	final int ACCEL_100mg_MSG_ID = 0x2A;
	final int SUMMARY_MSG_ID = 0x2B;
	
	
	
	//private int GP_HANDLER_ID = 0x20;
	
	private final int HEART_RATE = 0x100;
	private final int RESPIRATION_RATE = 0x101;
	private final int POSTURE = 0x103;
	private final int PEAK_ACCLERATION = 0x104;
	private final int VMU = 0x105;
	private final int ACTIVITY = 0x106;
	
	/*Creating the different Objects for different types of Packets*/
	private GeneralPacketInfo GPInfo = new GeneralPacketInfo();
	//private ECGPacketInfo ECGInfoPacket = new ECGPacketInfo();
	//private BreathingPacketInfo BreathingInfoPacket = new  BreathingPacketInfo();
	//private RtoRPacketInfo RtoRInfoPacket = new RtoRPacketInfo();
	//private AccelerometerPacketInfo AccInfoPacket = new AccelerometerPacketInfo();
	private SummaryPacketInfo SummaryInfoPacket = new SummaryPacketInfo();
	
	private PacketTypeRequest RqPacketType = new PacketTypeRequest();
	
	//Save zephyr values
	public static List<String> sensorValues = new ArrayList();

	public NewConnectedListener(Handler handler, Handler _NewHandler) {
		super(handler, null);
		_aNewHandler = _NewHandler;

		complete = true;
		msg1="";
		msg2="";

	}
	public void Connected(ConnectedEvent<BTClient> eventArgs) {
		System.out.println(String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));
		
		/*Use this object to enable or disable the different Packet types*/
		RqPacketType.GP_ENABLE = true;
		RqPacketType.BREATHING_ENABLE = true;
		RqPacketType.LOGGING_ENABLE = true;
		RqPacketType.ECG_ENABLE = true;
		RqPacketType.ACCELEROMETER_ENABLE= true;
		RqPacketType.EVENT_ENABLE = true;
		RqPacketType.RtoR_ENABLE = true;	
		RqPacketType.SUMMARY_ENABLE = true;
		RqPacketType.EnableEvent(true);
		RqPacketType.EnableGP(true);
		RqPacketType.EnableSummary(true);
		RqPacketType.EnableECG(true);
		RqPacketType.EnableLogging(true);
		
		
		//Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), RqPacketType);
		//ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), );
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
				ZephyrPacketArgs msg = eventArgs.getPacket();


				
				
				//byte CRCFailStatus = msg.getCRCStatus();
				//byte RcvdBytes = msg.getNumRvcdBytes() ;
				int MsgID = msg.getMsgID();
				byte [] DataArray = msg.getBytes();	
				switch (MsgID)
				{

				case GP_MSG_ID:
					
					
					//***************Displaying the Heart Rate********************************
					int HRate =  GPInfo.GetHeartRate(DataArray);
					Message text1 = _aNewHandler.obtainMessage(HEART_RATE);
					Bundle b1 = new Bundle();
					b1.putString("HeartRate", String.valueOf(HRate));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);

					//***************Displaying the Respiration Rate********************************
					double RespRate = GPInfo.GetRespirationRate(DataArray);
					
					text1 = _aNewHandler.obtainMessage(RESPIRATION_RATE);
					b1.putString("RespirationRate", String.valueOf(RespRate));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					
					
					
					//****************VMU***************************/
					double vmu = GPInfo.GetVMU(DataArray);
					text1 = _aNewHandler.obtainMessage(VMU);
					b1.putString("VMU", String.valueOf(vmu));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					
					//************* Activity **********************/
					//Avtivity Level
					String actLevel = "---";
					if (vmu<0.2)
						actLevel = "Low Activity";
					else if (0.2 <vmu && vmu< 0.8)
						actLevel = "Moderate Activity";
					else if(vmu >0.8)
						actLevel = "High Activity";
					
					text1 = _aNewHandler.obtainMessage(ACTIVITY);
					b1.putString("Activity", actLevel);
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);

					
					//***************Displaying the Posture******************************************					

				int PostureInt = GPInfo.GetPosture(DataArray);
				text1 = _aNewHandler.obtainMessage(POSTURE);
				b1.putString("Posture", String.valueOf(PostureInt));
				text1.setData(b1);
				_aNewHandler.sendMessage(text1);
				
				//***************Displaying the Peak Acceleration******************************************

				double PeakAccDbl = GPInfo.GetPeakAcceleration(DataArray);
				text1 = _aNewHandler.obtainMessage(PEAK_ACCLERATION);
				b1.putString("PeakAcceleration", String.valueOf(PeakAccDbl));
				text1.setData(b1);
				_aNewHandler.sendMessage(text1);
				
				//byte ROGStatus = GPInfo.GetROGStatus(DataArray);
				
				//**************** Get timestamp of the package ****************************
				//Calendar c = Calendar.getInstance();
				//int hour = c.get(Calendar.HOUR_OF_DAY);
				//int minute = c.get(Calendar.MINUTE);
				//int seconds = c.get(Calendar.SECOND);
				//int millis = c.get(Calendar.MILLISECOND);
				
				//**************** Get other values to save ***********************
				double vMin = GPInfo.GetY_AxisAccnMin(DataArray);
				double vPeak =  GPInfo.GetY_AxisAccnPeak(DataArray);
				double hMin = GPInfo.GetX_AxisAccnMin(DataArray);
				double hPeak =  GPInfo.GetX_AxisAccnPeak(DataArray);
				double zMin = GPInfo.GetZ_AxisAccnMin(DataArray);
				double zPeak =  GPInfo.GetZ_AxisAccnPeak(DataArray);
				double peakAcc =  GPInfo.GetPeakAcceleration(DataArray);
				double ECGAmpl =  GPInfo.GetECGAmplitude(DataArray);
				double ECGNoise =  GPInfo.GetECGNoise(DataArray);
				byte BatStatus =  GPInfo.GetBatteryStatus(DataArray);
				byte ConnStatus = GPInfo.GetBHSensConnStatus(DataArray);
				
		
					




				//**************** Save files ****************************************************
				msg1 = "("+PostureInt+ ", " +vmu +", "+HRate + ", " 
				+ RespRate+", " +vMin +", " +vPeak+", " +hMin+", " +hPeak+", " +zMin+", " +zPeak+", " +peakAcc+", "
						+ECGAmpl +", "+ ECGNoise;
				if (ConnStatus == 0)
					msg3 = ", "+ BatStatus +", 0" ;
				else
					msg3 = ", "+ BatStatus +", " +ConnStatus ;
				
				if (complete){
					//Wait for the other part of the message to complete
					complete = false;
				}
				else{
					sensorValues.add(msg1+msg2+msg3+msg4);
					complete = true;
				}
					
				/*sensorValues.add("("+hour+", "+minute+", "+seconds + "."+millis+", " +PostureInt+ ", " +vmu +", "+HRate + ", " 
				+ RespRate+", " +vMin +", " +vPeak+", " +hMin+", " +hPeak+", " +zMin+", " +zPeak+", " +peakAcc+", "
						+ECGAmpl +", "+ ECGNoise+" ,"+ BatStatus +"," +ConnStatus + ","+ ROGStatus +", no)");*/
				
					break;
				case BREATHING_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					//System.out.println("Breathing Packet Sequence Number is "+BreathingInfoPacket.GetSeqNum(DataArray));
					break;
				case ECG_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					//System.out.println("ECG Packet Sequence Number is "+ECGInfoPacket.GetSeqNum(DataArray));
					break;
				case RtoR_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					//("R to R Packet Sequence Number is "+RtoRInfoPacket.GetSeqNum(DataArray));
					break;
				case ACCEL_100mg_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					break;
				case SUMMARY_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					//**************** Get timestamp of the package ****************************
					/*Calendar c1 = Calendar.getInstance(); 
					int hour1 = c1.get(Calendar.HOUR_OF_DAY);
					int minute1 = c1.get(Calendar.MINUTE);
					int seconds1 = c1.get(Calendar.SECOND);
					int millis1 = c1.get(Calendar.MILLISECOND);*/
					
					String currentTimeStamp ="";
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
					currentTimeStamp = dateFormat.format(new Date());
				    String currentStamp = currentTimeStamp.substring(0,currentTimeStamp.length()-4);

				    //Timestamp timeS = Timestamp.valueOf(currentTimeStamp);
					//**************** Get other values to save ***********************
					//double HRate1 = SummaryInfoPacket.GetHeartRate(DataArray);
					double HConfidence =  SummaryInfoPacket.GetHeartRateRateConfidence(DataArray);
					double SConfidence = SummaryInfoPacket.GetSystemConfidence(DataArray);
					double InternalTemp =  SummaryInfoPacket.GetDevice_Internal_Temperature(DataArray);
					byte ROGStat = SummaryInfoPacket.GetROGStatus(DataArray);
					int ROGTime = SummaryInfoPacket.GetROGTime(DataArray);



					
					//**************** Save files ****************************************************
					msg2 =  ", " +HConfidence+", "+ SConfidence;
					msg4 = ", 0, 0, "+InternalTemp+ ", 0, " +ROGStat+", " +ROGTime+ ", '" + currentStamp+"', 'no')";
					
					if (complete){
						//Wait for the other part of the message to complete
						complete = false;
					}
					else{
						sensorValues.add(msg1+msg2+msg3+msg4);
						complete = true;
					}
					
					break;
					
				}
			}
		});
	}
	
}