package gui;

import gui.lib.DBCellRenderer;
import gui.lib.DBPanel;
import gui.lib.DBTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import lib.Car;
import lib.CarTakesTrip;
import lib.DatabaseObject;
import lib.Trip;

public class TripCarsPanel extends DBPanel {

    private final Trip trip;
    private final AbstractTableModel tableModel;
    private String[] columnNames = {"Registracijos numeris", "Markė", "Atsakingo asmens AK", "Draudimo pabaigos data",
        "Techninė apžiūra iki", "Skait. nuo", "Skait. iki", "Buvo naudojama"};

    private final List<Car> tripCars = new ArrayList<Car>();
    private final HashMap<String, CarTakesTrip> carTakesTripMap = new HashMap<String, CarTakesTrip>();

    /**
     * Creates new form TripCarsPanel
     */
    public TripCarsPanel() {
        this(false, null);
    }

    public TripCarsPanel(boolean autoUpdate, final Trip trip) {
        this.trip = trip;
        this.tableModel = new DBTableModel(columnNames, objects) {
            @Override
            public Object getObjectValue(DatabaseObject object, int row, int col) {
                Car car = (Car) object;
                CarTakesTrip carTakesTrip;
                switch (col) {
                    case 0:
                        return car.getRegistrationNumber();
                    case 1:
                        return car.getBrand();
                    case 2:
                        return car.getAccountableEmployeeSSN();
                    case 3:
                        return car.getInsuranceExpirationDate();
                    case 4:
                        return car.getTechnicalCheckExpirationDate();
                    case 5:
                        carTakesTrip = carTakesTripMap.get(car.getRegistrationNumber());
                        if (carTakesTrip != null) {
                            return carTakesTrip.getStartCounter();
                        }
                        return "---";
                    case 6:
                        carTakesTrip = carTakesTripMap.get(car.getRegistrationNumber());
                        if (carTakesTrip != null) {
                            return carTakesTrip.getEndCounter();
                        }
                        return "---";
                    case 7:
                        return getCarTookTrip(car);
                    default:
                        return null;
                }
            }

            @Override
            public boolean setObjectValue(DatabaseObject object, Object value, int row, int col) {
                if (object != null && !object.equals(getObjectValue(object, row, col))) {
                    Car car = (Car) object;
                    CarTakesTrip carTakesTrip;
                    switch (col) {
                        case 5:
                            carTakesTrip = carTakesTripMap.get(car.getRegistrationNumber());
                            if (carTakesTrip != null) {
                                if ((Long) value >= carTakesTrip.getEndCounter()) {
                                    showCounterValuesError();
                                    break;
                                }
                                if (trip.updateCarParams(car, (Long) value, carTakesTrip.getEndCounter())) {
                                    carTakesTrip.setStartCounter((Long) value);
                                }
                            }
                            break;
                        case 6:
                            carTakesTrip = carTakesTripMap.get(car.getRegistrationNumber());
                            if (carTakesTrip != null) {
                                if (carTakesTrip.getStartCounter() >= (Long) value) {
                                    showCounterValuesError();
                                    break;
                                }
                                if (trip.updateCarParams(car, carTakesTrip.getStartCounter(), (Long) value)) {
                                    carTakesTrip.setEndCounter((Long) value);
                                }
                            }
                            break;
                        case 7:
                            if (getCarTookTrip(car)) {
                                trip.removeCar(car);
                                carTakesTripMap.remove(car.getRegistrationNumber());
                            } else {
                                trip.addCar(car, 0, 1);
                                carTakesTripMap.put(car.getRegistrationNumber(),
                                        new CarTakesTrip(car.getRegistrationNumber(), trip.getID(), 0, 1));
                            }
                            updateCarList();
                            fireTableCellUpdated(row, 5);
                            fireTableCellUpdated(row, 6);
                            break;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean isObjectEditable(DatabaseObject object, int col) {
                Car car = (Car) object;
                if (getCarTookTrip(car)) {
                    return true;
                } else {
                    return (col < 5) || (col > 6);
                }
            }
        };

        initComponents();

        if (autoUpdate) {
            repopulateTripCarTable();
        }

        tripCarTable.setDefaultRenderer(Object.class, new DBCellRenderer());
    }

    private void showCounterValuesError() {
        JOptionPane.showMessageDialog(this, "Netinkami skaitliuko parodymai",
                "Klaida", JOptionPane.ERROR_MESSAGE);
    }

    private boolean getCarTookTrip(Car car) {
        return carTakesTripMap.containsKey(car.getRegistrationNumber());
    }

    private void updateCarList() {
        List<Car> fetchedCars = trip.fetchCars();

        tripCars.clear();
        carTakesTripMap.clear();
        if (fetchedCars != null) {
            tripCars.addAll(fetchedCars);

            for (Car car : fetchedCars) {
                carTakesTripMap.put(car.getRegistrationNumber(), trip.getCarDetails(car));
            }
        }
    }

    @Override
    public void resetData() {
        repopulateTripCarTable();
    }

    @Override
    public void redrawData() {
        tableModel.fireTableDataChanged();
    }

    private void repopulateTripCarTable() {
        updateCarList();

        List<Car> fetchedCars = tripCars;
        if (showAllCarsCheckBox.isSelected()) {
            fetchedCars = Car.fetchListByCompanyCode(trip.getCompanyCode());
        }

        objects.clear();
        if (fetchedCars != null) {
            objects.addAll(fetchedCars);
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
        tripCarTable = new javax.swing.JTable();
        showAllCarsCheckBox = new javax.swing.JCheckBox();

        tripCarTable.setModel(tableModel);
        tripCarTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tripCarTable);

        showAllCarsCheckBox.setText("Rodyti visas įmonės mašinas");
        showAllCarsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllCarsCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(showAllCarsCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showAllCarsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showAllCarsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllCarsCheckBoxActionPerformed
        repopulateTripCarTable();
    }//GEN-LAST:event_showAllCarsCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox showAllCarsCheckBox;
    private javax.swing.JTable tripCarTable;
    // End of variables declaration//GEN-END:variables
}
