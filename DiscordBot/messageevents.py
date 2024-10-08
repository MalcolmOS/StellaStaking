import enum
from sqlconnection import Database
import discord
import time

ADMIN_ROLE = 702605643927781659
ARCHIVE_REACTION_ID = 716522280317878273
ARCHIVE_CATEGORY_ID = 918684392954658877
DEPOSIT_CHANNEL = 919449370804510832
TICKETS_CATEGORY_ID = 919396433596129280
PREFIX = '!'


class InvalidLedgerException(Exception):
    def __init__(self, message='Invalid Ledger'):
        super(InvalidLedgerException, self).__init__(message)


class Bank:
    def __init__(self):
        pass

    @staticmethod
    def get_balance(user):
        sql = Database()
        balances = {"07": sql.get_osrs_balance(user=user), "rs3": sql.get_rs3_balance(user=user)}
        sql.close()
        return balances

    @staticmethod
    def add(user, amount, ledger):
        if ledger == '07':
            sql = Database()
            sql.add_osrs(user=user, amount=amount)
            sql.close()
        elif ledger == 'rs3':
            sql = Database()
            sql.add_rs3(user=user, amount=amount)
            sql.close()
        else:
            raise InvalidLedgerException()

    @staticmethod
    def withdraw(user, amount, ledger):
        if ledger == '07':
            sql = Database()
            sql.remove_osrs(user=user, amount=amount)
            sql.close()
        elif ledger == 'rs3':
            sql = Database()
            sql.remove_rs3(user=user, amount=amount)
            sql.close()
        else:
            raise InvalidLedgerException()


