package gui;

import gui.lib.DBPanel;
import gui.lib.DBPanelConfirmation;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lib.Company;

public class CompanyDialog extends javax.swing.JDialog {

    private final JFrame parentFrame;
    private final Company company;

    /**
     * Creates new form CompanyDialog
     */
    public CompanyDialog(JFrame parent, boolean modal, Company company) {
        super(parent, modal);

        this.parentFrame = parent;
        this.company = company;
        initComponents();

        nameLabel.setText(company.getName());
        codeLabel.setText(company.getCode());
        addressLabel.setText(company.getAddress());

        setTitle("Įmonės valdymas: " + company.getName());

        this.setLocationRelativeTo(parent);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        codeLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        companyEmployeesPanel1 = new CompanyEmployeesPanel(true, company);
        companyCarsPanel1 = new gui.CompanyCarsPanel(true, parentFrame, company);
        tripPanel1 = new gui.TripPanel(parentFrame, true, company);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        nameLabel.setText("pavadinimas");

        jLabel1.setText("Pavadinimas:");

        jLabel2.setText("Įmonės kodas:");

        codeLabel.setText("kodas");

        addressLabel.setText("adresas");

        jLabel3.setText("Adresas:");

        jTabbedPane1.addTab("Darbuotojai", companyEmployeesPanel1);
        jTabbedPane1.addTab("Mašinos", companyCarsPanel1);
        jTabbedPane1.addTab("Kelionės", tripPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameLabel)
                    .addComponent(addressLabel)
                    .addComponent(codeLabel))
                .addContainerGap(725, Short.MAX_VALUE))
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(codeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(addressLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        DBPanelConfirmation confirmation = new DBPanelConfirmation(this,
                jTabbedPane1.getComponents());

        int result = confirmation.showConfirmation();
        DBPanel panelWithError = confirmation.getPanelWithError();

        if (result != JOptionPane.CANCEL_OPTION && panelWithError == null) {
            setVisible(false);
            dispose();
        } else if (panelWithError != null) {
            jTabbedPane1.setSelectedComponent(panelWithError);
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addressLabel;
    private javax.swing.JLabel codeLabel;
    private gui.CompanyCarsPanel companyCarsPanel1;
    private gui.CompanyEmployeesPanel companyEmployeesPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel nameLabel;
    private gui.TripPanel tripPanel1;
    // End of variables declaration//GEN-END:variables
}