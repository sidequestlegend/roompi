# File RoombaSCI.py
import serial
import time
from cStringIO import StringIO
from Tkinter import *

# recommended string building tech. from
# http://www.skymind.com/~ocrow/python_string/
#wrapped in a string builder class
class StringBuilder:
    def __init__(self):
        self.collection = StringIO()
    def append(self, string):
        self.collection.write(string)
    def toString(self):
        return self.collection.getvalue()

class ByteBuilder:
    def __init__(self):
        self.collection = StringIO()
        self.strCollection = StringIO()
    def append(self, byte):
        x = hex(byte)
        x = x.replace("0x","")
        #http://aspn.activestate.com/ASPN/Cookbook/Python/Recipe/510399
        result =  chr( int (x, 16 ) )
        self.collection.write(result)
        self.strCollection.write("\\x" + x)
    def toCharString(self):
        return self.collection.getvalue()
    def toString(self):
        return self.strCollection.getvalue()


class ConvertToBytes:
    def __init__(self, data):
        self.int = data
    def high_byte(self):
        s = self.to_hex()
        s = str(s).replace('0x','')
        high = s[0:2]
        high = "0x" + high
        return int(high,16)
    def low_byte(self):
        s = self.to_hex()
        s = str(s).replace('0x','')
        low = s[2:]
        low = "0x" + low
        return int(low,16)
    def to_hex(self):
        s = hex(self.int)
        s = str(s).replace('0x','')
        while(len(s) < 4):
            s = "0" + s
        return "0x" + s

class Bumps(object):
    def __init__(self,data):
        self.data = data
    def __right(self):
        x = self.data & 1
        return bool(x > 0)
    right = property(__right)
    def __left(self):
        x = self.data & 2
        return bool(x > 0)
    left = property(__left)
        
class Buttons(object):
    def __init__(self,data):
        self.data = data
    def __max(self):
        x = self.data & 1
        return bool(x > 0)
    max = property(__max)
    def __clean(self):
        x = self.data & 2
        return bool(x > 0)
    clean = property(__clean)
    def __spot(self):
        x = self.data & 4
        return bool(x > 0)
    spot = property(__spot)
    def __power(self):
        x = self.data & 8
        return bool(x > 0)
    power = property(__power)

class Cliff(object):
    def __init__(self,dataList):
        self.dataList = dataList
    def __left(self):
        return bool(self.dataList[0] == 1)
    left = property(__left)
    def __front_left(self):
        return bool(self.dataList[1] == 1)
    front_left = property(__front_left)
    def __front_right(self):
        return bool(self.dataList[2] == 1)
    front_right = property(__front_right)
    def __right(self):
        return bool(self.dataList[3] == 1)
    right = property(__right)

class DirtDetector(object):
    def __init__(self,dataList):
        self.dataList = dataList
    def __left(self):
        return self.dataList[0]
    left = property(__left)
    def __right(self):
        return self.dataList[1]
    right = property(__right)

class MotorOvercurrents(object):
    def __init__(self,data):
        self.__data = data
    def __sidebrush(self):
        x = self.__data & 1
        return bool(x > 0)
    sidebrush = property(__sidebrush)
    def __vacuum(self):
        x = self.__data & 2
        return bool(x > 0)
    vacuum = property(__vacuum)
    def __mainbrush(self):
        x = self.__data & 4
        return bool(x > 0)
    mainbrush = property(__mainbrush)
    def __drive_right(self):
        x = self.__data & 8
        return bool(x > 0)
    drive_right = property(__drive_right)
    def __drive_left(self):
        x = self.__data & 16
        return bool(x > 0)
    drive_left = property(__drive_left)

class WheelDrops(object):
    def __init__(self,data):
        self.__data = data
    def __right(self):
        x = self.__data & 4
        return bool(x > 0)
    right = property(__right)
    def __left(self):
        x = self.__data & 8
        return bool(x > 0)
    left = property(__left)
    def __castor(self):
        x = self.__data & 16
        return bool(x > 0)
    castor = property(__castor)

