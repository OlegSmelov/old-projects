package gui;

import lib.Plant;
import lib.PlantMap;
import lib.Selling;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class SellingDialog extends Dialog {

    protected boolean result = false;
    protected Shell shlSellingDialog;

    private Text txtPrice;
    private DateTime dateSellingDate;
    private Combo cmbPlant;

    private PlantMap plantMap;

    private String sellingPlant;
    private String sellingDate;
    private double sellingAmount;
    private double sellingPrice;
    private Label lblArAuga;
    private Text txtAmount;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public SellingDialog(Shell parent, int style, PlantMap plantMap) {
        super(parent, style);
        this.plantMap = plantMap;
        setText("SWT Dialog");
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public boolean open() {
        return open(null);
    }

    public boolean open(Selling selling) {
        createContents();

        for (String plantName : plantMap.keySet())
            cmbPlant.add(plantName);
        if (cmbPlant.getItemCount() == 0) {
            showError("Augalų sąrašas tuščias!");
            return false;
        } else
            cmbPlant.select(0);

        if (selling != null) {
            txtPrice.setText(Double.toString(selling.getPrice()));
            txtAmount.setText(Double.toString(selling.getAmount()));
        }
        
        updateIsGrowing();

        shlSellingDialog.pack();
        shlSellingDialog.layout();
        center();
        shlSellingDialog.open();
        Display display = getParent().getDisplay();
        while (!shlSellingDialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }
    
    private void updateIsGrowing() {
        boolean grows = false;
        
        try {
            int index = cmbPlant.getSelectionIndex();
            Plant plant = plantMap.get(cmbPlant.getItem(index));
            grows = plant.isGrowing(dateSellingDate.getMonth());
        } catch (Exception e) {
        }
        
        lblArAuga.setText(grows ? "Taip" : "Ne");
    }

    /**
     * Put the dialog in the center of its parent
     */
    private void center() {
        Shell parent = getParent();
        if (parent != null) {
            Rectangle bounds = parent.getBounds();
            Rectangle rect = shlSellingDialog.getBounds();

            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;
            shlSellingDialog.setLocation(x, y);
        }
    }
    
    private void showError(String message) {
        MessageBox messageBox = new MessageBox(getParent(), getStyle() | SWT.ERROR);
        messageBox.setText("Klaida");
        messageBox.setMessage(message);
        messageBox.open();
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlSellingDialog = new Shell(getParent(), getStyle());
        shlSellingDialog.setSize(305, 146);
        shlSellingDialog.setText("Pardavimas");
        shlSellingDialog.setLayout(new GridLayout(2, false));

        Label lblData = new Label(shlSellingDialog, SWT.NONE);
        lblData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblData.setText("Data:");

        dateSellingDate = new DateTime(shlSellingDialog, SWT.BORDER);
        dateSellingDate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateIsGrowing();
            }
        });
        dateSellingDate.setEnabled(true);
        GridData gd_dateSellingDate = new GridData(SWT.FILL, SWT.CENTER, false,
                false, 1, 1);
        gd_dateSellingDate.minimumWidth = 200;
        dateSellingDate.setLayoutData(gd_dateSellingDate);

        Label lblAugalas = new Label(shlSellingDialog, SWT.NONE);
        lblAugalas.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblAugalas.setText("Augalas:");

        cmbPlant = new Combo(shlSellingDialog, SWT.READ_ONLY);
        cmbPlant.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateIsGrowing();
            }
        });
        cmbPlant.setEnabled(true);
        GridData gd_cmbPlant = new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1);
        gd_cmbPlant.minimumWidth = 200;
        cmbPlant.setLayoutData(gd_cmbPlant);
        
        Label lblAuga = new Label(shlSellingDialog, SWT.NONE);
        lblAuga.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblAuga.setText("Auga:");
        
        lblArAuga = new Label(shlSellingDialog, SWT.NONE);
        lblArAuga.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        
        Label lblKiekis = new Label(shlSellingDialog, SWT.NONE);
        lblKiekis.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblKiekis.setText("Kiekis:");
        
        txtAmount = new Text(shlSellingDialog, SWT.BORDER);
        txtAmount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblKaina = new Label(shlSellingDialog, SWT.NONE);
        lblKaina.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblKaina.setText("Kaina:");

        txtPrice = new Text(shlSellingDialog, SWT.BORDER);
        txtPrice.setEnabled(true);
        GridData gd_textPrice = new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1);
        gd_textPrice.minimumWidth = 200;
        txtPrice.setLayoutData(gd_textPrice);
        new Label(shlSellingDialog, SWT.NONE);

        Composite composite = new Composite(shlSellingDialog, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
        fl_composite.spacing = 5;
        composite.setLayout(fl_composite);

        Button btnAtsaukti = new Button(composite, SWT.NONE);
        btnAtsaukti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = false;
                shlSellingDialog.close();
            }
        });
        btnAtsaukti.setText("Atšaukti");

        Button btnOK = new Button(composite, SWT.NONE);
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (cmbPlant.getSelectionIndex() < 0) {
                    cmbPlant.forceFocus();
                    return;
                }

                int index = cmbPlant.getSelectionIndex();
                Plant plant = plantMap.get(cmbPlant.getItem(index));
                if (plant != null) {
                    if (!plant.isGrowing(dateSellingDate.getMonth())) {
                        showError("Tą mėnesį pasirinktas augalas neauga.");
                        return;
                    }
                }
                
                try {
                    sellingAmount = Double.parseDouble(txtAmount.getText());
                } catch (Exception err) {
                    txtAmount.forceFocus();
                    return;
                }
                
                if (sellingAmount < 0) {
                    showError("Kiekis negali būti neigiamas!");
                    return;
                }

                try {
                    sellingPrice = Double.parseDouble(txtPrice.getText());
                } catch (Exception err) {
                    txtPrice.forceFocus();
                    return;
                }
                
                if (sellingPrice < 0) {
                    showError("Kaina negali būti neigiama!");
                    return;
                }

                sellingDate = String.format("%d-%02d-%02d",
                        dateSellingDate.getYear(),
                        dateSellingDate.getMonth() + 1,
                        dateSellingDate.getDay());
                sellingPlant = cmbPlant.getText();
                result = true;
                shlSellingDialog.close();
            }
        });
        btnOK.setText("OK");

    }

    public String getSellingDate() {
        return sellingDate;
    }

    public String getSellingPlant() {
        return sellingPlant;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public double getSellingAmount() {
        return sellingAmount;
    }
}
