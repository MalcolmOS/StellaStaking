package core.ui.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.util.HashMap;

public class TableDisplay extends JTable {

    private final HashMap<String, Integer> columnMinSize = new HashMap<String, Integer>();
    private final HashMap<String, Integer> columnMaxSize = new HashMap<String, Integer>();

    public TableDisplay(TableModel model) {
        super.setModel(model);
        this.setMap();

        for (String key : this.columnMaxSize.keySet()) {
            try {
                super.getColumn(key).setMaxWidth(this.columnMaxSize.get(key));
                super.getColumn(key).setWidth(this.columnMaxSize.get(key));
            } catch (IllegalArgumentException ignored) {

            }
        }
        for (String key : this.columnMinSize.keySet()) {
            try {
                super.getColumn(key).setMinWidth(this.columnMinSize.get(key));
            } catch (IllegalArgumentException ignored) {

            }
        }

        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        super.setDefaultRenderer(String.class, centerRenderer);
        super.setDefaultRenderer(Integer.class, centerRenderer);
        super.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < super.getColumnCount(); i++) {
            super.getColumnModel().getColumn(i).setResizable(false);
        }
    }

    private void setMap() {
        this.columnMaxSize.put("", 25);
        this.columnMinSize.put("", 1);

        this.columnMaxSize.put("Ledger", 70);
        this.columnMinSize.put("Ledger", 1);
    }
}