class CommandExecutor:
    def __init__(self, client):
        self.client = client
        self.content = None
        self.message = None
        self.bank = Bank()

    def set_context(self, message):
        self.content = message.content.lower()
        self.message = message

    async def run_commands(self):
        try:
            await self.run_regular_commands()
            if ADMIN_ROLE in [role.id for role in self.message.author.roles]:
                await self.run_admin_commands()
        except IndexError:
            await self.message.channel.send("Error command. Please review the commands by using !help")
        except InvalidLedgerException:
            await self.message.channel.send("Invalid ledger, use either 07 or RS3.")

    async def run_regular_commands(self):
        self.add_user()

        if self.is_command(command=Command.HELP):
            await self.display_help()
        elif self.is_command(command=Command.BALANCE):
            await self.display_balance()
        elif self.is_command(command=Command.DEPOSIT):
            await self.initiate_deposit()
        elif self.is_command(command=Command.PRIVATE):
            await self.set_channel_status()
        elif self.is_command(command=Command.STAKE):
            await self.run_stake()

    async def run_admin_commands(self):
        if self.is_command(command=AdminCommand.CANCEL):
            await self.cancel_stake()
        elif self.is_command(command=AdminCommand.CLOSE):
            await self.archive(channel=self.message.channel)
        elif self.is_command(command=AdminCommand.TRANSFER):
            await self.transfer()
        elif self.is_command(command=AdminCommand.WITHDRAW):
            await self.withdraw()
        elif self.is_command(command=AdminCommand.WIN):
            await self.book_win()
        elif self.is_command(command=AdminCommand.LOSE):
            await self.book_loss()
        elif self.is_command(command=AdminCommand.UPDATE_TAX):
            await self.update_tax()
        elif self.is_command(command=AdminCommand.GET_TAX):
            sql = Database()
            await self.message.channel.send(f"Current tax rate is {sql.get_tax_rate()}%")
            sql.close()

    async def update_tax(self):
        splits = self.content.split(" ")
        rate = splits[1]
        sql = Database()
        sql.set_tax_rate(rate=rate)
        sql.close()
        embed = discord.Embed(title=f"Tax Adjusted", description=f"Tax rate updated to {rate}%", color=discord.Color.blue())
        await self.message.channel.send(embed=embed)

    async def display_help(self):
        desc = f"**User Commands**\n\n" \
               f"**!balance** - displays balance - usage: !balance\n" \
               f"**!deposit** - opens deposit ticket - usage: !deposit 50M 07/RS3\n" \
               f"**!help** - displays this message - usage: !help\n" \
               f"**!private** - turns on/off private balances - usage: !private on/off\n" \
               f"**!stake** - opens a stake ticket - usage: !stake 50M 07/RS3\n\n" \
               f"**Admin Commands**\n\n" \
               f"**!cancel** - cancels a stake for mentioned user - usage: !cancel @user\n" \
               f"**!close** - closes a ticket - usage: !close\n" \
               f"**!gettax** - grabs the current tax rate - usage: !gettax\n"\
               f"**!lose** - books a loss for mentioned user - usage: !lose @user\n" \
               f"**!transfer** - transfers GP to mentioned user - usage: !transfer @user 50M 07/RS3\n" \
               f"**!updatetax** - updates the current tax rate - usage: !updatetax 5\n" \
               f"**!win** - books win for mentioned user - usage: !win @user\n" \
               f"**!withdraw** - withdraws from mentioned user - usage !withdraw @user 50M 07/RS3\n"

        embed = discord.Embed(title=f"Stake Request", description=desc, color=discord.Color.blue())
        await self.message.channel.send(embed=embed)

    async def book_loss(self):
        try:
            user = self.message.mentions[0].id
            sql = Database()
            stake = sql.get_incomplete_stakes(user=user)
            sql.set_stake_result(stake_id=stake['id'], result='LOSE')
            amount_to_remove = float(stake['amount'])
            self.bank.withdraw(user=user, amount=amount_to_remove, ledger=stake['ledger'])
            sql.add_bank_transaction(admin=self.message.author.id, user=user, amount=amount_to_remove, action="LOSE", ledger=stake['ledger'])
            desc = f"Unlucky on the loss <@{user}>\n"\
                   f"Amount: {amount_to_remove}M {stake['ledger']}\n"\
                   f"New balance: {float(self.bank.get_balance(user=user)[stake['ledger']])}M {stake['ledger']}"
            embed = discord.Embed(title=f"Stake Result | Lost", description=desc, color=discord.Color.red())
            embed.set_thumbnail(url=self.message.mentions[0].avatar_url)
            await self.message.channel.send(embed=embed)
            sql.close()
        except TypeError:
            await self.message.channel.send(embed=f"There is no current stake set up with that user.")

    async def book_win(self):
        try:
            user = self.message.mentions[0].id
            sql = Database()
            stake = sql.get_incomplete_stakes(user=user)
            sql.set_stake_result(stake_id=stake['id'], result='WIN')
            rate = sql.get_tax_rate()
            amount_to_add = float(float(stake['amount']) * 2) * float(1-(float(rate)/100)) - float(stake['amount'])
            self.bank.add(user=user, amount=amount_to_add, ledger=stake['ledger'])
            sql.add_bank_transaction(admin=self.message.author.id, user=user, amount=amount_to_add, action="WIN", ledger=stake['ledger'])
            desc = f"Congratulations on the win <@{user}>\n"\
                   f"Amount: {stake['amount']}M {stake['ledger']}\n"\
                   f"New balance: {float(self.bank.get_balance(user=user)[stake['ledger']])}M {stake['ledger']}"
            embed = discord.Embed(title=f"Stake Result | Win", description=desc, color=discord.Color.green())
            embed.set_thumbnail(url=self.message.mentions[0].avatar_url)
            await self.message.channel.send(embed=embed)
            sql.close()
        except TypeError:
            await self.message.channel.send(embed=f"There is no current stake set up with that user.")

    async def withdraw(self):
        splits = self.content.split(" ")
        user = self.message.mentions[0].id
        amount = splits[2].replace("m", "")
        self.bank.withdraw(user=user, amount=amount, ledger=splits[3])
        embed = discord.Embed(title=f"Withdraw Request", description=f"<@{self.message.author.id}> withdrew **{amount}M {splits[3]}** from <@{user}>'s wallet.")
        await self.message.channel.send(embed=embed)
        sql = Database()
        sql.add_bank_transaction(admin=self.message.author.id, user=user, amount=amount, action="WITHDRAW", ledger=splits[3])
        sql.close()

    async def transfer(self):
        splits = self.content.split(" ")
        user = self.message.mentions[0].id
        amount = splits[2].replace("m", "")
        self.bank.add(user=user, amount=amount, ledger=splits[3])
        embed = discord.Embed(title=f"Transfer Request", description=f"<@{self.message.author.id}> transferred **{amount}M {splits[3]}** to <@{user}>'s wallet.")
        embed.set_thumbnail(url=self.message.mentions[0].avatar_url)
        await self.message.channel.send(embed=embed)
        sql = Database()
        sql.add_bank_transaction(admin=self.message.author.id, user=user, amount=amount, action="DEPOSIT", ledger=splits[3])
        sql.close()

    async def cancel_stake(self):
        sql = Database()
        sql.cancel_stake(user=self.message.mentions[0].id)
        sql.close()
        embed = discord.Embed(title=f"Stake Cancelled", description=f"<@{self.message.author.id}> cancelled <@{self.message.mentions[0].id}>'s stake.")
        await self.message.channel.send(embed=embed)
        await self.archive(channel=self.message.channel)

    async def run_stake(self):
        splits = self.content.split(" ")
        amount = splits[1].replace('m', '')
        ledger = splits[2].upper()
        balances = self.bank.get_balance(user=self.message.author.id)
        if int(amount) < 50:
            desc = f"Error requesting stake: You must stake a minimum of 50M"
            embed = discord.Embed(title=f"Minimum Stake Amount", description=desc, color=discord.Color.blue())
            embed.set_thumbnail(url=self.message.author.avatar_url)
            await self.message.channel.send(embed=embed)
        elif int(amount) > balances.get(ledger.lower()):
            desc = f"Sorry, insufficient funds <@{self.message.author.id}, to make a deposit go to <#{DEPOSIT_CHANNEL}>"
            embed = discord.Embed(title=f"Insufficient Funds", description=desc, color=discord.Color.blue())
            embed.set_thumbnail(url=self.message.author.avatar_url)
            await self.message.channel.send(embed=embed)
        elif not ledger.__eq__('07') and not ledger.__eq__('RS3'):
            raise InvalidLedgerException()
        else:
            channel = await self.get_channel(identifier='stake')
            sql = Database()
            rate = sql.get_tax_rate()
            incomplete = sql.get_incomplete_stakes(user=self.message.author.id)
            if incomplete:
                pool = int(int(incomplete['amount'] * 2))
                desc = f"<@{self.message.author.id}> You have an incomplete stake.\nPlease wait for a host to cancel or complete the stake.\n\n" \
                       f"Current {incomplete['ledger']} balance: {balances[incomplete['ledger'].lower()]}M\n\n" \
                       f"Stake amount: {incomplete['amount']}M {incomplete['ledger']}\n" \
                       f"Payout amount: {pool * (1-(rate/100)) - int(incomplete['amount'])}M {incomplete['ledger']}\n\n\n" \
                       f"Stake ID: {incomplete['id']}"
            else:
                stake = sql.create_stake(user=self.message.author.id, amount=amount, ledger=ledger)
                pool = int(int(amount * 2))
                desc = f"<@{self.message.author.id}> Your stake request has been sent for review\n\n" \
                       f"Current {ledger} balance: {balances[ledger.lower()]}M\n\n" \
                       f"Stake amount: {amount}M {ledger}\nPayout amount: {pool * (1-(rate/100)) - (int(amount))}M {ledger}\n\n\n" \
                       f"Stake ID: {stake}"
            embed = discord.Embed(title=f"Stake Request", description=desc, color=discord.Color.blue())
            embed.set_thumbnail(url=self.message.author.avatar_url)
            await channel.send(embed=embed)
            sql.close()
            time.sleep(0.5)
            await channel.send(f'<@{self.message.author.id}> <@&{ADMIN_ROLE}>')

    async def initiate_deposit(self):
        splits = self.content.split(" ")
        amount = splits[1].replace("m", "")
        ledger = splits[2].upper()
        if not ledger.__eq__('07') and not ledger.__eq__('RS3'):
            raise InvalidLedgerException()
        else:
            channel = await self.get_channel(identifier='deposit')
            if channel:
                desc = f"<@{self.message.author.id}> Your deposit request has been sent for review\n\nAmount: {amount}M {ledger}\n\nPlease provide your RSN in this channel."
                embed = discord.Embed(title=f"Deposit Request", description=desc, color=discord.Color.blue())
                await channel.send(embed=embed)
                history = await channel.history(oldest_first=True).flatten()
                await history[0].add_reaction(self.client.get_emoji(ARCHIVE_REACTION_ID))
                time.sleep(0.5)
                await channel.send(f'<@{self.message.author.id}> <@&{ADMIN_ROLE}>')

    async def archive(self, channel):
        archive = discord.utils.get(self.message.guild.channels, id=ARCHIVE_CATEGORY_ID)
        uid = channel.name.split("-")[2]
        user = await self.client.fetch_user(uid)
        perms = channel.overwrites_for(user)
        perms.read_messages = False
        perms.send_messages = False
        await channel.set_permissions(user, overwrite=perms)
        await channel.edit(name=channel.name.replace("stake-", "archived-sta-").replace("deposit-", "archived-dep-"), position=1, category=archive)

    async def get_channel(self, identifier):
        for channel in self.message.guild.channels:
            if channel.name == f'{identifier}-{self.message.author.name.lower()}-{self.message.author.id}':
                return channel
        if not self.channel_exists(identifier=identifier):
            return await self.create_channel(identifier=identifier)

    async def set_channel_status(self):
        splits = self.content.split(" ")
        status = splits[1]
        sql = Database()
        if status == 'on':
            sql.set_private(user=self.message.author.id, status=True)
            await self.message.channel.send("Private enabled")
        elif status == 'off':
            sql.set_private(user=self.message.author.id, status=False)
            await self.message.channel.send("Private disabled")
        sql.close()

    async def display_balance(self):
        balances = self.bank.get_balance(user=self.message.author.id)
        sql = Database()
        private = sql.is_user_private(user=self.message.author.id)
        weekly_wagers = sql.get_weekly_wagers(user=self.message.author.id)
        embed = discord.Embed(title=f"{self.message.author.name}'s Wallet")
        embed.add_field(name="OSRS Balance", value=f'{balances["07"]}M', inline=True)
        embed.add_field(name="RS3 Balance", value=f'{balances["rs3"]}M', inline=True)
        embed.add_field(name="OSRS Weekly Wagers", value=f'{weekly_wagers["07"]}M', inline=False)
        embed.add_field(name="RS3 Weekly Wagers", value=f'{weekly_wagers["rs3"]}M', inline=False)
        embed.set_thumbnail(url=self.message.author.avatar_url)
        sql.close()
        if private:
            await self.message.author.send(embed=embed)
        else:
            await self.message.channel.send(embed=embed)

    async def create_channel(self, identifier):
        guild = self.message.guild
        member = self.message.author
        overwrites = {
            guild.default_role: discord.PermissionOverwrite(read_messages=False),
            member: discord.PermissionOverwrite(read_messages=True),
        }
        tickets_category = discord.utils.get(self.message.guild.channels, id=TICKETS_CATEGORY_ID)
        channel = await guild.create_text_channel(f'{identifier}-{self.message.author.name}-{self.message.author.id}', overwrites=overwrites, position=1, category=tickets_category)
        return channel

    def channel_exists(self, identifier):
        for channel in self.message.guild.text_channels:
            if channel.name == f'{identifier}-{self.message.author.name.lower()}-{self.message.author.id}':
                return True

    def add_user(self):
        sql = Database()
        try:
            sql.add_user(user=self.message.author.id, name=f'{self.message.author.name} {self.message.author.discriminator}')
        except Exception as e:
            pass
        sql.close()

    def is_command(self, command):
        splits = self.content.split(" ")
        return splits[0].__eq__(PREFIX + command.value)


