package com.empatica.sample.USB;

import com.empatica.sample.BGService;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.Server.IITServerConnector;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Cat on 2/21/2017.
 */
public class USBMessageSender {

    /* *********************************
     LIST OF COMMANDS
     */

    //Commands
    public static String _GET_DATA = "get_values";
    public static String _GET_ALL = "get_all";
    public static String _GET_ALL_NO_SYNC = "get_all_no_sync";
    public static String _START_SENDING = "first_value";

    public static String _END_COMMAND = "next_end";
    public static String _NO_DATA = "no_data";
    public static String _ACK_SYNCHRONIZED = "usb_sync";
    public static String _CONNECTION_ESTABLISHED = "connection_process_end";
    public static String _CONNECTION_END = "end_connection";
    public static String _WRONG_COMMAND = "wrong_command";

    public static String _TEST_USB= "verify_usb";
    public static String _ACK_TEST_USB= "ACK_usb";

    public static String _TEST_DEVICE= "verify_device";
    public static String _VERIFY_DEVICE_CONNECTED= "device_connected";
    public static String _VERIFY_DEVICE_DISCONNECTED= "device_disconnected";

    //Sensor information
    public static final String _SENSOR_ID = "sensor_table";
    public static final String _EMPATICA = "empatica";
    public static final String _DEXCOM = "dexcom";

    //Max number of samples per JSON object (packet) to send via USB
    private static final int LOCAL_SENDING_AMOUNT = 200;

    //Socket out
    public PrintWriter socketOut;
    public Socket client = null;

    public USBMessageSender (PrintWriter socket, Socket client){
        socketOut = socket;
        this.client = client;
    }


    /**
     * Get all no sync values, and return a list of the JSON Strings to be sent
     *
     * @return
     */
    public void messageAllAsync(String table) {
        messageNAsync(table, IITDatabaseManager.MAX_READ_SAMPLES_SYNCHRONIZE);
    }

    /**
     * Message N samples of the async values
     *
     * @param table
     * @param samples
     * @return
     */

    public void messageNAsync(String table, int samples) {
        //Get last not SYNCHRONIZED values (Not sent via USB)
        messageAsync(table, IITDatabaseManager.syncColumn, IITDatabaseManager.syncStatusNo, samples, true);

    }

    //**************************************************************
    // Read, send adn received messages


    /**
     * Send the list of values via USB
     * Conver the list into JSON messages
     * @param values List<Map<String, String></>
     */
    public void  sendUSBList(List<Map<String, String>> values, String table_name){
        int n = values.size();
        int max_jsons = 1+(int)Math.ceil(n/LOCAL_SENDING_AMOUNT);

        //Send to Server - CUT INTO SMALLER PIECES TOO
        List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
        //List too long: break in smaller chunks
        int sent = 0;
        for (int i = 0; i < n && sent < max_jsons-1; i++) {
            Map<String, String> val = values.get(i);
            val.put("table_name", table_name);
            temp.add(val);
            if ((i + 1) % LOCAL_SENDING_AMOUNT == 0) {
                //System.out.println("Save temp");

                //SEND RIGHT HERE
                sendUSBmessage(IITServerConnector.convertToJSON(temp));

                //result[sent] = IITServerConnector.convertToJSON(temp);
                sent++;
                temp = new ArrayList<Map<String, String>>();
                //System.out.println("Saved");
            }
        }
        //SEND RIGHT HERE
        if (temp != null && !temp.isEmpty()) {
            sendUSBmessage(IITServerConnector.convertToJSON(temp));

        }else{
            //DO NOTHING
        }

        //Remaining
        //result[sent]= IITServerConnector.convertToJSON(temp);
    }

    public void sendUSBmessage(String msg) {
        if (socketOut != null) {
            System.out.println("send msg - "+msg);
            //socketOut.print(msg);
            socketOut.println(msg);
            socketOut.flush();
        } else {
            System.out.println("msg not sent " + client);
            /*try {
                socketOut = new PrintWriter(client.getOutputStream(), true);
                socketOut.println(msg +" \n");
                socketOut.flush();
            }catch(Exception e){
                System.out.println("Error trying to start socket out again: "+e);
            }*/
        }


    }

    /**
     * Get all no sync values, and return a list of the JSON Strings to be sent
     * @return
     */
    public void messageAsync(String table, String check_column, String check_value, int max, boolean ACK) {
        //TODO NO ACK NOW
        BGService.ackInProgress = true;

        List<Map<String, String>> listReadToUSB = null;
        if(ACK){
            while (listReadToUSB == null)
                listReadToUSB=  BGService.storingManager.myDB.getNotCheckedValues(table, BGService.columnsTable, check_column, check_value, max, true);
            //listReadToUSB = BGService.storingManager.myDB.getLastNSamples(table, BGService.columnsTable, max);
            Collections.reverse(listReadToUSB);
        }
        else{
            //listReadToUSB = BGService.storingManager.myDB.getLastNSamples(table, BGService.columnsTable, max);
            while (listReadToUSB == null)
                listReadToUSB=  BGService.storingManager.myDB.getNotCheckedValues(table, BGService.columnsTable, check_column, check_value, max, true);
            Collections.reverse(listReadToUSB);
        }


        if (listReadToUSB !=null ){
            sendUSBList(listReadToUSB, table);
            sendUSBmessage(_END_COMMAND);


        } else
            sendUSBmessage(_NO_DATA);

        BGService.ackInProgress = false;

    }
}
