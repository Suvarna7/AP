package edu.virginia.dtc.APCservice.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("hiding")
public class ThreadSafeArrayList<String> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final List<String> list = new ArrayList<String>();



    public void set(String o){
        writeLock.lock();
        try{
            list.add(o);

        }finally {
            writeLock.unlock();
        }
    }

    public String get(int i){
        readLock.lock();
        try{
            return list.get(i);
        }finally{
            readLock.unlock();
        }
    }

    public void clear(){
        writeLock.lock();
        try{
            list.clear();

        }finally {
            writeLock.unlock();
        }
    }

    public int size(){
        readLock.lock();
        try{
            return list.size();
        }finally{
            readLock.unlock();
        }

    }


}
