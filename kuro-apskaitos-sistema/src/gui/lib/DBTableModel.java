package gui.lib;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import lib.DatabaseObject;

public abstract class DBTableModel extends AbstractTableModel {

    private final String[] columnNames;
    private final List<DatabaseObject> objects;

    abstract public Object getObjectValue(DatabaseObject object, int row, int col);
    abstract public boolean setObjectValue(DatabaseObject object, Object value, int row, int col);
    abstract public boolean isObjectEditable(DatabaseObject object, int col);

    public DBTableModel(String[] columnNames, List<DatabaseObject> objects) {
        this.columnNames = columnNames;
        this.objects = objects;
    }

    public DatabaseObject getDatabaseObject(int row) {
        try {
            return objects.get(row);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return objects.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return getObjectValue(objects.get(row), row, col);
    }

    @Override
    public Class getColumnClass(int col) {
        Object value = getValueAt(0, col);
        if (value == null) {
            return Object.class;
        } else {
            return value.getClass();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (setObjectValue(objects.get(row), value, row, col)) {
            fireTableCellUpdated(row, col);
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return isObjectEditable(objects.get(row), col);
    }
}
