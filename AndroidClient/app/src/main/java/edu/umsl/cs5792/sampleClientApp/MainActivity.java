package edu.umsl.cs5792.sampleClientApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int OPEN_FILE_STREAM = 2;
    private static final int SO_TIMEOUT = 60 * 1000; // 60 second timeout

    Thread getScoreThread, sendLocationThread;
    EditText etToken;
    TextView tvConnectMsg, tvSendMsg, tvDetailMsg;
    WebView webView;
    Map<String, String> extraHeaders = new HashMap<String, String>();

    Button btnGetScore, btnSendLocation;

    // String SERVER_HOST = "hopefullyhuman.com";
    String SERVER_HOST = "hopefullyhuman.com";
    int SERVER_PORT = 7777;

    private DataOutputStream out;
    Uri uri = null;

    Date connectStartTime, connectEndTime;
    private FusedLocationProviderClient fusedLocationClient;
    private Activity mainActivity;

    @Override
    // TODO change this to location callback
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        // register callback for file selection
        if (req == OPEN_FILE_STREAM && res == Activity.RESULT_OK) {
            // The result data contains the document URI
            if (data != null) {
                uri = data.getData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        // get our view handles
        tvConnectMsg = findViewById(R.id.tvConnectMsg);
        tvSendMsg = findViewById(R.id.tvSendMsg);
        tvDetailMsg = findViewById(R.id.tvDetailMsg);

        etToken = findViewById(R.id.etToken);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        // webView.setPadding(10, 0, 10, 0);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        // webView.setScrollContainer(false);

        String theToken = getSubjectToken();

        if(theToken != "") {
            extraHeaders.put("SUBJECTTOKEN", theToken);
        }

        webView.loadUrl("http://www.hopefullyhuman.com/data", extraHeaders);
        // webView.loadUrl("http://www.hopefullyhuman.com/");

        btnGetScore = findViewById(R.id.btnGetScore);
        btnSendLocation = findViewById(R.id.btnSendLocation);

        // "get score" button handler - validate host/IP, start connection thread
        btnGetScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getScoreThread = new Thread(new GetScoreThread());
                getScoreThread.start();
            }
        });

        // "Send Location" button handler
        // verify connection and file uri and start send thread
        btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("Trying");
                    if (ActivityCompat.checkSelfPermission(
                            mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mainActivity, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION}, 9999);
                    } else {
                        Log.d(TAG, "getLocation: permissions granted");
                    }

                    fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                System.out.println(location);

                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    sendLocationThread = new Thread(new refreshBrowser(location));
                                    sendLocationThread.start();
                                }
                            }
                        });
                } catch (SecurityException e) {
                    // TODO - something meaningful here?
                    System.out.println("Security Exception");

                    Log.e(TAG, e.toString());
                }
            }
        });

    }

    private String getSubjectToken() {
        Editable etSubjectToken = etToken.getText();
        String subjectToken = "";

        if(etSubjectToken != null) {
            String subjectTokenString = etSubjectToken.toString();

            if(subjectTokenString != null && !subjectTokenString.trim().isEmpty()) {
               subjectToken = subjectTokenString.trim();
            }
        }

        return subjectToken;
    }

    // send a JSON object to the Score Server and return the result
    // TODO multiple sequential requests seem to garble data, not sure if its netcat or this code
    private JSONObject sendData(JSONObject requestJSON) {
        Socket sock = new Socket();
        JSONObject responseJSON = new JSONObject();
        String lastError;

        try {
            setStatusMessage(tvConnectMsg, "Connecting...");
            sock.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), SO_TIMEOUT);

            // set 10 second read/write timeout
            sock.setSoTimeout(SO_TIMEOUT);

            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            out.writeBytes(requestJSON.toString());
            out.writeByte(4);

            out.flush();

            InputStream in = sock.getInputStream();

            byte[] buf = new byte[1024];
            int bytesRead;
            String rawResponse = "";
            while((bytesRead = in.read(buf)) != -1) {
                rawResponse += new String(buf, 0, bytesRead);
            };

            System.out.print(rawResponse);
            System.out.flush();

            responseJSON = new JSONObject(rawResponse);

        }
        catch (IOException e) {
            Log.e(TAG, e.toString());

            if(e instanceof SocketTimeoutException) {
                setStatusMessage(tvConnectMsg, "Connection timed out");
            }
            else {
                setStatusMessage(tvConnectMsg, "Connection failed");
            }
        }
        catch (JSONException e) {
            Log.e(TAG, e.toString());

            setStatusMessage(tvConnectMsg, "Error parsing response");
        }

        try {
            sock.close();
        }
        catch(Exception e) {
        }

        return responseJSON;
    }

    // update the given text field with the given message so we 
    // don't have to scatter runOnUiThread calls everywhere
    private void setStatusMessage(TextView tv, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(msg);
            }
        });
    }

    // create socket connection to given host and port
    class GetScoreThread implements Runnable {
        public void run() {
            try {
                JSONObject requestJSON = new JSONObject();

                // just hardcode it for now
                requestJSON.put("actType", "REQ");
                String theToken = getSubjectToken();

                if(theToken != "") {
                    requestJSON.put("subjectToken", theToken);

                    JSONObject responseJSON = sendData(requestJSON);

                    double score = responseJSON.getDouble("score");

                    setStatusMessage(tvConnectMsg, String.valueOf(score));
                }
            }
            catch (JSONException e) {
                Log.e(TAG, e.toString());

                setStatusMessage(tvConnectMsg, "Error parsing response");
            }
        }
    }
  
    // write location data to server
    class refreshBrowser implements Runnable {
        Location location;

        public refreshBrowser(Location l) {
            location = l;
        }

        public void run() {
            try {
                JSONObject requestJSON = new JSONObject();

                String theToken = getSubjectToken();

                if(theToken != "") {
                    // just hardcode it for now
                    requestJSON.put("actType", "REQ");
                    requestJSON.put("subjectToken", theToken);

                    JSONObject responseJSON = sendData(requestJSON);

                    double score = responseJSON.getDouble("score");
                    setStatusMessage(tvConnectMsg, String.valueOf(score));
                }

                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        String theToken = getSubjectToken();
                        if(theToken != "") {
                            extraHeaders.put("SUBJECTTOKEN", theToken);
                        }

                        webView.loadUrl("http://www.hopefullyhuman.com/data", extraHeaders);
                    }
                });
            }
            catch (JSONException e) {
                Log.e(TAG, e.toString());

                setStatusMessage(tvConnectMsg, "Error parsing response");
            }

        }
    }

    // close input file and socket and update UI
    class DisconnectThread implements Runnable {
        public void run() {
            try {
                if(out != null) out.close();
            }
            catch (IOException e) {
                Log.i(TAG, "Error closing output stream" + e.toString());
            }
            connectEndTime = new Date();

            double connectTime = (connectEndTime.getTime() - connectStartTime.getTime()) / 1000.;

            setStatusMessage(tvConnectMsg, "Disconnected");
        }
    }
}
