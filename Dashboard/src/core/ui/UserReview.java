package core.ui;

import core.ui.tables.DataTable;
import core.ui.tables.TableDisplay;
import core.ui.tables.UserReviewTable;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;

public class UserReview {

    private final static DecimalFormat DF = new DecimalFormat("0.00");
    private final static String PATTERN = "#,##0.00;(#,##0.00)";

    static {
        DF.applyPattern(PATTERN);
    }

    private final JFrame frame = new JFrame("User Review");

    private final String id;
    private final GUI gui;

    public UserReview(String id, GUI gui) {
        this.id = id;
        this.gui = gui;
        this.init();
    }

    private TableDisplay getTable() {
        return new TableDisplay(new DataTable(new UserReviewTable(this.getMap())));
    }

    public void open() {
        this.frame.setVisible(true);
    }

    public void close() {
        this.frame.setVisible(false);
        this.frame.dispose();
    }

    private void init() {
        this.frame.setSize(350, 260);
        this.frame.setResizable(false);
        this.frame.setLayout(null);
        final JScrollPane scrollPane = new JScrollPane();
        final JTable table = this.getTable();

        this.frame.add(scrollPane);
        scrollPane.setViewportView(table);
        scrollPane.setBounds(0,0,350,280);

    }

    private LinkedHashMap<String, String> getMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("Name", this.gui.getUserCache().getName(this.id));
        map.put("Unique ID", this.id);
        map.put("OSRS Balance", DF.format(this.gui.getUserCache().getOSRSBalance(this.id)));
        map.put("RS3 Balance", DF.format(this.gui.getUserCache().getRS3Balance(this.id)));
        map.put("Total OSRS Stakes", DF.format(this.gui.getStakeCache().getTotalOSRSForUser(this.id)));
        map.put("Total OSRS Wins", DF.format(this.gui.getStakeCache().getTotalOSRSForUserByResult(this.id, "WIN")));
        map.put("Total OSRS Losses", DF.format(this.gui.getStakeCache().getTotalOSRSForUserByResult(this.id, "LOSE")));
        map.put("Total OSRS Past Week", DF.format(this.gui.getStakeCache().getWeeklyOSRSForUser(this.id)));
        map.put("Total RS3 Stakes", DF.format(this.gui.getStakeCache().getTotalRS3ForUser(this.id)));
        map.put("Total RS3 Wins",  DF.format(this.gui.getStakeCache().getTotalRS3ForUserByResult(this.id, "WIN")));
        map.put("Total RS3 Losses", DF.format(this.gui.getStakeCache().getTotalRS3ForUserByResult(this.id, "LOSE")));
        map.put("Total RS3 Past Week", DF.format(this.gui.getStakeCache().getWeeklyRS3ForUser(this.id)));
        return map;
    }
}
