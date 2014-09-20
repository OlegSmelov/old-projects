package gui;

import lib.Country;
import lib.CountryMap;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class SellerDialog extends Dialog {

    protected boolean result = false;
    protected Shell shlTiekejas;
    private Text txtPavadinimas;
    private Combo combo;

    private String sellerName;
    private String countryName;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public SellerDialog(Shell parent, int style) {
        super(parent, style);
        setText("SWT Dialog");
    }

    public boolean open(CountryMap countryMap) {
        return open(countryMap, "", null);
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public boolean open(CountryMap countryMap, String sellerName,
            String countryName) {
        createContents();

        txtPavadinimas.setText(sellerName);

        for (Country country : countryMap.values()) {
            if (country != null) {
                combo.add(country.getName());
                if (country.getName().equals(countryName))
                    combo.select(combo.getItemCount() - 1);
            }
        }

        if (combo.getItemCount() == 0) {
            MessageBox message = new MessageBox(getParent(), getStyle()
                    | SWT.ERROR);
            message.setText("Klaida");
            message.setMessage("Šalių sąrašas tuščias!");
            message.open();
            return false;
        }

        if (combo.getSelectionIndex() == -1)
            combo.select(0);

        shlTiekejas.pack();
        shlTiekejas.layout();
        center();
        shlTiekejas.open();
        Display display = getParent().getDisplay();
        while (!shlTiekejas.isDisposed()) {
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
            Rectangle rect = shlTiekejas.getBounds();

            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;
            shlTiekejas.setLocation(x, y);
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlTiekejas = new Shell(getParent(), getStyle());
        shlTiekejas.setSize(450, 131);
        shlTiekejas.setText("Tiekėjas");
        shlTiekejas.setLayout(new GridLayout(2, false));

        Label lblPavadinimas = new Label(shlTiekejas, SWT.NONE);
        lblPavadinimas.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblPavadinimas.setText("Pavadinimas:");

        txtPavadinimas = new Text(shlTiekejas, SWT.BORDER);
        GridData gd_txtPavadinimas = new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1);
        gd_txtPavadinimas.minimumWidth = 200;
        txtPavadinimas.setLayoutData(gd_txtPavadinimas);

        Label lblSalis = new Label(shlTiekejas, SWT.NONE);
        lblSalis.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblSalis.setText("Šalis:");

        combo = new Combo(shlTiekejas, SWT.READ_ONLY);
        GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
                1);
        gd_combo.minimumWidth = 200;
        combo.setLayoutData(gd_combo);

        Composite composite = new Composite(shlTiekejas, SWT.NONE);
        FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
        fl_composite.spacing = 5;
        composite.setLayout(fl_composite);
        composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 2, 1));

        Button btnAtaukti = new Button(composite, SWT.NONE);
        btnAtaukti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = false;
                shlTiekejas.close();
            }
        });
        btnAtaukti.setText("Atšaukti");

        Button btnOK = new Button(composite, SWT.NONE);
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (txtPavadinimas.getText().isEmpty()) {
                    txtPavadinimas.forceFocus();
                    return;
                }

                if (combo.getSelectionIndex() == -1) {
                    combo.forceFocus();
                    return;
                }

                sellerName = txtPavadinimas.getText();
                countryName = combo.getText();

                result = true;
                shlTiekejas.close();
            }
        });
        btnOK.setText("OK");

        shlTiekejas.setDefaultButton(btnOK);
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getCountryName() {
        return countryName;
    }
}
