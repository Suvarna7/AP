package com.ece.iit;

import android.app.Activity;
import android.os.Bundle;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Include our folder lib in the list of libPaths
        //1. Get current locations
        String libPathProperty = System.getProperty("java.library.path");
        System.out.println(libPathProperty);
        //2. Add /lib and set 
        String pathToLib = this.getApplicationInfo().dataDir + "/lib";
        libPathProperty += ":"+pathToLib +":./lib";
        System.out.println(libPathProperty);

        //3. Set the new libraries path
        System.setProperty("java.library.path", libPathProperty);
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
            fieldSysPath.setAccessible( true );
            fieldSysPath.set( null, null );
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        //4.Load library
        System.loadLibrary("libjipopt.so");
        
        //Run the optimization
        // Create the problem
        HS071 hs071 = new HS071();
                
                // Get the default initial guess
                //double x[] = hs071.getInitialGuess();
                
                // solve the problem
                //hs071.solve(x);
    }
}
