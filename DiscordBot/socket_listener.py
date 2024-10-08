import socket
from sqlconnection import Database

ADMIN_PASSWORD = ''


class Listener:
    def __init__(self):
        self.socket = None
        self.socket_data = {}
        self.conn, self.address = None, None
        self.raw_data = None

    def listen(self, sock):
        self.accept_socket(sock=sock)
        print(f'Incoming request from {self.address} - {self.raw_data}')
        if self.raw_data:
            self.process()

    def accept_socket(self, sock):
        try:
            self.socket = sock
            self.socket.listen(5)
            self.conn, self.address = self.socket.accept()
            self.conn.settimeout(5)
            self.address = self.address[0]
            raw_data = self.conn.recv(1024)
            if not raw_data:
                return
            self.raw_data = raw_data.decode('utf-8', 'ignore').replace('\n', '')
        except socket.timeout:
            pass

    def process(self):
        splits = self.raw_data.split(';')
        if splits[1] == ADMIN_PASSWORD:
            sql = Database()
            data = sql.get_data_for_dashboard()
            self.conn.send(f"{data}\n".encode())
        else:
            self.conn.send(f"You have a small dick\n".encode())

        self.socket = None

