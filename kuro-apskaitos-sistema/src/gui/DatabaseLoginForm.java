package gui;

import java.awt.Image;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import lib.DatabaseObject;

public class DatabaseLoginForm extends javax.swing.JFrame {

    private static final String HOSTNAME = "hostname";
    private static final String DATABASE = "database";
    private static final String USERNAME = "username";

    private final Preferences prefs;

    public DatabaseLoginForm() {
        initComponents();
        setLocationRelativeTo(null);
        this.setTitle("Kuro apskaitos valdymo sistema");

        try {
            Image im;
            im = ImageIO.read(getClass().getResource("/resources/gas.png"));
            this.setIconImage(im);

            Class<?> macAppClass = findLibrary("com.apple.eawt.Application");
            if (macAppClass != null) {
               Object macApp = macAppClass.getConstructor(new Class<?>[0]).newInstance();
               Method method = macAppClass.getMethod("setDockIconImage", Image.class);
               method.invoke(macApp, im);
            }
        } catch (Exception ex) {
        }

        prefs = Preferences.userNodeForPackage(DatabaseLoginForm.class);
        hostField.setText(prefs.get(HOSTNAME, "localhost"));
        dbNameField.setText(prefs.get(DATABASE, "apskaitadb"));
        userField.setText(prefs.get(USERNAME, "apskaita"));
    }

    public static Class<?> findLibrary(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dbNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Prisijungimas prie duomenų bazės");
        setResizable(false);

        hostField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Serverio adresas");

        jLabel2.setText("Duomenų bazė");

        jLabel3.setText("Slaptažodis:");

        jButton1.setText("Prisijungti");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Atšaukti");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Vartotojas:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1))
                    .add(userField)
                    .add(hostField)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, dbNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .add(passwordField)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel3)
                            .add(jLabel1)
                            .add(jLabel2))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(16, 16, 16)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hostField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dbNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(userField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 27, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Connection connection;
        String url = "jdbc:postgresql://" + hostField.getText() + "/" + dbNameField.getText();
        String user = userField.getText();

        try {
            connection = DriverManager.getConnection(url, user, new String(passwordField.getPassword()));

            DatabaseObject.setConnection(connection);

            prefs.put(HOSTNAME, hostField.getText());
            prefs.put(DATABASE, dbNameField.getText());
            prefs.put(USERNAME, userField.getText());

            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);

            this.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Klaida bandant prisijungti. Patikrinkite ar suvesti duomenys yra geri.");
            System.err.println(ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void hostFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hostFieldActionPerformed
    }//GEN-LAST:event_hostFieldActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField dbNameField;
    private javax.swing.JTextField hostField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField userField;
    // End of variables declaration//GEN-END:variables
}
