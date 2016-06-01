import socket
from skimage import io

port = 9090

client_socket = socket.socket()
client_socket.connect(('192.168.56.1', port))
print("Connected")


def send_img(client_socket, img):
    buffer = img.ravel().tobytes()
    print('Buffer length:', len(buffer))
    
    client_socket.send(len(buffer).to_bytes(8, byteorder='big'))
    client_socket.send(img.shape[0].to_bytes(4, byteorder='big'))
    client_socket.send(img.shape[1].to_bytes(4, byteorder='big'))
    client_socket.send(buffer)

img = io.imread("pythonlogo.jpg")
send_img(client_socket, img)

img = io.imread("javalogo.jpg")
send_img(client_socket, img)

client_socket.close()
print('Connection closed')