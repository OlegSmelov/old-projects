package gui.lib;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import lib.DatabaseObject;

public class DBCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        TableModel m = table.getModel();
        
        if (!isSelected) {
            if (m instanceof DBTableModel) {
                DBTableModel tableModel = (DBTableModel) m;

                DatabaseObject databaseObject = tableModel.getDatabaseObject(row);
                if (databaseObject.hasErrors()) {
                    c.setBackground(Color.red);
                    c.setForeground(table.getForeground());
                } else if (databaseObject.isCreated()) {
                    if (!m.isCellEditable(row, column)) {
                        c.setBackground(new Color(174, 197, 223));
                        c.setForeground(table.getForeground());
                    } else if (!databaseObject.isUpdated()) {
                        c.setBackground(new Color(234, 231, 200));
                        c.setForeground(table.getForeground());
                    } else {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                } else {
                    if (!m.isCellEditable(row, column)) {
                        c.setBackground(new Color(174, 197, 223));
                        c.setForeground(table.getForeground());
                    } else {
                        c.setBackground(new Color(234, 231, 200));
                        c.setForeground(table.getForeground());
                    }
                }
            }
        }

        return c;
    }
}
