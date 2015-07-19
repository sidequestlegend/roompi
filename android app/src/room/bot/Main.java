package room.bot;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import room.bot.WSClient;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;


import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_video.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class Main extends Activity{

    private RelativeLayout roombaView;
    private static WSClient roombaclient;
    static ImageView opponentView;
    RelativeLayout root;
	int maxvel = 500;
	int maxrad = 1950;
	static Bitmap oldImage;
    
    final static Handler h = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            	Bitmap bmp = (Bitmap) msg.obj;
            	/*if(oldImage != null){
	            	try
	            	{
	            	   int MAX_CORNERS = 500;
	            	   IplImage imgA = IplImage.create(640, 480, IPL_DEPTH_32F, 4);
	            	   IplImage imgB = IplImage.create(640, 480, IPL_DEPTH_32F, 4);
	
	            	   bmp.copyPixelsToBuffer(imgB.getByteBuffer());
	            	   oldImage.copyPixelsToBuffer(imgA.getByteBuffer());
	            	   
	            	   CvSize img_sz = cvGetSize(imgA);
	                   int win_size = 15;
	
	                   // Get the features for tracking
	                   IplImage eig_image = cvCreateImage(img_sz, IPL_DEPTH_32F, 1);
	                   IplImage tmp_image = cvCreateImage(img_sz, IPL_DEPTH_32F, 1);
	
	                   IntPointer corner_count = new IntPointer(1).put(MAX_CORNERS);
	                   CvPoint2D32f cornersA = new CvPoint2D32f(MAX_CORNERS);
	
	                   CvArr mask = null;
	                   cvGoodFeaturesToTrack(imgA, eig_image, tmp_image, cornersA,
	                           corner_count, 0.05, 5.0, mask, 3, 0, 0.04);
	
	                   cvFindCornerSubPix(imgA, cornersA, corner_count.get(),
	                           cvSize(win_size, win_size), cvSize(-1, -1),
	                           cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.03));
	
	                   // Call Lucas Kanade algorithm
	                   BytePointer features_found = new BytePointer(MAX_CORNERS);
	                   FloatPointer feature_errors = new FloatPointer(MAX_CORNERS);
	
	                   CvSize pyr_sz = cvSize(imgA.width() + 8, imgB.height() / 3);
	
	                   IplImage pyrA = cvCreateImage(pyr_sz, IPL_DEPTH_32F, 1);
	                   IplImage pyrB = cvCreateImage(pyr_sz, IPL_DEPTH_32F, 1);
	
	                   CvPoint2D32f cornersB = new CvPoint2D32f(MAX_CORNERS);
	                   cvCalcOpticalFlowPyrLK(imgA, imgB, pyrA, pyrB, cornersA, cornersB,
	                           corner_count.get(), cvSize(win_size, win_size), 5,
	                           features_found, feature_errors,
	                           cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3), 0);
	
	                   // Make an image of the results
	                   for (int i = 0; i < corner_count.get(); i++) {
	                       if (features_found.get(i) == 0 || feature_errors.get(i) > 550) {
	                           System.out.println("Error is " + feature_errors.get(i) + "/n");
	                           continue;
	                       }
	                       System.out.println("Got it/n");
	                       cornersA.position(i);
	                       cornersB.position(i);
	                       CvPoint p0 = cvPoint(Math.round(cornersA.x()),
	                               Math.round(cornersA.y()));
	                       CvPoint p1 = cvPoint(Math.round(cornersB.x()),
	                               Math.round(cornersB.y()));
	                       cvLine(imgB, p0, p1, CV_RGB(255, 0, 0), 
	                               2, 8, 0);
	                       Bitmap bitmapOut = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);  
	                       bitmapOut.copyPixelsFromBuffer(imgB.getByteBuffer());  
	                       opponentView.setImageBitmap(bitmapOut);
	                   }
	            	   
	
	            	}catch (Exception e) 
	            	{
	            	   //this is the line of code that sends a real error message to the log.
	            	   Log.e("ERROR", "ERROR in Code: " + e.toString());
	            				
	            	   e.printStackTrace();
	            	}	
            	}else{*/
            		opponentView.setImageBitmap(bmp);
            	//}
            	oldImage = bmp;
            return false;
        }
    });
	int outvel = 0;
	Drawable musicofficon; 
	Drawable musicicon;
	int outrad = 0;
	boolean isPlaying = false;
	boolean isDriving = false;
    Main savemain;
    static String ipaddrees = "";
    double statusBarHeight = 0;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		roombaConnect("192.168.10.1");
	
		
		statusBarHeight = 0;//Math.ceil(25 * getResources().getDisplayMetrics().density);
		
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        savemain = this;
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
	    display.getSize(size);
	    size.x = size.x/2;
