package gui;

import lib.PlantManager;
import lib.SellerMap;
import lib.Plant;
import lib.PoisonousPlant;
import lib.Seller;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class PlantDialog extends Dialog {

    protected boolean result = false;
    protected Shell shlPlantDialog;
    private Text txtPavadinimas;
    private Text txtDescription;
    private Text txtPoison;
    private Combo cmbType;
    private Combo cmbSeller;
    private Combo cmbGrowsUntil;
    private Combo cmbGrowsFrom;

    private PlantManager pM = new PlantManager();
    private int plantType = -1;
    private SellerMap sellerMap;
    private String plantName;
    private String plantDescription;
    private String sellerName;
    private String poison;
    private int plantGrowsFrom;
    private int plantGrowsUntil;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public PlantDialog(Shell parent, int style, SellerMap sellerMap) {
        super(parent, style);
        this.sellerMap = sellerMap;
        setText("SWT Dialog");
    }

    public boolean open() {
        return open(null, false);
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public boolean open(Plant plant, boolean disableTypes) {
        createContents();
        String plantName = "";
        String plantType = "";
        
        if (sellerMap == null || sellerMap.isEmpty()) {
            MessageBox messageBox = new MessageBox(shlPlantDialog, SWT.PRIMARY_MODAL | SWT.OK | SWT.ERROR);
            messageBox.setText("Klaida");
            messageBox.setMessage("Tiekėjų sąrašas tuščias!");
            messageBox.open();
            return false;
        }

        if (plant != null) {
            plantName = plant.getName();
            plantType = plant.getType();
            plantGrowsUntil = plant.getGrowsUntil();
            plantGrowsFrom = plant.getGrowsFrom();
            plantDescription = plant.getDescription();
            cmbGrowsFrom.select(plantGrowsFrom);
            cmbGrowsUntil.select(plantGrowsUntil);
            if (plantDescription != null) {
                txtDescription.setText(plantDescription);
            }
            if (plant.getSeller() != null) {
                String sellerName = plant.getSeller().getName();
                for (int i = 0; i < cmbSeller.getItemCount(); i++) {
                    if (cmbSeller.getItem(i).equals(sellerName)) {
                        cmbSeller.select(i);
                        break;
                    }
                }
            }
            txtPoison.setEnabled(plant.isPoisonous());
            
            if(plant.isPoisonous()) {
                txtPoison.setText(((PoisonousPlant) plant).getPoison());
            }
        }

        txtPavadinimas.setText(plantName);

        for (int i = 0; i < cmbType.getItemCount(); i++)
            if (cmbType.getItem(i).equals(plantType)) {
                cmbType.select(i);
                break;
            }
        cmbType.setEnabled(!disableTypes);

        shlPlantDialog.pack();
        shlPlantDialog.layout();
        center();
        shlPlantDialog.open();
        Display display = getParent().getDisplay();
        while (!shlPlantDialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    /**
     * Put the dialog in the center of its parent
     */
    private void center() {
        Shell parent = getParent();
        if (parent != null) {
            Rectangle bounds = parent.getBounds();
            Rectangle rect = shlPlantDialog.getBounds();

            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;
            shlPlantDialog.setLocation(x, y);
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlPlantDialog = new Shell(getParent(), getStyle());
        shlPlantDialog.setSize(364, 288);
        shlPlantDialog.setText("Augalas");
        shlPlantDialog.setLayout(new GridLayout(2, false));

        Label lblPavadinimas = new Label(shlPlantDialog, SWT.NONE);
        lblPavadinimas.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblPavadinimas.setText("Pavadinimas:");

        txtPavadinimas = new Text(shlPlantDialog, SWT.BORDER);
        GridData gd_txtPavadinimas = new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1);
        gd_txtPavadinimas.minimumWidth = 200;
        txtPavadinimas.setLayoutData(gd_txtPavadinimas);

        Label lblTipas = new Label(shlPlantDialog, SWT.NONE);
        lblTipas.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblTipas.setText("Tipas:");

        cmbType = new Combo(shlPlantDialog, SWT.READ_ONLY);
        cmbType.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (cmbType.getText().equals("Nuodingas")) {
                    txtPoison.setEnabled(true);
                } else {
                    txtPoison.setEnabled(false);
                }
            }
        });
        cmbType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        Label lblTiekejas = new Label(shlPlantDialog, SWT.NONE);
        lblTiekejas.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblTiekejas.setText("Tiekėjas:");

        cmbSeller = new Combo(shlPlantDialog, SWT.READ_ONLY);
        cmbSeller.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        Label lblGrowsFrom = new Label(shlPlantDialog, SWT.NONE);
        lblGrowsFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblGrowsFrom.setText("Auga nuo:");

        cmbGrowsFrom = new Combo(shlPlantDialog, SWT.READ_ONLY);
        cmbGrowsFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));

        Label lblGrowsUntil = new Label(shlPlantDialog, SWT.NONE);
        lblGrowsUntil.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblGrowsUntil.setText("Auga iki:");

        cmbGrowsUntil = new Combo(shlPlantDialog, SWT.READ_ONLY);
        cmbGrowsUntil.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        Label lblAprasymas = new Label(shlPlantDialog, SWT.NONE);
        lblAprasymas.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                true, 1, 1));
        lblAprasymas.setText("Aprašymas:");

        txtDescription = new Text(shlPlantDialog, SWT.BORDER | SWT.WRAP
                | SWT.V_SCROLL | SWT.MULTI);
        GridData gd_txtDescription = new GridData(SWT.FILL, SWT.FILL, true,
                true, 1, 1);
        gd_txtDescription.minimumHeight = 150;
        gd_txtDescription.minimumWidth = 250;
        gd_txtDescription.heightHint = 142;
        txtDescription.setLayoutData(gd_txtDescription);

        Label lblNewLabel = new Label(shlPlantDialog, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblNewLabel.setText("Nuodai:");

        txtPoison = new Text(shlPlantDialog, SWT.BORDER);
        txtPoison.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        Composite composite_1 = new Composite(shlPlantDialog, SWT.NONE);
        FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
        fl_composite_1.spacing = 5;
        composite_1.setLayout(fl_composite_1);
        composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 2, 1));

        Button btnAtsaukti = new Button(composite_1, SWT.NONE);
        btnAtsaukti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = false;
                shlPlantDialog.close();
            }
        });
        btnAtsaukti.setText("Atšaukti");

        Button btnOk = new Button(composite_1, SWT.NONE);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (txtPavadinimas.getText().isEmpty()) {
                    txtPavadinimas.forceFocus();
                    return;
                }
                
                poison = "";
                plantName = txtPavadinimas.getText();
                plantDescription = txtDescription.getText();
                plantGrowsFrom = cmbGrowsFrom.getSelectionIndex();
                plantGrowsUntil = cmbGrowsUntil.getSelectionIndex();

                if (cmbType.getSelectionIndex() >= 0) {
                    plantType = cmbType.getSelectionIndex();
                } else {
                    cmbType.forceFocus();
                    return;
                }

                if (cmbSeller.getSelectionIndex() >= 0) {
                    sellerName = cmbSeller.getText();
                } else {
                    cmbSeller.forceFocus();
                    return;
                }
                
                if (cmbType.getText().equals("Nuodingas")) {
                    poison = txtPoison.getText();
                }

                result = true;
                shlPlantDialog.close();
            }
        });
        btnOk.setText("OK");

        shlPlantDialog.setDefaultButton(btnOk);

        pM.fillPlantCombo(cmbType);
        cmbType.select(0); // default

        sellerMap.fillSellerCombo(cmbSeller);

        Plant p = new Plant("temp");
        p.fillMonthCombo(cmbGrowsFrom);
        p.fillMonthCombo(cmbGrowsUntil);

        cmbGrowsFrom.select(0);
        cmbGrowsUntil.select(11);

        cmbSeller.select(0);

    }

    public Plant getPlant() {
        Plant plant;
        PlantManager pM = new PlantManager();
        plant = pM.createPlant(plantType, plantName, plantDescription,
                getSeller(), plantGrowsFrom, plantGrowsUntil);
        if (plant.isPoisonous()) {
            PoisonousPlant poisonousPlant = (PoisonousPlant) plant;
            poisonousPlant.setPoison(poison);
            return poisonousPlant;
        }
        return plant;
    }

    /**
     * @return grąžina pasirinktą augalo tipą, arba -1 jei niekas nebuvo
     *         pasirinkta
     */
    public int getPlantType() {
        return plantType;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getPlantDescription() {
        return plantDescription;
    }

    public Seller getSeller() {
        return sellerMap.get(sellerName);
    }
    
    public String getPoison() {
        return poison;
    }
}
