package core.ui;

import core.cache.BankCache;
import core.cache.StakeCache;
import core.cache.UserCache;
import core.exports.BankExport;
import core.exports.StakesExport;
import core.exports.UserExport;
import core.ui.tables.TableGenerator;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;

public class GUI {

    private final static DecimalFormat DF = new DecimalFormat("0.00");
    private final static String PATTERN = "#,##0.00;(#,##0.00)";

    static {
        DF.applyPattern(PATTERN);
    }

    private final static Rectangle TABLE_BOUNDS = new Rectangle(5, 120, 1475, 700);

    private final JFrame frame = new JFrame("Staking Dashboard");
    private final TableGenerator tableGenerator;
    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JPanel userPanel = new JPanel();
    private final JScrollPane userScrollPane = new JScrollPane();
    private JTable userTable;

    private final JPanel bankPanel = new JPanel();
    private final JScrollPane bankScrollPane = new JScrollPane();
    private final JTable bankTable;

    private final JPanel stakesPanel = new JPanel();
    private final JScrollPane stakesScrollPane = new JScrollPane();
    private final JTable stakesTable;

    private final BankCache bankCache;
    private final StakeCache stakeCache;
    private final UserCache userCache;

    private final JSONObject data;

    public GUI(JSONObject data) {

        this.data = data;
        this.userCache = new UserCache(this.data);
        this.stakeCache = new StakeCache(this.data);
        this.bankCache = new BankCache(this.data);

        this.tableGenerator = new TableGenerator(this);
        this.userTable = this.tableGenerator.getUserTable();
        this.bankTable = this.tableGenerator.getBankTable();
        this.stakesTable = this.tableGenerator.getStakesTable();
        this.init();
    }

    public JSONObject getData() {
        return this.data;
    }

    public JTable getUserTable() {
        return this.userTable;
    }

