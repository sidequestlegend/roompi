package harris.shane.roompi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class IPCamera{

    private static final String TAG = "MjpegActivity";

    Uri url;
    MjpegInputStream is;
    boolean running = false;
    Handler h;
    Context ctx;
    int id;

    IPCamera(Uri url, Context ctx){
        this.url = url;
        this.ctx = ctx;
    }

    public void startStream(Handler h, int id){
    this.h = h;
    this.id = id;
        try {
            is = new MjpegInputStream(httpRequest(url));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public InputStream httpRequest(Uri url) throws FileNotFoundException {

        return ctx.getContentResolver().openInputStream(url);

    }
}