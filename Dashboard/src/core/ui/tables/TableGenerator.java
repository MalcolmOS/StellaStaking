package core.ui.tables;

import core.cache.BankCache;
import core.cache.StakeCache;
import core.cache.UserCache;
import core.ui.GUI;
import core.ui.UserReview;

import java.util.LinkedHashMap;

public class TableGenerator {

    private final BankCache bankCache;
    private final StakeCache stakeCache;
    private final UserCache userCache;

    private final GUI gui;

    public TableGenerator(GUI gui) {
        this.gui = gui;
        this.userCache = this.gui.getUserCache();
        this.stakeCache = this.gui.getStakeCache();
        this.bankCache = this.gui.getBankCache();
    }

    public TableDisplay getUserTable() {
        final TableDisplay table = new TableDisplay(new DataTable(new UserTable(this.userCache.getData())));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleUserTableClick(evt);
            }
        });

        return table;
    }

    public TableDisplay getFilteredUsersTable(LinkedHashMap<?,?> map) {
        final TableDisplay table = new TableDisplay(new DataTable(new UserTable(map)));

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleUserTableClick(evt);
            }
        });
        return table;
    }

    public TableDisplay getBankTable() {
        final BankTable table = new BankTable(this.bankCache.getData());
        table.setUserContext(this.userCache);
        return new TableDisplay(new DataTable(table));
    }

    public TableDisplay getStakesTable() {
        final StakesTable table = new StakesTable(this.stakeCache.getData());
        table.setUserContext(this.userCache);
        return new TableDisplay(new DataTable(table));
    }

    private void handleUserTableClick(java.awt.event.MouseEvent evt) {
        final int row = this.gui.getUserTable().rowAtPoint(evt.getPoint());
        final int col = this.gui.getUserTable().columnAtPoint(evt.getPoint());
        if (col == 0 && row >= 0) {
            final String id = String.valueOf(this.gui.getUserTable().getValueAt(row, 1));
            final UserReview review = new UserReview(id, this.gui);
            review.open();
        }
    }
}
