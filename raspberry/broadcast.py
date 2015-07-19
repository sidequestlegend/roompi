from time import sleep
from socket import socket, AF_INET, SOCK_DGRAM, SOL_SOCKET, SO_BROADCAST, gethostbyname, gethostname
import subprocess

PORT = 65431
MAGIC = "roomba-pi"

s = socket(AF_INET, SOCK_DGRAM) #create UDP socket
s.bind(('', 0))
s.setsockopt(SOL_SOCKET, SO_BROADCAST, 1) #this is a broadcast socket

#p1 = subprocess.Popen(["ip", "addr","show","wlan0"], stdout=subprocess.PIPE)
#p2 = subprocess.Popen(["grep", "inet"], stdin=p1.stdout, stdout=subprocess.PIPE)
#p1.stdout.close()  # Allow p1 to receive a SIGPIPE if p2 exits.
#output,err = p2.communicate()
#output = output.strip()
#output = output[5:]
#output = output.split("/")[0]
#ip = output.split(".")
my_ip = "192.168.10.1"
while 1:
    data = MAGIC+my_ip
    s.sendto(data, ('<broadcast>', PORT))
    sleep(5)