class MessageEvents:
    def __init__(self, client):
        self.client = client
        print("Loading Guild Message Events")

    def run(self):
        @self.client.event
        async def on_message(message):
            await self.check_guild_reactions(message=message)
            self.message = message
            if message.author.bot:
                return
            if message.content.lower().startswith(PREFIX):
                print(f'detected command: {message.content.lower()}')
                executor = CommandExecutor(client=self.client)
                executor.set_context(message=message)
                await executor.run_commands()

    async def check_guild_reactions(self, message):
        if message.guild:
            for channel in message.guild.channels:
                if "deposit-" in channel.name:
                    history = await channel.history(oldest_first=True).flatten()
                    if history[0].reactions:
                        if history[0].reactions[0].count > 1:
                            await self.archive(channel=channel, message=message)

    async def archive(self, channel, message):
        archive = discord.utils.get(message.guild.channels, id=ARCHIVE_CATEGORY_ID)

        user = await self.client.fetch_user(int(channel.name.replace("deposit-", "")))
        perms = channel.overwrites_for(user)
        perms.read_messages = False
        perms.send_messages = False

        await channel.set_permissions(user, overwrite=perms)
        await channel.edit(name=channel.name.replace("deposit-", "archived-dep-"), position=1, category=archive)


class Command(enum.Enum):
    BALANCE = 'balance'
    DEPOSIT = 'deposit'
    HELP = 'help'
    PRIVATE = 'private'
    STAKE = 'stake'


class AdminCommand(enum.Enum):
    CANCEL = 'cancel'
    CLOSE = 'close'
    GET_TAX = 'gettax'
    LOSE = 'lose'
    UPDATE_TAX = 'updatetax'
    TRANSFER = 'transfer'
    WIN = 'win'
    WITHDRAW = 'withdraw'
