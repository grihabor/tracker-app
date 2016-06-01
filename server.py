import socket
import matplotlib.pyplot as plt
import numpy as np

port = 9090
server_socket = socket.socket()
server_socket.bind(('', port))

while True:
    print(socket.gethostbyname(socket.gethostname()))
    print('Listening on {}'.format(port))
    server_socket.listen(1)
    connection, addr = server_socket.accept()

    print('Connected:', addr)
    
    def recv_image():
        buf = connection.recv(8)
        if not buf:
            raise Exception('Failed to get img size')
        size = int.from_bytes(buf, byteorder='big')
        print(buf)
        print('Size = ', size)
        
        buf = connection.recv(4)
        if not buf:
            raise Exception('Failed to get img height')
        height = int.from_bytes(buf, byteorder='big')
        
        buf = connection.recv(4)
        if not buf:
            raise Exception('Failed to get img width')
        width = int.from_bytes(buf, byteorder='big')
        print('img.shape =', (height, width))
        
        img = np.zeros((0), dtype=np.uint8)
        while size > 0:
            buffer = connection.recv(1024) 
            if not buffer:
                raise Exception("No data recieved")
            
            size -= len(buffer)
            
            print(size)
            print(len(buffer))
            print()
            
            img = np.append(img, np.frombuffer(buffer, dtype=np.uint8))
            
        print(img.shape)    
        try:
            img = img.reshape((height, width, -1))
            plt.imshow(img)
        except Exception:
            img = img.reshape((height, width))
            plt.imshow(img, cmap='gray')
        
        plt.show()

    try:
        while True:
            print("Recieving image...")    
            recv_image()
    except Exception as e:
        print(e)
    finally:
        connection.close()
        print('Connection closed')