package core.ui.tables;

import javax.swing.table.AbstractTableModel;

public class DataTable extends AbstractTableModel {

    private final Object[][] data;
    private final String[] columns;

    private final TableData tableData;

    public DataTable(TableData tableData) {
        this.tableData = tableData;
        this.columns = this.tableData.getAllColumns();
        this.data = this.tableData.getAll();
    }

    public DataTable(String[] columns, Object[][] data) {
        this.data = data;
        this.columns = columns;
        this.tableData = null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getRowCount() {
        return this.data.length;
    }

    @Override
    public int getColumnCount() {
        return this.columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.data[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue != null) {
            this.data[rowIndex][columnIndex] = String.valueOf(aValue);
        }
    }

    @Override
    public String getColumnName(int column) {
        return this.columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return this.data[0][columnIndex].getClass();

    }
}