//	    size.y = size.y/2;
        opponentView = new ImageView(this);
        opponentView.setScaleType(ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams ownparams = new RelativeLayout.LayoutParams((int)(size.x*2), (int)(size.y));
        ownparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        opponentView.setLayoutParams(ownparams);
        
        Thread ndsthread = new Thread(new Runnable(){@Override public void run() { new NDS(Main.this);}});
        ndsthread.start();
        
        super.onCreate(savedInstanceState);
        // Create the layout for the dialog
        root = new RelativeLayout(this);
        root.setMinimumHeight(size.x);
        root.setMinimumWidth(size.y);
        setContentView(root);
         
        //videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
        
        roombaView = new RelativeLayout(this);
        //ownView = new OwnSurfaceView(this, null);
       
        RelativeLayout.LayoutParams viewparams = new RelativeLayout.LayoutParams((int)(size.x*0.57), (int)(size.x*0.57));
        viewparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        roombaView.setLayoutParams(viewparams);
        //roombaView.setBackgroundColor(Color.argb(50,255,255,255));
        roombaView.setTranslationX((int)(size.x+(size.x*0.35)));
        roombaView.setTranslationY((int)((size.x*0.35)*-1));
        
        Drawable movebakicon= getResources().getDrawable(getResources().getIdentifier("movebak", "drawable", this.getPackageName()));
        Bitmap movebakmap = ((BitmapDrawable) movebakicon).getBitmap();
     // Scale it to 50 x 50
        movebakicon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(movebakmap, (int)(size.x*0.57), (int)(size.x*0.57), true));
        
        roombaView.setBackground(movebakicon);
        
        roombaMusic(size);
        
        Drawable moveicon= getResources().getDrawable(getResources().getIdentifier("movebut", "drawable", this.getPackageName()));
        Bitmap movemap = ((BitmapDrawable) moveicon).getBitmap();
     // Scale it to 50 x 50
        moveicon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(movemap, (int)(size.x*0.20), (int)(size.x*0.20), true));
        
        
        
        
        
        
        
        final Button move = new Button(this);
        move.setId(5001);
        move.setSoundEffectsEnabled(false);
        move.setCompoundDrawablesWithIntrinsicBounds( null, moveicon, null, null );
        move.setClickable(false);
        move.setBackgroundColor(Color.argb(0, 0,0,0));
        move.setWidth( (int) (size.x*0.20));
        move.setHeight( (int) (size.x*0.20));
        move.setTranslationX((int)(((size.x*0.57)/2)-(int)(size.x*0.10)));
        move.setTranslationY((int)(((size.x*0.57)/2)-(int)(size.x*0.10)));
   
        roombaView.addView(move);
        roombaView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

            	int halfpoint = (int)((size.x*0.57)/2)-(int)(size.x*0.10);
            	if(event.getY() > (0+(int)(size.x*0.10)) && event.getY() < ((int)(size.x*0.57)-(int)(size.x*0.10))){
                    move.setTranslationY((int)(event.getY()-(int)(size.x*0.10)));
                    if(event.getY() < (int)((size.x*0.57)/2)){
                    	outvel = (int)((((int)((size.x*0.57)/2)-event.getY())/halfpoint)*maxvel);
                    }else{
                    	outvel = -1*(int)(((event.getY()-(int)((size.x*0.57)/2))/halfpoint)*maxvel);
                    }
            	}
                if(event.getX() > (0+(int)(size.x*0.10)) && event.getX() < ((int)(size.x*0.57)-(int)(size.x*0.10))){
                    move.setTranslationX((int)(event.getX()-(int)(size.x*0.10)));
                    if(event.getX() < (int)((size.x*0.57)/2)){
                    	outrad = (maxrad-(int)((((int)((size.x*0.57)/2)-event.getX())/halfpoint)*maxrad));
                    }else{
                    	outrad = -1*(maxrad-(int)(((event.getX()-(int)((size.x*0.57)/2))/halfpoint)*maxrad));
                    }
                    /*
                    if(event.getX() < (int)((size.x*0.57)/2))
                    	outvel = (int)((((int)((size.x*0.57)/2)-event.getX())/halfpoint)*maxvel);
                    }else{
                    	outvel = (int)(((event.getX()-(int)((size.x*0.57)/2))/halfpoint)*maxvel);
                    }
                    */
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                    isDriving = true;
                   // roombaclient.send(("roomba-rol").getBytes());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                	isDriving = false;
                	roombaclient.send(("roomba-sto").getBytes());
                    move.setTranslationX((int)(((size.x*0.57)/2)-(int)(size.x*0.10)));
                    move.setTranslationY((int)(((size.x*0.57)/2)-(int)(size.x*0.10)));
                	//roombaclient.send(("roomba-sto").getBytes());
                }
                return true;
            }
        });
        
        
        final Handler driver = new Handler();
        Runnable drive = new Runnable(){

			@Override
			public void run() {
				if(isDriving && roombaclient != null){
					roombaclient.send(("roomba-dri,"+outvel+","+outrad).getBytes());
				}
				driver.postDelayed(this, 250);
				
			}
        	
        };
        driver.post(drive);
        
        RelativeLayout sidebar = new RelativeLayout(this);
        
        RelativeLayout.LayoutParams sideparams = new RelativeLayout.LayoutParams((int)(size.x*0.6), (int) ((size.y)));
        sideparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        
        sidebar.setLayoutParams(sideparams);
        sidebar.setGravity(Gravity.CENTER_VERTICAL);
        sidebar.setVisibility(View.GONE);
        sidebar.setClickable(true);
        sidebar.setBackgroundColor(Color.argb(208,22, 32, 31));
        
       //ownView = new OwnSurfaceView(this, null);
        RelativeLayout.LayoutParams oownparams = new RelativeLayout.LayoutParams((int)(1), (int)(1));
        oownparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        //ownView.setLayoutParams(oownparams);
        //root.addView(ownView);
	    root.addView(opponentView);
        root.addView(roombaView);
        addButton("clean","roomba-cln", size, (int)(size.x*0.80),(int) (size.y-((size.x*0.20)+statusBarHeight)));
        addButton("spot","roomba-spt", size, (int)(size.x),(int) (size.y-((size.x*0.20)+statusBarHeight)));
        addButton("max","roomba-max", size, (int)(size.x*1.20),(int) (size.y-((size.x*0.20)+statusBarHeight)));
        addButton("full","roomba-ful", size, (int)(size.x*1.40),(int) (size.y-((size.x*0.20)+statusBarHeight)));
        addButton("safe","roomba-saf", size, (int)(size.x*1.60),(int) (size.y-((size.x*0.20)+statusBarHeight)));
        addButton("dock","roomba-dok", size, (int)(size.x*1.80),(int) (size.y-((size.x*0.20)+statusBarHeight)));
        
        
        
    }
    
    
    
    
    @Override
    protected void onResume() {
        super.onResume();
        //ownView.reuseCamera();
    }

    @Override
    protected void onPause() {
    	//ownView.closeCamera();
        super.onPause();
    }

    @Override
    protected void onStop() {
        //QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
        super.onStop();
    }

	
	 protected String wifiIpAddress(Context context) {
	        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
	        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

	        // Convert little-endian to big-endianif needed
	        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	            ipAddress = Integer.reverseBytes(ipAddress);
	        }

	        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

	        String ipAddressString;
	        try {
	            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	        } catch (UnknownHostException ex) {
	            Log.e("WIFIIP", "Unable to get host address.");
	            ipAddressString = null;
	        }

	        return ipAddressString;
	    }
	    
	
	public Button addButton(String icon,final String cmd, Point size, int x,int y){
		Drawable cleanicon=getResources().getDrawable(getResources().getIdentifier(icon, "drawable", this.getPackageName()));
        Bitmap cleanmap = ((BitmapDrawable) cleanicon).getBitmap();
     // Scale it to 50 x 50
        cleanicon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(cleanmap, (int)(size.x*0.19), (int)(size.x*0.19), true));
        Button clean = new Button(this);
        clean.setId(5001);
        clean.setSoundEffectsEnabled(false);
        clean.setCompoundDrawablesWithIntrinsicBounds( null, cleanicon, null, null );
        
        clean.setBackgroundColor(Color.argb(0, 0,0,0));
        clean.setWidth( (int) (size.x*0.20));
        clean.setHeight( (int) (size.x*0.20));
        clean.setTranslationX(x);
        clean.setTranslationY(y);
        root.addView(clean);
        clean.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                    roombaclient.send((cmd).getBytes());
                } 
                return true;
            }
        });
        return clean;
		
	}

	
    public void roombaConnect(final String ip){
	try {
		roombaclient = new WSClient(new URI("ws://"+ip+":65432"), new Draft_10()){
		    @Override
		    public void onMessage(String message) {
		    	
            	//makeToast(message);
		    }

		    @Override
		    public void onOpen(ServerHandshake handshakedata) {

		    	IPCamera cam1 = new IPCamera("http://"+ip+":8080/?action=stream", "admin", "admin", Main.this); 
    			
    			
    			startTask(cam1, 1, false, h);
		    	Log.d("NDS","new connection opened");
		    }

		    @Override
		    public void onClose(int code, String reason, boolean remote) {
		    }
		    

		    @Override
		    public void onError(Exception ex) {
		    	 
		    	Log.d("NDS","an error occured:"+ ex);
		    }
			
		};

		roombaclient.connect();
	} catch (URISyntaxException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    }
    /*
    public void SendMicAudio(final String ip)
    {
        Thread thrd = new Thread(new Runnable() {
                        @Override
            public void run() 
            {
                AudioRecord audio_recorder = new AudioRecord(
						MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        		AudioFormat.ENCODING_DEFAULT,
                                AudioFormat.ENCODING_PCM_16BIT) * 2);
                audio_recorder.startRecording();
                int bytes_read = 0;
                int bytes_count = 0;
                byte[] buf = new byte[BUF_SIZE];
                try
                {
                    InetAddress addr = InetAddress.getByName(ip);
                    DatagramSocket sock = new DatagramSocket();

                    while(true)
                    {
                        bytes_read = audio_recorder.read(buf, 0, BUF_SIZE);
                        DatagramPacket pack = new DatagramPacket(buf, bytes_read,
                                addr, AUDIO_PORT);
                        sock.send(pack);
                        bytes_count += bytes_read;
                        Thread.sleep(SAMPLE_INTERVAL, 0);
                    }
                }
                catch (InterruptedException ie)
                {
                    Log.e("NDSAUDIO", "InterruptedException");
                }
//                catch (FileNotFoundException fnfe)
//                {
//                    Log.e("NDSAUDIO", "FileNotFoundException");
//                }
                catch (SocketException se)
                {
                    Log.e("NDSAUDIO", "SocketException");
                }
                catch (UnknownHostException uhe)
                {
                    Log.e("NDSAUDIO", "UnknownHostException");
                }
                catch (IOException ie)
                {
                    Log.e("NDSAUDIO", "IOException");
                }
            } // end run
        });
        thrd.start();
    }
    

    public void RecvAudio()
    {
        Thread thrd = new Thread(new Runnable() {
                        @Override
            public void run() 
            {
                Log.e("NDSAUDIO", "start recv thread, thread id: "
                    + Thread.currentThread().getId());
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 
                        SAMPLE_RATE, AudioFormat.ENCODING_DEFAULT, 
                        AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, 
                        AudioTrack.MODE_STREAM);
                track.play();
                try
                {
                    DatagramSocket sock = new DatagramSocket(ALT_AUDIO_PORT);
                    byte[] buf = new byte[BUF_SIZE];

                    while(true)
                    {
                        DatagramPacket pack = new DatagramPacket(buf, BUF_SIZE);
                        sock.receive(pack);
                        Log.d("NDSAUDIO", "recv pack: " + pack.getLength());
                        track.write(pack.getData(), 0, pack.getLength());
                    }
                }
                catch (SocketException se)
                {
                    Log.e("NDSAUDIO", "SocketException: " + se.toString());
                }
                catch (IOException ie)
                {
                    Log.e("NDSAUDIO", "IOException" + ie.toString());
                }
            } // end run
        });
        thrd.start();
    }
    */
    private DownloadImageTask startTask(IPCamera cam, int id, boolean useParallelExecution, Handler h) {
        DownloadImageTask task = new DownloadImageTask(cam, id);        
        if (useParallelExecution) {            
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);            
        } else {           
            task.execute(h);
        }        
        return task;        
    }

    private class DownloadImageTask extends AsyncTask<Handler, Void, Void> {        
        IPCamera cam;
        int id;
        DownloadImageTask(IPCamera cam, int id){
            this.cam = cam;
            this.id = id;
        }



        protected Void doInBackground(Handler... h) {    
            cam.startStream(h[0], id);    
            cam.getFrame();         
            return null;        
        }
    }
    
    
    private void roombaMusic(Point size){

        // see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
        
        musicofficon= getResources().getDrawable(getResources().getIdentifier("musicoffnote", "drawable", this.getPackageName()));
        Bitmap musicoffmap = ((BitmapDrawable) musicofficon).getBitmap();
     // Scale it to 50 x 50
        musicofficon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(musicoffmap, (int)(size.x*0.20), (int)(size.x*0.20), true));
        
        
        
        musicicon= getResources().getDrawable(getResources().getIdentifier("musicnote", "drawable", this.getPackageName()));
        Bitmap musicmap = ((BitmapDrawable) musicicon).getBitmap();
     // Scale it to 50 x 50
        musicicon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(musicmap, (int)(size.x*0.20), (int)(size.x*0.20), true));
        
        
        final Button music = new Button(this);
        music.setId(5001);
        music.setSoundEffectsEnabled(false);
        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
        music.setClickable(false);
        music.setBackgroundColor(Color.argb(0, 0,0,0));
        music.setWidth( (int) (size.x*0.20));
        music.setHeight( (int) (size.x*0.20));
        music.setTranslationX((int)(size.x-(int)(size.x*0.20)));
        root.addView(music);
        music.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                	final Handler handler = new Handler();
                	if(isPlaying){
                    	roombaclient.send(("roomba-off").getBytes());
                    	handler.postDelayed(new Runnable(){
                        	@Override
                        	      public void run(){

                    	roombaclient.send(("roomba-saf").getBytes());
                        	}
                        	
                    	},500);
                    	isPlaying=false;
                    	music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
                    	
                	}else{
                        isPlaying = true;

                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicofficon, null, null );
                	roombaclient.send(("roomba-sng,76,12,76,12,20,12,76,12,20,12,72,12,76,12,20,12,79,12,20,36,67,12").getBytes());
                	
                	handler.postDelayed(new Runnable(){
                	@Override
                	      public void run(){
                		if(isPlaying){
                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
                			roombaclient.send(("roomba-sng,72,12,20,24,67,12,20,24,64,12,20,24,69,12,20,12,71,12,20,12,70,12,69,12,20,12,67,16,76,16,79,16,81,12,20,12,77,12,79,12,20,12,76,12,20,12,72,12,74,12,71,12").getBytes());
                			handler.postDelayed(new Runnable(){
                            	@Override
                            	      public void run(){
                            		if(isPlaying){
                                    music.setCompoundDrawablesWithIntrinsicBounds( null, musicofficon, null, null );
                            			roombaclient.send(("roomba-sng,72,12,20,24,67,12,20,24,64,12,20,24,69,12,20,12,71,12,20,12,70,12,69,12,20,12,67,16,76,16,79,16,81,12,20,12,77,12,79,12,20,12,76,12,20,12,72,12,74,12,71,12").getBytes());
                            			handler.postDelayed(new Runnable(){

										@Override
										public void run() {
											if(isPlaying){
					                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
											roombaclient.send(("roomba-sng,48,12,20,12,79,12,78,12,77,12,75,12,60,12,76,12,53,12,68,12,69,12,72,12,60,12,69,12,72,12,74,12,48,12,20,12,79,12,78,12,77,12,75,12,55,12,76,12,20,12,84,12,20,12,84,12,84,12").getBytes());
			                			
											handler.postDelayed(new Runnable(){
		
												@Override
												public void run() {
													if(isPlaying){
							                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicofficon, null, null );
													// TODO Auto-generated method stub
													roombaclient.send(("roomba-sng,55,12,20,12,48,12,20,12,79,12,78,12,77,12,75,12,60,12,76,12,53,12,68,12,69,12,72,12,60,12,69,12,72,12,74,12,48,12,20,12,75,24,20,12,74,24,20,12,72,24,20,12,55,12,55,12,20,12,48,12").getBytes());
													handler.postDelayed(new Runnable(){
		
														@Override
														public void run() {
															if(isPlaying){
									                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
															roombaclient.send(("roomba-sng,48,12,20,12,79,12,78,12,77,12,75,12,60,12,76,12,53,12,68,12,69,12,72,12,60,12,69,12,72,12,74,12,48,12,20,12,79,12,78,12,77,12,75,12,55,12,76,12,20,12,84,12,20,12,84,12,84,12").getBytes());
							                			
															handler.postDelayed(new Runnable(){
		
																@Override
																public void run() {
																	if(isPlaying){
											                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicofficon, null, null );
																	// TODO Auto-generated method stub
																	roombaclient.send(("roomba-sng,55,12,20,12,48,12,20,12,79,12,78,12,77,12,75,12,60,12,76,12,53,12,68,12,69,12,72,12,60,12,69,12,72,12,74,12,48,12,20,12,75,24,20,12,74,24,20,12,72,24,20,12,55,12,55,12,20,12,48,12").getBytes());
																	handler.postDelayed(new Runnable(){
						
																		@Override
																		public void run() {
																			if(isPlaying){
													                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
												                			
												                			roombaclient.send(("roomba-sng,72,12,20,24,67,12,20,24,64,12,20,24,69,12,20,12,71,12,20,12,70,12,69,12,20,12,67,16,76,16,79,16,81,12,20,12,77,12,79,12,20,12,76,12,20,12,72,12,74,12,71,12").getBytes());
												                			handler.postDelayed(new Runnable(){
												                            	@Override
												                            	      public void run(){
																					if(isPlaying){
												                                    music.setCompoundDrawablesWithIntrinsicBounds( null, musicofficon, null, null );
												                            			
												                            			roombaclient.send(("roomba-sng,72,12,20,24,67,12,20,24,64,12,20,24,69,12,20,12,71,12,20,12,70,12,69,12,20,12,67,16,76,16,79,16,81,12,20,12,77,12,79,12,20,12,76,12,20,12,72,12,74,12,71,12").getBytes());
												                            			handler.postDelayed(new Runnable(){

																						@Override
																						public void run() {
																							if(isPlaying){
																	                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
																						// TODO Auto-generated method stub
																						roombaclient.send(("roomba-sng,72,12,72,12,20,12,72,12,20,12,72,12,74,12,20,12,76,12,72,12,20,12,69,12,67,12,20,12,43,12,20,12,72,12,72,12,20,12,72,12,20,12,72,12,74,12,76,12,55,12,20,24,48,12,20,24,43,12,20,12,72,12,72,12,20,12,72,12,20,12,72,12,74,12,20,12,76,12,72,12,20,12,69,12,67,12,20,12,43,12,20,12,76,12,76,12,20,12,76,12,20,12,72,12,76,12,20,12,79,12,20,36,67,12").getBytes());
																						handler.postDelayed(new Runnable(){
									
																							@Override
																							public void run() {
																								if(isPlaying){
																		                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicofficon, null, null );
																								// TODO Auto-generated method stub
																								roombaclient.send(("roomba-sng,76,12,72,12,20,12,67,12,55,12,20,12,68,12,20,12,69,12,77,12,53,12,77,12,69,12,60,12,53,12,20,12,71,16,81,16,81,16,81,16,79,16,77,16,76,12,72,12,55,12,69,12,67,12,60,12,55,12,20,12,76,12,72,12,20,12,67,12,55,12,20,12,68,12,20,12,69,12,77,12,53,12,77,12,69,12,60,12,53,12,20,12,71,12,77,12,20,12,77,12,77,16,76,16,74,16,72,12,64,12,55,12,64,12,60,12").getBytes());
																								handler.postDelayed(new Runnable(){
									
																									@Override
																									public void run() {
																										if(isPlaying){
																				                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
																										// TODO Auto-generated method stub
																										roombaclient.send(("roomba-sng,72,12,20,24,67,12,20,24,64,24,69,16,71,16,69,16,68,24,70,24,68,24,67,12,65,12,67,48").getBytes());
																										handler.postDelayed(new Runnable(){
																											
																											@Override
																											public void run() {
																												if(isPlaying){
																						                        music.setCompoundDrawablesWithIntrinsicBounds( null, musicicon, null, null );
																												// TODO Auto-generated method stub
																						                        roombaclient.send(("roomba-sng,45,10,57,10,42,10,54,10,43,10,55,10,20,64,20,64,45,10,57,10,42,10,54,10,43,10,55,10,20,64,20,64,38,10,50,10,35,10,47,10,36,10,48,10,20,64,20,64,38,10,50,10,35,10,47,10,36,10,48,10,20,64,20,64,48,5,47,5,46,5,45,10,20,10,48,10,20,10,47,10,20,10,53,10,20,10,52,10,20,10,46,10,20,10,45,10,20,10,51,5,50,5,49,5,55,5,54,5,53,10,48,10,44,10,43,10,42,10,41").getBytes());
																											}
																											}
																			                				
																			                				
																			                			}, 6050);
																									}
																									}
																	                				
																	                				
																	                			}, 6050);
																							}
																							}
			
															                				
															                			}, 6050);
																						}
																					}
					
													                			}, 6050);
												                            	}
																			}
												                				
												                			}, 6050);
																		}
																		}
		
										                			}, 6050);
																}
																}
								                				
								                				
								                			}, 6050);
														}
														}
						                				
						                			}, 6050);
												}
												}
				                				
				                				
				                			}, 6050);
										}
										}
		                			}, 6050);
                            	}
		                       }
                         	}, 6050);
                	}
                	   	}
                	}, 3000);
                	}
                	//alert.show();
                }
                return true;
            }
        });
        
        
        
    }
}
