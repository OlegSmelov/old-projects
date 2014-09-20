package gui;

import gui.lib.DBCellRenderer;
import gui.lib.DBPanel;
import gui.lib.DBTableModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lib.DatabaseObject;
import lib.Employee;
import lib.Trip;

public class TripEmployeesPanel extends DBPanel {

    private final Trip trip;
    private final AbstractTableModel tableModel;
    private String[] columnNames = {"Asmens kodas", "Vardas", "Pavardė", "Banko sąskaitos nr.", "Važiavo"};

    private final List<Employee> tripEmployees = new ArrayList<Employee>();

    public TripEmployeesPanel() {
        this(false, null);
    }

    /**
     * Creates new form CompanyEmployeesPanel
     */
    public TripEmployeesPanel(boolean autoUpdate, final Trip trip) {
        this.trip = trip;
        this.tableModel = new DBTableModel(columnNames, objects) {

            @Override
            public Object getObjectValue(DatabaseObject object, int row, int col) {
                Employee employee = (Employee) object;
                switch (col) {
                    case 0:
                        return employee.getSSN();
                    case 1:
                        return employee.getFirstName();
                    case 2:
                        return employee.getLastName();
                    case 3:
                        return employee.getBankAccount();
                    case 4:
                        return getEmployeeTookTrip(employee);
                    default:
                        return null;
                }
            }

            @Override
            public boolean setObjectValue(DatabaseObject object, Object value, int row, int col) {
                if (object != null && !object.equals(getObjectValue(object, row, col))) {
                    Employee employee = (Employee) object;
                    switch (col) {
                        case 4:
                            if (getEmployeeTookTrip(employee)) {
                                trip.removeEmployee(employee);
                            } else {
                                trip.addEmployee(employee);
                            }
                            updateEmployeeList();
                            break;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean isObjectEditable(DatabaseObject object, int col) {
                return col == 4;
            }
        };

        initComponents();

        if (autoUpdate) {
            repopulateEmployeeTable();
        }

        employeeTable.setDefaultRenderer(Object.class, new DBCellRenderer());
    }

    private boolean getEmployeeTookTrip(Employee employee) {
        for (Employee e : tripEmployees) {
            if (employee.getSSN().equals(e.getSSN())) {
                return true;
            }
        }
        return false;
    }

    private void updateEmployeeList() {
        List<Employee> fetchedEmployees = trip.fetchEmployees();

        tripEmployees.clear();
        if (fetchedEmployees != null) {
            tripEmployees.addAll(fetchedEmployees);
        }
    }

    @Override
    public void resetData() {
        repopulateEmployeeTable();
    }

    @Override
    public void redrawData() {
        tableModel.fireTableDataChanged();
    }

    private void repopulateEmployeeTable() {
        updateEmployeeList();

        List<Employee> fetchedEmployees = tripEmployees;
        if (showAllEmployeesCheckbox.isSelected()) {
            fetchedEmployees = Employee.fetchByCompanyCode(trip.getCompanyCode());
        }

        objects.clear();
        if (fetchedEmployees != null) {
            objects.addAll(fetchedEmployees);
        }

        tableModel.fireTableDataChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();
        showAllEmployeesCheckbox = new javax.swing.JCheckBox();

        employeeTable.setModel(tableModel);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(employeeTable);

        showAllEmployeesCheckbox.setText("Rodyti visus įmonės darbuotojus");
        showAllEmployeesCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllEmployeesCheckboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(showAllEmployeesCheckbox)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showAllEmployeesCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showAllEmployeesCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllEmployeesCheckboxActionPerformed
        repopulateEmployeeTable();
    }//GEN-LAST:event_showAllEmployeesCheckboxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable employeeTable;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox showAllEmployeesCheckbox;
    // End of variables declaration//GEN-END:variables
}
