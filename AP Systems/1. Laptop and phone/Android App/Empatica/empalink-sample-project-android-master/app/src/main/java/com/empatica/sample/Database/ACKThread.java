package com.empatica.sample.Database;

import com.empatica.sample.BGService;

/**
 * Created by Cat on 3/17/2017.
 */
public class ACKThread implements Runnable {

    private IITDatabaseManager db;
    private String table;
    private String status;
    private String last_stamp;

    public ACKThread(){

    }
    @Override
    public void run() {
        //Call garbage collector
        System.gc();

        //ACK
        db.ackSyncStatusAllPrevious(BGService.empaticaTableName,status, last_stamp);
    }

    public void presetRunningParams(IITDatabaseManager db, String table,  String status, String last_up){
        this.db = db;
        this.table = table;
        this.status = status;
        this.last_stamp = last_up;
    }
}