class TwoBytes:
    def __init__(self, data):
        self.data = data
    def to_int16(self):
        high = hex(self.data[0])
        low = hex(self.data[1])

        high = str(high).replace("0x","")
        if (len(high) == 1):
            high = "0"+high
        low = str(low).replace("0x","")
        if(len(low) == 1):
            low = "0" +low
        cmb = "0x" + high + low
        intX = int(cmb,16)
        if intX > 32767:
            return intX - 65536
        return intX

    def to_uint16(self):
        high = hex(self.data[0])
        low = hex(self.data[1])

        high = str(high).replace("0x","")
        if (len(high) == 1):
            high = "0"+high
        low = str(low).replace("0x","")
        if(len(low) == 1):
            low = "0" +low
        cmb = "0x" + high + low
        intX = int(cmb,16)
        return intX

class SensorData(object):
    def __init__(self,data):
        self.__data = data

    def __bumps(self):
        return Bumps(self.__data[0])

    bumps = property(__bumps)

    def __wheel_drops(self):
        return WheelDrops(self.__data[0])
    wheel_drops = property(__wheel_drops)

    def __wall(self):
        return bool(self.__data[1] == 1)
    wall = property(__wall)
    
    def __cliff(self):
        return Cliff(self.__data[2:6])
    cliff = property(__cliff)
    
    def __virtual_wall(self):
        return bool(self.__data[6] == 1)
    virtual_wall = property(__virtual_wall)
    
    def __motor_overcurrents(self):
        return MotorOvercurrents(self.__data[7])
    motor_overcurrents = property(__motor_overcurrents)

    def __dirt_detector(self):
        return DirtDetector(self.__data[8:10])
    dirt_detector = property(__dirt_detector)

    def __remote_control_cmds(self):
        return self.__data[10]
    remote_control_cmds = property(__remote_control_cmds)
    
    def __buttons(self):
        return Buttons(self.__data[11])
    buttons = property(__buttons)

    def __distance(self):
        return TwoBytes(self.__data[12:14]).to_int16()
    distance = property(__distance)

    def __angle(self):
        return TwoBytes(self.__data[14:16]).to_int16()
    angle = property(__angle)
    
    def __charging_state(self):
        return self.__data[16]
    charging_state = property(__charging_state)

    def __voltage(self):
        return TwoBytes(self.__data[17:19]).to_uint16()
    voltage = property(__voltage)
    
    def __current(self):
        return TwoBytes(self.__data[19:21]).to_int16()
    current = property(__current)
    
    def __temperature(self):
        return self.__data[21];
    temperature = property(__temperature)

    def __charge(self):
        return TwoBytes(self.__data[22:24]).to_uint16()
    charge = property(__charge)
    
    def __capacity(self):
        print self.__data[24:26]
        return TwoBytes(self.__data[24:26]).to_uint16()
    capacity = property(__capacity)

    

class int16(object):
    def __init__(self, i):
        self.__val = 0
        self.__raw = i
        if (i < 0):
            self.__val = (65535 + i) + 1
        else:
            self.__val = i
    def __get_value(self):
        return self.__val
    value = property(__get_value)
            
