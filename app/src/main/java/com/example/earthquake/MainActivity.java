package com.example.earthquake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    ListView earthquakeListView;
    ArrayAdapter<Quake> aa;
    ArrayList<Quake> earthquakes = new ArrayList<>();
    private static final String TAG = "EARTHQUAKE";

    ArrayList<Double> latitudes = new ArrayList<>();
    ArrayList<Double> longitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        earthquakeListView = (ListView)this.findViewById(R.id.list);
        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<>(this, layoutID , earthquakes);
        earthquakeListView.setAdapter(aa);
        refreshEarthquakes();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Get the ID of the selected menu item
        if (id == R.id.menu_refresh) {
            refreshQuakes();
            return true;
        }
        else if (id == R.id.menu_map) {
            Intent i = new Intent(this, earthQuakeMap.class);
            Bundle bundle = new Bundle();
            i.putExtra("latitudes", latitudes);
            i.putExtra("longitudes", longitudes);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshQuakes() {
        refreshEarthquakes();
    }
    private void refreshEarthquakes() {
        // Get the XML
        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Parse the earthquake feed.
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                // Clear the old earthquakes
                earthquakes.clear();
                // Get a list of each earthquake entry.
                NodeList nl = docEle.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0 ; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("title").item(0);
                        Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
                        Element when = (Element)entry.getElementsByTagName("updated").item(0);
                        Element link = (Element)entry.getElementsByTagName("link").item(0);
                        String depth = entry.getElementsByTagName("georss:elev").item(0).getFirstChild().getNodeValue();
                        String details = title.getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.gov";
                        String linkString = hostname + link.getAttribute("href");
                        String point = g.getFirstChild().getNodeValue();
                        String dt = when.getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SS'Z'", Locale.US);
                        Date qdate = new GregorianCalendar(0,0,0).getTime();
                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }
                        String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        l.setLatitude(Double.parseDouble(location[0]));
                        l.setLongitude(Double.parseDouble(location[1]));
                        String magnitudeString = details.split(" ")[1];
                        int end = magnitudeString.length();
                        double magnitude = Double.parseDouble(magnitudeString.substring(0, end));
                        if (details.contains(",")) {
                            details = details.split(",")[1].trim();
                        } else {
                            details = details.split("-")[1].trim();
                        }
                        Quake quake = new Quake(qdate, details, l, magnitude, linkString , depth);
                        addNewQuake(quake);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException", e);
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "Parser Configuration Exception", e);
        } catch (SAXException e) {
            Log.d(TAG, "SAX Exception", e);
        }
        finally {

        }
    }


    private void addNewQuake(Quake _quake) {
        earthquakes.add(_quake); // Add the new quake to our list of earthquakes.
        latitudes.add(_quake.getLocation().getLatitude());
        longitudes.add(_quake.getLocation().getLongitude());
        aa.notifyDataSetChanged(); // Notify the array adapter of a change.
    }
}

