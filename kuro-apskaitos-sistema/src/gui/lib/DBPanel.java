package gui.lib;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import lib.DatabaseObject;

// Abstract panel that works with DatabaseObject type objects

public abstract class DBPanel extends javax.swing.JPanel {

    protected final List<DatabaseObject> objects = new ArrayList<DatabaseObject>();

    // reset any changes made to the DB
    public abstract void resetData();

    // redraw data
    public abstract void redrawData();

    // is there any pending changes to the DB?
    public boolean isUpdated() {
        for (DatabaseObject object : objects) {
            if (!object.isUpdated() || !object.isCreated()) {
                return false;
            }
        }
        return true;
    }

    // save any changes to the DB
    public boolean saveData() {
        boolean allOK = true;
        List<String> errors = null;

        for (DatabaseObject object : objects) {
            if (object.validate()) {
                if (!object.isCreated()) {
                    object.create();
                } else {
                    object.submitChanges();
                }
            } else {
                if (errors == null) {
                    errors = object.getErrors();
                }
                allOK = false;
            }
        }

        if (errors != null) {
            StringBuilder message = new StringBuilder();

            for (String error : errors) {
                message.append(error);
                message.append("\n");
            }

            JOptionPane.showMessageDialog(this, message.toString(), "Klaidos", JOptionPane.ERROR_MESSAGE);
        }

        return allOK;
    }
}
