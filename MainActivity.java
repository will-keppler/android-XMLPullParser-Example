package com.androidprogramming.will.xmlppexample;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    List<StackOverflowXmlParser.Entry> entries = null;
    StackOverflowXmlParser sop = new StackOverflowXmlParser();
    InputStream in = null;//may not need this if changes work...
    BufferedReader buffReader = null;
    StringBuilder text = new StringBuilder();
    FileInputStream fInStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("onCreate(); ", "right after setContentView");
        //Need an InputStream to pass to the StackOverflowXmlParser constructor
        //Store return value in a List() object...

        //File file = new File("/data/local/tmp", "test.xml");
        //Log.i("OnCreate(); ", "after File creation; file.length() = " + file.length());

        try {
            fInStr = openFileInput("test.xml");
            Log.i("OnCreate(); ", "after File creation; file.length() = " + fInStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
/*
        try{
            buffReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = buffReader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            //buffReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } //Removed the finally call here because it was closing the in stream before i can use it in SOFP
        catch (IOException e) {
            e.printStackTrace();
        }
*/

        Log.i("onCreate(); ", "Creating the InputStream passed");
        Log.d("onCreate(); ", "text.substring(0,15) = " + text.substring(0));

        Thread thread = new Thread(null, startParsing, "Background");
        thread.start();


    }//end onCreate()

    private Runnable startParsing = new Runnable() {
        public void run() {
            try {
                entries = sop.parse(fInStr);
                Log.v("startParsing(); ", "after the call to .parse; entries = " + entries);
                Log.v("startParsing(); ", "after the call to .parse; entries.isEmpty = " + entries.isEmpty());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }//Need both catch clauses because the SOP class throws those two exceptions.
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



