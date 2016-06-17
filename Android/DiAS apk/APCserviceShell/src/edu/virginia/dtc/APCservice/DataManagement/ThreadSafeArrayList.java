package edu.virginia.dtc.APCservice.DataManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ThreadSafeArrayList - ArrayList implementation to avoidd conflicts when different Threads are
 * accessing
 */
public class ThreadSafeArrayList<String> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final List<String> list = new ArrayList<String>();


    /**
     * Write a new values in the Array
     * @param o
     */
    public void set(String o){
        writeLock.lock();
        try{
            list.add(o);

        }finally {
            writeLock.unlock();
        }
    }

    /**
     * Get value at a given index
     * @param i index
     * @return String values
     */
    public String get(int i){
        readLock.lock();
        try{
            return list.get(i);
        }finally{
            readLock.unlock();
        }
    }

    /**
     * Clear array
     */
    public void clear(){
        writeLock.lock();
        try{
            list.clear();

        }finally {
            writeLock.unlock();
        }
    }

    /**
     * Get size of the array
     * @return size
     */
    public int size(){
        readLock.lock();
        try{
            return list.size();
        }finally{
            readLock.unlock();
        }

    }


}
