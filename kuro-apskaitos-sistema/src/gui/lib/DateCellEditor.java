package gui.lib;

import java.awt.Color;
import java.awt.Component;
import java.sql.Date;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class DateCellEditor extends DefaultCellEditor implements TableCellEditor {

    Date oldDate;
    JTextField textField;

    public DateCellEditor() {
        super(new JTextField());
        textField = (JTextField) this.getComponent();
        textField.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
    }

    @Override
    public Object getCellEditorValue() {
        try {
            return Date.valueOf(textField.getText());
        } catch (IllegalArgumentException e) {
            return oldDate;
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        oldDate = (Date) value;
        textField.setText(value.toString());
        return textField;
    }
}
