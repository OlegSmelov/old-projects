package gui.lib;

import java.awt.Component;
import javax.swing.JOptionPane;

public class DBPanelConfirmation {

    private final Component parentComponent;
    private final Component[] components;
    private DBPanel panelWithError;


    public DBPanelConfirmation(Component parentComponent, Component[] components) {
        this.parentComponent = parentComponent;
        this.components = components;
        this.panelWithError = null;
    }

    private boolean getAllPanelsUpdated() {
        boolean updated = true;
        for (Component component : components) {
            if (component instanceof DBPanel) {
                DBPanel panel = (DBPanel) component;
                if (!panel.isUpdated()) {
                    updated = false;
                    break;
                }
            }
        }
        return updated;
    }

    private boolean updateAllPanels() {
        boolean errorOccured = false;
        for (Component component : components) {
            if (component instanceof DBPanel) {
                DBPanel panel = (DBPanel) component;
                if (!panel.isUpdated()) {
                    if (!panel.saveData()) {
                        errorOccured = true;
                        this.panelWithError = panel;
                        break;
                    }
                    panel.redrawData();
                }
            }
        }
        return errorOccured;
    }

    public int showConfirmation() {
        boolean updated = getAllPanelsUpdated();

        if (updated) {
            return JOptionPane.NO_OPTION;
        } else {
            int result = JOptionPane.showConfirmDialog(parentComponent,
                    "Išsaugoti pakeitimus?", "Kuro apskaitos sistema",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                updateAllPanels();
            }

            return result;
        }
    }

    public DBPanel getPanelWithError() {
        return panelWithError;
    }
}
