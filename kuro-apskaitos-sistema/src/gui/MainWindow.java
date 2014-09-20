package gui;

import gui.lib.DBPanel;
import gui.lib.DBPanelConfirmation;
import java.awt.Component;
import javax.swing.JOptionPane;
import lib.DatabaseObject;

public class MainWindow extends javax.swing.JFrame {

    public MainWindow() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jScrollBar1 = new javax.swing.JScrollBar();
                companyPanel2 = new gui.CompanyPanel(this, true);

                setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                setTitle("Kuro apskaitos sistema");
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                                formWindowClosing(evt);
                        }
                        public void windowClosed(java.awt.event.WindowEvent evt) {
                                formWindowClosed(evt);
                        }
                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(companyPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(companyPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        DBPanelConfirmation confirmation = new DBPanelConfirmation(this,
                new Component[]{companyPanel2});

        int result = confirmation.showConfirmation();
        DBPanel panelWithError = confirmation.getPanelWithError();

        if (result != JOptionPane.CANCEL_OPTION && panelWithError == null) {
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

        private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
            DatabaseObject.closeConnetion();
        }//GEN-LAST:event_formWindowClosed

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private gui.CompanyPanel companyPanel2;
        private javax.swing.JScrollBar jScrollBar1;
        // End of variables declaration//GEN-END:variables
}
