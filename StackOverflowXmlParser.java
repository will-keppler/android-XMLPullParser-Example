package com.androidprogramming.will.xmlppexample;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by will on 3/19/15.
 */
public class StackOverflowXmlParser {
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        Log.i("StackOverflowXMLP(); ", "parse()");
        Log.i("StackOverflowXMLP(); ", "parse(); in = " + in.toString());
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            Log.i("StackOverflowXMLP(); ", "parse(); before .setInput");
            parser.setInput(in, null);
            Log.i("StackOverflowXMLP(); ", "parse(); after .setInput");
            Log.i("StackOverflowXMLP(); ", "parse(); parser.nextTag() = " + parser.getEventType() + " " + parser.getDepth());
            parser.nextTag();
            return readFeed(parser);
        }finally {
            in.close();
        }
    }//end parse()

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        Log.i("StackOverflowXMLP(); ", "reedFeed()");
        //Log.i("StackOverflowXMLP(); ", "reedFeed(); xmlpp.endtag = " + XmlPullParser.END_TAG);// == 3
        //Log.i("StackOverflowXMLP(); ", "reedFeed(); xmlpp.starttag = " + XmlPullParser.START_TAG);// ==2
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            //Start by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            }else {
                skip(parser);
            }
        }//end while
        return entries;
    }//end readFeed()

    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;

        private Entry(String title, String summary, String link) {
            this.title = title;
            this.summary = summary;
            this.link = link;
        }//end Entry() constructor

        public String toString() {
            String s = "Title: " + this.title + " | Summary: " + this.summary + " | Link: " + this.link;
            return s;
        }

    }//end class Entry

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;

        Log.i("StackOverflowXMLP(); ", "reedEntry()");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if(name.equals("title")){
                title = readTitle(parser);
            } else if(name.equals("summary")){//summary
                summary = readSummary(parser);
            } else if (name.equals("link")){
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }//end while
        return new Entry(title, summary, link);
    }//end readEntry()

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.i("StackXmlParser();", "readTitle()");
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }// end readTitle()

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.i("StackXmlParser();", "readLink()");
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }// end readLink()

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.i("StackXmlParser();", "readSummary()");
        parser.require(XmlPullParser.START_TAG, ns, "summary");//summary
        String summary = readText(parser);
        Log.i("StackXmlParser(); ", "readSUmmary(); parser.getName = " + parser.getName());
        if (parser.getName() != "summary") {//summary
            summary = readText(parser);
            //since <p is the only tag in <summary; set summary to the text in the <p tag
        }
        //parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }//end readSummary()

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.i("StackXmlParser();", "readText()");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }//end readText()

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        Log.i("StackOverflowXMLP(); ", "skip()");

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    Log.i("StackOverflowXMLP(); ", "skip(); xmlpp.end tag depth--");
                    Log.i("StackOverflowXMLP(); ", "skip(); parser = " + parser);
                    Log.i("StackOverflowXMLP(); ", "skip(); parser.getName = " + parser.getName());
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }//end skip()

}