    public BankCache getBankCache() {
        return this.bankCache;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public StakeCache getStakeCache() {
        return this.stakeCache;
    }

    public TableGenerator getTableGenerator() {
        return this.tableGenerator;
    }

    public void open() {
        this.frame.setVisible(true);
    }

    public void close() {
        this.frame.setVisible(false);
        this.frame.dispose();
    }

    private void frameCloseEvent(WindowEvent e) {
        System.exit(0);
    }

    private void init() {
        this.frame.setSize(1500, 900);
        this.frame.getContentPane().setLayout(null);

        this.createUserPanel();
        this.createBankPanel();
        this.createStakesPanel();

        this.frame.getContentPane().add(this.tabbedPane);
        this.tabbedPane.setBounds(0, 10, 1490, 890);

        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frameCloseEvent(e);
            }
        });
        this.frame.setResizable(false);
    }

    public void repaintUserTable(JTable table) {

        this.userPanel.remove(this.userTable);
        this.userTable = table;

        this.userScrollPane.setViewportView(this.userTable);
        this.userPanel.add(this.userScrollPane);
        this.userScrollPane.setBounds(TABLE_BOUNDS);

        this.userPanel.validate();
        this.userPanel.repaint();
    }

    private void createUserPanel() {
        this.userPanel.setLayout(null);
        this.userPanel.add(this.userScrollPane);
        this.tabbedPane.addTab("Users", this.userPanel);
        this.userScrollPane.setViewportView(this.userTable);
        this.userScrollPane.setBounds(TABLE_BOUNDS);

        final JLabel totalUsers = new JLabel("Total Users: " + this.userCache.getTotalUsers());
        totalUsers.setBounds(5, 0, 200, 20);
        this.userPanel.add(totalUsers);

        final JLabel totalOSRSBalance = new JLabel("Total OSRS Balance: " + DF.format(this.userCache.getTotalBalanceByLedger("07")) + "M");
        totalOSRSBalance.setBounds(5, 20, 200, 20);
        this.userPanel.add(totalOSRSBalance);

        final JLabel averageOSRSBalance = new JLabel("Average OSRS Balance: " + DF.format(this.userCache.getAverageBalanceByLedger("07")) + "M");
        averageOSRSBalance.setBounds(5, 40, 200, 20);
        this.userPanel.add(averageOSRSBalance);

        final JLabel totalRS3Balance = new JLabel("Total RS3 Balance: " + DF.format(this.userCache.getTotalBalanceByLedger("rs3")) + "M");
        totalRS3Balance.setBounds(5, 60, 200, 20);
        this.userPanel.add(totalRS3Balance);

        final JLabel averageRS3Balance = new JLabel("Average RS3 Balance: " + DF.format(this.userCache.getAverageBalanceByLedger("rs3")) + "M");
        averageRS3Balance.setBounds(5, 80, 200, 20);
        this.userPanel.add(averageRS3Balance);

        final JButton exportButton = new JButton("Export");
        exportButton.setBounds(1275, 5, 200, 40);
        this.userPanel.add(exportButton);
        exportButton.addActionListener(this::exportUsers);

        final JButton filterButton = new JButton("Filter");
        filterButton.setBounds(1275, 50, 200, 40);
        this.userPanel.add(filterButton);
        filterButton.addActionListener(this::filterUsers);
    }

    private void createBankPanel() {
        this.bankPanel.setLayout(null);
        this.bankPanel.add(this.bankScrollPane);
        this.tabbedPane.addTab("Bank Data", this.bankPanel);
        this.bankScrollPane.setViewportView(this.bankTable);
        this.bankScrollPane.setBounds(TABLE_BOUNDS);

        final JLabel totalOSRSDeposits = new JLabel("Total OSRS Deposits: " + DF.format(this.bankCache.getTotalOSRSAction("DEPOSIT")) + "M");
        totalOSRSDeposits.setBounds(5, 0, 200, 20);
        this.bankPanel.add(totalOSRSDeposits);

        final JLabel totalOSRSWithdraws = new JLabel("Total OSRS Withdraws: " + DF.format(this.bankCache.getTotalOSRSAction("withdraw")) + "M");
        totalOSRSWithdraws.setBounds(5, 20, 200, 20);
        this.bankPanel.add(totalOSRSWithdraws);

        final JLabel totalRS3Deposits = new JLabel("Total RS3 Deposits: " + DF.format(this.bankCache.getTotalRS3Action("DEPOSIT")) + "M");
        totalRS3Deposits.setBounds(5, 40, 200, 20);
        this.bankPanel.add(totalRS3Deposits);

        final JLabel totalRS3Withdraws = new JLabel("Total RS3 Withdraws: " + DF.format(this.bankCache.getTotalRS3Action("WITHDRAW")) + "M");
        totalRS3Withdraws.setBounds(5, 60, 200, 20);
        this.bankPanel.add(totalRS3Withdraws);

        final JButton exportButton = new JButton("Export");
        exportButton.setBounds(1275, 5, 200, 40);
        this.bankPanel.add(exportButton);
        exportButton.addActionListener(this::exportBank);

    }

    private void createStakesPanel() {
        this.stakesPanel.setLayout(null);
        this.stakesPanel.add(this.stakesScrollPane);
        this.tabbedPane.addTab("Stakes", this.stakesPanel);
        this.stakesScrollPane.setViewportView(this.stakesTable);
        this.stakesScrollPane.setBounds(TABLE_BOUNDS);

        final JLabel totalOSRSStakes = new JLabel("Total OSRS Stakes: " + DF.format(this.stakeCache.getTotalOSRSStakes()) + "M");
        totalOSRSStakes.setBounds(5, 0, 200, 20);
        this.stakesPanel.add(totalOSRSStakes);

        final JLabel totalOSRSUserWins = new JLabel("Total OSRS User Wins: " + DF.format(this.stakeCache.getTotalOSRSByResult("WIN")) + "M");
        totalOSRSUserWins.setBounds(5, 20, 200, 20);
        this.stakesPanel.add(totalOSRSUserWins);

        final JLabel totalOSRSUserLosses = new JLabel("Total OSRS User Losses: " + DF.format(this.stakeCache.getTotalOSRSByResult("LOSE")) + "M");
        totalOSRSUserLosses.setBounds(5, 40, 200, 20);
        this.stakesPanel.add(totalOSRSUserLosses);

        final JLabel totalRS3Stakes = new JLabel("Total RS3 Stakes: " + DF.format(this.stakeCache.getTotalRS3Stakes()) + "M");
        totalRS3Stakes.setBounds(5, 60, 200, 20);
        this.stakesPanel.add(totalRS3Stakes);

        final JLabel totalRS3UserWins = new JLabel("Total RS3 User Wins: " + DF.format(this.stakeCache.getTotalRS3ByResult("WIN")) + "M");
        totalRS3UserWins.setBounds(5, 80, 200, 20);
        this.stakesPanel.add(totalRS3UserWins);

        final JLabel totalRS3UserLosses = new JLabel("Total RS3 User Losses: " + DF.format(this.stakeCache.getTotalRS3ByResult("LOSE")) + "M");
        totalRS3UserLosses.setBounds(5, 100, 200, 20);
        this.stakesPanel.add(totalRS3UserLosses);

        final JButton exportButton = new JButton("Export");
        exportButton.setBounds(1275, 5, 200, 40);
        this.stakesPanel.add(exportButton);
        exportButton.addActionListener(this::exportStakes);

    }

    private void exportBank(ActionEvent e) {
        try {
            final BankExport export = new BankExport(this.data);
            export.exportAll();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void exportStakes(ActionEvent e) {
        try {
            final StakesExport export = new StakesExport(this.data);
            export.exportAll();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void exportUsers(ActionEvent e) {
        try {
            final UserExport export = new UserExport(this.data);
            export.exportAll();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void filterUsers(ActionEvent e){
        UserFilter filter = new UserFilter(this);
        filter.open();
    }

}