class RoombaAPI(object):

    def __init__(self,port,baudrate):
        self.__speed = 255
        self.port = serial.Serial()
        #should be connected upon initialization. run again to verify connection settings
        self.port.port = port
        self.port.baudrate = baudrate
        self.port.timeout = 10

    def connect(self):
        if (self.port.isOpen() == False):
            self.port.open()
        #set connection to Rootooth at 57k
        print self.port
        self.port.write("$$$")
        time.sleep(.1)
        self.port.write("U,57.6,N\n")
        time.sleep(.1)
        self.wakeup()

    def __isconnected(self):
        return self.port.isopen()
    
    isconnected = property(__isconnected)

    def wakeup(self):
        print "waking up roomba"
        self.port.write("$$$")
        time.sleep(.1)
        self.port.write("S@,8080\n")
        time.sleep(.1)
        self.port.write("S&,8000\n")
        time.sleep(.1)
        self.port.write("S&,8080\n")
        time.sleep(.1)
        self.port.write("---\n")
        time.sleep(.1)
        self.start()

    def close(self):
        self.port.close()

    def send_to_roomba(self, data):
        count = 0
        bytes = ByteBuilder()
        while(count < len(data)):
              bytes.append(data[count])
              count = count + 1
        print "Sending: " + bytes.toString()
        self.port.write(bytes.toCharString())
        time.sleep(.2)
              
    def sendcmd(self, cmd):
        bytes =[]
        bytes.append(cmd)
        self.send_to_roomba(bytes)

    def start(self):
        self.sendcmd(128)

    def control(self):
        self.sendcmd(130)
        
    def safe(self):
        self.sendcmd(131)

    def full(self):
        self.sendcmd(132)

    def off(self):
        self.sendcmd(133)
        
    def spot(self):
        self.sendcmd(134)
        
    def clean(self):
        self.sendcmd(135)
        
    def max(self):
        self.sendcmd(136)
        
    def __get_speed(self):
        if self.__speed < 0:
            return 0
        if self.__speed > 500:
            return 500
        return self.__speed

    def __set_speed(self, speedInt):
        if speedInt < 0:
            self.__speed = 0
        elif speed > 500:
            self.__speed = 500
        else:
            self.__speed = speedInt

    speed = property(__get_speed,__set_speed)
    
    def drive(self, velocity, radius):
        vel = ConvertToBytes(int16(velocity).value)
        rad = ConvertToBytes(int16(radius).value)

        cmd = []
        cmd.append(137)
        cmd.append(vel.high_byte())
        cmd.append(vel.low_byte())
        cmd.append(rad.high_byte())
        cmd.append(rad.low_byte())

        self.send_to_roomba(cmd)

    def forward(self):
        self.drive(self.speed,-32768)

    def backward(self):
        self.drive(self.speed *-1,-32768)
        
    def left(self):
        self.drive(self.speed,2)
        
    def right(self):
        self.drive(self.speed,-2)
        
    def spin_left(self):
        self.drive(self.speed,1)
        
    def spin_right(self):
        self.drive(self.speed,-1)

    def stop(self):
        self.drive(0,0)
        
    def motors(self, data):
        cmd =[]
        cmd.append(138)
        cmd.append(data)
        self.send_to_roomba(cmd)
        
    def led(self, led,color,intensity):
        cmd = []
        cmd.append(139)
        cmd.append(led)
        cmd.append(color)
        cmd.append(intensity)
        self.send_to_roomba(cmd)
        
    def song(self):
        return
    
    def play(self, songNum):
        cmd = []
        cmd.append(141)
        cmd.append(songNum)
        self.send_to_roomba(cmd)
        
    def fdock(self):
        self.sendcmd(143)
        
    def __sensors(self):
        print "sensors command"
        cmd = []
        cmd.append(142)
        #cmd.append(148)
		#module.exports.Sensors = new Command('sensors', 142, 1);
		#module.exports.QueryList = new Command('query_list', 149);
		#module.exports.Stream = new Command('stream', 148);
		#module.exports.ToggleStream = new Command('pause_resume_stream', 150, 1);
        cmd.append(0)
        self.port.flushInput()
        self.send_to_roomba(cmd)
        #time.sleep(.2)
        s = self.port.read(26)
        if len(s) < 26:
            print s
            return None
        if len(s) > 26:
            return self.sensors
        data = []
        output = str(s)
        x = 0
        end = len(output)
        while(x < end):
            data.append(ord(output[x]))
            x = x + 1
        print data    
        return SensorData(data)

    sensors = property(__sensors)