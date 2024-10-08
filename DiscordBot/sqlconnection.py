import mysql.connector
from time import time
import creds


class Database:
    def __init__(self):
        self.db = self.open_connection()
        self.cursor = self.db.cursor()
        self.result = None

    def get_tax_rate(self):
        command = f'SELECT * FROM TAX_RATE'
        print(f'Executing: {command}')
        self.cursor.execute(command, None)
        return self.cursor.fetchall()[0][0]

    def set_tax_rate(self, rate):
        command = f'UPDATE TAX_RATE SET RATE=%s'
        inserts = [rate]
        print(f'Executing: {command}')
        self.cursor.execute(command, inserts)
        self.db.commit()

    def add_user(self, user, name):
        command = f'INSERT INTO USERS (ID, NAME, OSRS_BALANCE, RS3_BALANCE, PRIVATE) VALUES(%s, %s, %s, %s, %s)'
        print(f'Executing: {command}')
        inserts = [user, name, 0, 0, 1]
        self.cursor.execute(command, inserts)
        self.db.commit()

    def is_user_private(self, user):
        command = f'SELECT PRIVATE FROM USERS WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [user]
        self.cursor.execute(command, inserts)
        return self.cursor.fetchall()[0][0] == 0

    def add_osrs(self, user, amount):
        current_balance = self.get_osrs_balance(user=user)
        new = float(current_balance) + float(amount)
        command = f'UPDATE USERS SET OSRS_BALANCE=%s WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [new, user]
        self.cursor.execute(command, inserts)
        self.db.commit()

    def add_rs3(self, user, amount):
        current_balance = self.get_rs3_balance(user=user)
        new = float(current_balance) + float(amount)
        command = f'UPDATE USERS SET RS3_BALANCE=%s WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [new, user]
        self.cursor.execute(command, inserts)
        self.db.commit()

    def remove_osrs(self, user, amount):
        current_balance = self.get_osrs_balance(user=user)
        new = int(current_balance) - int(amount)
        command = f'UPDATE USERS SET OSRS_BALANCE=%s WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [new, user]
        self.cursor.execute(command, inserts)
        self.db.commit()

    def remove_rs3(self, user, amount):
        current_balance = self.get_rs3_balance(user=user)
        new = int(current_balance) - int(amount)
        command = f'UPDATE USERS SET RS3_BALANCE=%s WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [new, user]
        self.cursor.execute(command, inserts)
        self.db.commit()

    def get_osrs_balance(self, user):
        command = f'SELECT OSRS_BALANCE FROM USERS WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [user]
        self.cursor.execute(command, inserts)
        return self.cursor.fetchall()[0][0]

    def get_rs3_balance(self, user):
        command = f'SELECT RS3_BALANCE FROM USERS WHERE ID=%s'
        print(f'Executing: {command}')
        inserts = [user]
        self.cursor.execute(command, inserts)
        return self.cursor.fetchall()[0][0]

    def add_bank_transaction(self, admin, user, amount, action, ledger):
        inserts = [admin, user, amount, action, ledger, time()]
        command = f'INSERT INTO BANK_DATA (REQUESTING_ADMIN, USER, AMOUNT, ACTION, LEDGER, TIMESTAMP) VALUES(%s, %s, %s, %s, %s, %s)'
        print(f'Executing: {command}')
        self.cursor.execute(command, inserts)
        self.db.commit()

    def create_stake(self, user, amount, ledger):

        inserts = [user, amount, ledger, "N/A", time()]
        command = f'INSERT INTO STAKES (USER, AMOUNT, LEDGER, RESULT, TIMESTAMP) VALUES(%s, %s, %s, %s, %s)'
        print(f'Executing: {command}')
        self.cursor.execute(command, inserts)
        self.db.commit()
        command = f'SELECT LAST_INSERT_ID()'
        print(f'Executing: {command}')
        self.cursor.execute(command, None)

        return self.cursor.fetchall()[0][0]

    def cancel_stake(self, user):
        inserts = ['CANCELLED', user, "N/A"]
        command = f'UPDATE STAKES SET RESULT=%s WHERE USER=%s AND RESULT=%s'
        print(f'Executing: {command}')
        self.cursor.execute(command, inserts)
        self.db.commit()

    def get_incomplete_stakes(self, user):
        try:
            inserts = [user, "N/A"]
            command = f'SELECT * FROM STAKES WHERE USER=%s AND RESULT=%s'
            print(f'Executing: {command}')
            self.cursor.execute(command, inserts)
            result = self.cursor.fetchall()[0]
            return {'id': result[0], 'user': result[1], 'amount': result[2], 'ledger': result[3], 'status': result[4], 'timestamp': result[5]}
        except IndexError:
            return False

    def set_stake_result(self, stake_id, result):
        inserts = [result, stake_id]
        command = f'UPDATE STAKES SET RESULT=%s WHERE STAKE_ID=%s'
        print(f'Executing: {command}')
        self.cursor.execute(command, inserts)
        self.db.commit()

    def get_weekly_wagers(self, user):
        try:
            week_time = 604_800
            osrs_total = 0
            rs3_total = 0
            command = f'SELECT * FROM STAKES'
            print(f'Executing: {command}')
            self.cursor.execute(command, None)
            for stake in self.cursor.fetchall():
                if float(stake[1]) == float(user):
                    if stake[4].__eq__('WIN') or stake[4].__eq__('LOSE'):
                        if float(stake[5]) + week_time > time():
                            if stake[3].__eq__('07'):
                                osrs_total += float(stake[2])
                            else:
                                rs3_total += float(stake[2])

            return {"07": osrs_total, "rs3": rs3_total}
        except Exception as e:
            return {"07": 0.00, "rs3": 0.00}

    def set_private(self, user, status):
        if status:
            inserts = [0, user]
            command = f'UPDATE USERS SET PRIVATE=%s WHERE ID=%s'
            print(f'Executing: {command}')
        else:
            inserts = [1, user]
            command = f'UPDATE USERS SET PRIVATE=%s WHERE ID=%s'
            print(f'Executing: {command}')
        self.cursor.execute(command, inserts)
        self.db.commit()

    def get_data_for_dashboard(self):
        users = {}
        stakes = {}
        bank_data = {}
        command = f'SELECT * FROM USERS'
        self.cursor.execute(command, None)
        for user in self.cursor.fetchall():
            u = {'name': user[1], '07': float(user[2]), 'rs3': float(user[3])}
            users[user[0]] = u

        command = f'SELECT * FROM STAKES'
        self.cursor.execute(command, None)
        for stake in self.cursor.fetchall():
            s = {'user': stake[1], 'amount': float(stake[2]), 'ledger': stake[3], 'result': stake[4], 'timestamp': stake[5]}
            stakes[stake[0]] = s

        command = f'SELECT * FROM BANK_DATA'
        self.cursor.execute(command, None)
        for bank_transaction in self.cursor.fetchall():
            b = {'requesting_admin': bank_transaction[1], 'user': bank_transaction[2], 'amount': float(bank_transaction[3]), 'action': bank_transaction[4], 'ledger': bank_transaction[5], 'timestamp': bank_transaction[6]}
            bank_data[bank_transaction[0]] = b

        print({"users": users, "stakes": stakes, "bank_data": bank_data})
        return {"users": users, "stakes": stakes, "bank_data": bank_data}

    @staticmethod
    def open_connection():
        return mysql.connector.connect(
            host=creds.SQL_DB_HOST,
            user=creds.SQL_DB_USER,
            password=creds.SQL_DB_PASS,
            database=creds.SQL_DB_DATABASE
        )

    def close(self):
        self.db.close()
