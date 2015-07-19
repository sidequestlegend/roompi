package room.bot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IPCamera{

    private static final String TAG = "MjpegActivity";

    String url;
    String usr;
    String pwd; 
    MjpegInputStream is;
    boolean running = false;
    Handler h;
    Context ctx;
    int id;

    IPCamera(String url, String usr, String pwd, Context ctx){
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;
        this.ctx = ctx;
    }

    public void startStream(Handler h, int id){
    this.h = h;
    this.id = id;
    is = new MjpegInputStream(httpRequest(url,usr,pwd));    
    running = true;
}   

public void getFrame(){     
    while (running){
        Bitmap b;
        try {   
            b = is.readMjpegFrame();
            Message m = h.obtainMessage(id, b);
            m.sendToTarget();
            } catch (IOException e) {
            e.printStackTrace();
        }           
    }   
}

    public InputStream httpRequest(String url, String usr, String pwd){
        HttpResponse res = null;
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
            new UsernamePasswordCredentials(usr, pwd));
        httpclient.setCredentialsProvider(credProvider);
        Log.d(TAG, "1. Sending http request");
        try {
            res = httpclient.execute(new HttpGet(URI.create(url)));
            Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
            if(res.getStatusLine().getStatusCode()==401){
                //You must turn off camera User Access Control before this will work
                return null;
            }
            return res.getEntity().getContent();  
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d(TAG, "Request failed-ClientProtocolException", e);
            //Error connecting to camera
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Request failed-IOException", e);
            //Error connecting to camera
        }

        return null;

    }
}