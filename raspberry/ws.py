from SimpleWebSocketServer import WebSocket, SimpleWebSocketServer
from threading import Thread
from threading import Timer
from pyRoomba import RoombaAPI
import subprocess
import time
import os

x = RoombaAPI("/dev/ttyUSB0",115200)
def roombaReady():
	print x.sensors
	#property(x.__sensors)
	print x.sensors.charge
	print x.sensors.capacity
	x.safe()
	#x.off()
	

try:
	x.connect()
	x.start()
	x.control()
	time.sleep(2)
	x.send_to_roomba([128,131])       	
	x.send_to_roomba([128,131])       	
	x.send_to_roomba([140,0,11])       	
	x.send_to_roomba([76,12,76,12,20,12,76,12,20,12,72,12,76,12,20,12,79,12,20,36,67,12])
	x.send_to_roomba([141,0])
	t = Timer(5,roombaReady)
	t.start()
except:
	x.close()

	
#def stopRoomba():
#	x.stop()

#stopTimer = Timer(1000,stopRoomba)

class SimpleEcho(WebSocket):

	def handleMessage(self):
		global x
		if self.data is None:
			self.data = ''
# roomba sensors in json format
		if str(self.data)[:10] == "roomba-clf":
			self.sendMessage('rclif,{"left":"'+str(x.sensors.cliff.left)+'","frontleft":"'+str(x.sensors.cliff.front_left)+'","frontright":"'+str(x.sensors.cliff.front_right)+'","right":"'+str(x.sensors.cliff.right)+'"}')
		if str(self.data)[:10] == "roomba-bat":
			self.sendMessage('rbatt,{"millivolts":"'+str(x.sensors.charge)+'","charginfstate":"'+str(x.sensors.charging_state)+'","charge":"'+str(x.sensors.charge)+'","capacity":"'+str(x.sensors.capacity)+'"}')
#roomba song
		if str(self.data)[:10] == "roomba-s11":
#			self.sendMessage('rdist,{"distance":"'+str(x.sensors.distance)+'"}')
		        x.send_to_roomba([164,49,50,51,52])       	

		if str(self.data)[:10] == "roomba-sng":
#			self.sendMessage('rdist,{"distance":"'+str(x.sensors.distance)+'"}')
			data = str(self.data).split(",")
			bytes =[]
			count=1
			while(count < len(data)):
				bytes.append(int(data[count]))
				count = count + 1
		        x.send_to_roomba([128,131])       	
		        x.send_to_roomba([140,0,(len(data)-1)/2])       	
      			x.send_to_roomba(bytes)
		        x.send_to_roomba([141,0])



#roomba direct drive
		if str(self.data)[:10] == "roomba-dri":
			self.sendMessage('rbdri,')
			getvals = str(self.data).split(",")
			#stopTimer.stop()
			x.drive(int(float(getvals[1])),int(float(getvals[2])))
			#stopTimer.start()
#roomba driving controls
		if str(self.data)[:10] == "roomba-fwd":
			x.forward()
			self.sendMessage('rbfwd,')
		if str(self.data)[:10] == "roomba-bak":
			x.backward()
			self.sendMessage('rbbak,')
		if str(self.data)[:10] == "roomba-lft":
			x.left()
			self.sendMessage('rblft,')
		if str(self.data)[:10] == "roomba-rgt":
			x.right()
			self.sendMessage('rbrgt,')
		if str(self.data)[:10] == "roomba-rol":
			x.spin_left()
			self.sendMessage('rbrol,')
		if str(self.data)[:10] == "roomba-ror":
			x.spin_right()
			self.sendMessage('rbror,')
		if str(self.data)[:10] == "roomba-sto":
			x.stop()
			self.sendMessage('rbsto,')
		if str(self.data)[:10] == "roomba-spe":
			x.__set_speed(int(float(str(self.data)[10:3])))
			self.sendMessage('rbspe,')

#roomab modes
		if str(self.data)[:10] == "roomba-dok":
			x.fdock()
			self.sendMessage('rbdok,')
		if str(self.data)[:10] == "roomba-wak":
			x.start()
			x.control()
			self.sendMessage('rbwak,')
		if str(self.data)[:10] == "roomba-off":
			x.off()
			self.sendMessage('rbsto,')
		if str(self.data)[:10] == "roomba-saf":
			x.safe()
			self.sendMessage('rbsaf,')
		if str(self.data)[:10] == "roomba-ful":
			x.full()
			self.sendMessage('rbful,')
		if str(self.data)[:10] == "roomba-cln":
			x.clean()
			self.sendMessage('rbcln,')
		if str(self.data)[:10] == "roomba-max":
			x.max()
			self.sendMessage('rbmax,')
		if str(self.data)[:10] == "roomba-spt":
			x.spot()
			self.sendMessage('rbspt,')
# Robot arm motion
#		if str(self.data)[:10] == "arm-baseac":
#			MoveArm([0,1,0])
#			self.sendMessage('arbsa,')
#		if str(self.data)[:10] == "arm-basecl":
#			MoveArm([0,2,0])
#			self.sendMessage('arbsc,')
#		if str(self.data)[:10] == "arm-sholup":
#			MoveArm([64,2,0])
#			self.sendMessage('arshu,')
#		if str(self.data)[:10] == "arm-sholdn":
#			MoveArm([128,0,0])
#			self.sendMessage('arelu,')
#		if str(self.data)[:10] == "arm-elbwup":
#			MoveArm([16,0,0])
#			self.sendMessage('areld,')
#		if str(self.data)[:10] == "arm-elbwdn":
#			MoveArm([32,0,0])
#			self.sendMessage('arshd,')
#		if str(self.data)[:10] == "arm-wrstup":
#			MoveArm([4,0,0])
#			self.sendMessage('arswru,')
#		if str(self.data)[:10] == "arm-wrstdn":
#			MoveArm([8,0,0])
#			self.sendMessage('arwrd,')
#		if str(self.data)[:10] == "arm-gripop":
#			MoveArm([2,0,0])
#			self.sendMessage('arwgro,')
#		if str(self.data)[:10] == "arm-liteon":
#			MoveArm([0,0,1])
#			self.sendMessage('arlio,')
#		if str(self.data)[:10] == "arm-liteof":
#			MoveArm([0,0,0])
#			self.sendMessage('arlif,')
#		if str(self.data)[:10] == "arm-stopit":
#			StopArm()
#			self.sendMessage('rbstp,')

	def handleConnected(self):
		global x
	        x.send_to_roomba([128,131])       	
	        x.send_to_roomba([140,0,6])       	
	        x.send_to_roomba([45,10,57,10,42,10,54,10,43,10,55,10])
	        x.send_to_roomba([141,0])
		print self.address, 'connected'

	def handleClose(self):
		global x
		x.stop()
		x.off()
		print self.address, 'closed'


def websocketThread():
	global x
	server = SimpleWebSocketServer('', 65432, SimpleEcho)
	server.serveforever()

def sensorThread():
	global x
	print x.sensors.charge


websocketthread = Thread( target=websocketThread, args=() )
websocketthread.start()
websocketthread.join()

#sensorthread = Thread( target=sensorThread, args=() )
#sensorthread.start()
#sensorthread.join()

