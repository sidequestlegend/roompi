package room.bot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.util.Log;

public class NDS {
	int devcnt = 0;
	DatagramSocket c;
	boolean roomba = false;
	boolean android = false;
	Main main;
	public NDS(Main main){
		this.main = main;
	// Find the server using UDP broadcast
	try {
	  c = new DatagramSocket(65431, InetAddress.getByName("0.0.0.0"));
	  c.setBroadcast(true);

	  getNDS();
	  //Close the port!
	  //
	} catch (IOException ex) {
	  //Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
	}
}
	
	public void getNDS(){

			 byte[] recvBuf = new byte[15000];
			  DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			  try {
				c.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

			  String message = new String(receivePacket.getData()).trim();
			  Log.d("NDS",message);
			  //We have a response
			 //Check if the message is correct
			  if (message.startsWith("roomba-pi")) {
				  if(!roomba){
					  roomba = true;
					  this.main.roombaConnect(receivePacket.getAddress().getHostAddress());
				  }
			  }
			  if (message.startsWith("roomba-cam")) {
				  if(!android){
				  android = true;
				  Main.ipaddrees = receivePacket.getAddress().getHostAddress();
				  }
			  }
			  try {

					if(roomba && android){
						c.close();
					}else{
						Thread.sleep(2000);
						Log.d("NDS","run");
						getNDS();
					}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

