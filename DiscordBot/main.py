from messageevents import *
from socket import *
from socket_listener import Listener
from threading import Thread
import time
import creds
import os

TOKEN = ''


def listen_for_client():
    s = socket(AF_INET, SOCK_STREAM)
    s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
    s.bind((creds.IP, creds.PORT))
    listener = Listener()
    while True:
        try:
            listener.listen(sock=s)
        except Exception:
            pass
        time.sleep(.2)


def backup_server():
    while True:
        os.system(f'cmd /c mysqldump --user {creds.SQL_DB_USER} --password {creds.SQL_DB_PASS} --all-databases > backup.sql')
        print("Backup created...")
        time.sleep(30)


if __name__ == '__main__':
    thread = Thread(target=listen_for_client, args=())
    thread.setDaemon(True)
    thread.start()

    backup_thread = Thread(target=backup_server, args=())
    backup_thread.setDaemon(True)
    backup_thread.start()

    client = discord.Client()
    print("STARTING")
    message_events = MessageEvents(client=client)
    message_events.run()

    client.run(TOKEN)




