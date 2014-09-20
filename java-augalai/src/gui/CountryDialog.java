package gui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class CountryDialog extends Dialog {

    protected boolean result;
    protected Shell shlSalis;
    private Text txtName;
    private String name;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public CountryDialog(Shell parent, int style) {
        super(parent, style);
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

    public boolean open(String defaultName) {
        result = false;

        createContents();

        if (defaultName != null) {
            txtName.setText(defaultName);
            txtName.setSelection(0, defaultName.length());
        }

        shlSalis.pack();
        shlSalis.layout();
        center();
        shlSalis.open();
        Display display = getParent().getDisplay();
        while (!shlSalis.isDisposed()) {
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
            Rectangle rect = shlSalis.getBounds();

            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;
            shlSalis.setLocation(x, y);
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlSalis = new Shell(getParent(), getStyle());
        shlSalis.setSize(389, 100);
        shlSalis.setText("Šalis");
        shlSalis.setLayout(new GridLayout(2, false));

        Label lblName = new Label(shlSalis, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
                1, 1));
        lblName.setText("Pavadinimas:");

        txtName = new Text(shlSalis, SWT.BORDER);
        GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1);
        gd_txtName.minimumWidth = 200;
        txtName.setLayoutData(gd_txtName);

        Composite composite = new Composite(shlSalis, SWT.NONE);
        FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
        fl_composite.spacing = 5;
        composite.setLayout(fl_composite);
        composite.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false,
                true, 2, 1));

        Button btnAtsaukti = new Button(composite, SWT.NONE);
        btnAtsaukti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shlSalis.close();
            }
        });
        btnAtsaukti.setText("&Atšaukti");

        Button btnOk = new Button(composite, SWT.NONE);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (txtName.getText().isEmpty()) {
                    txtName.forceFocus();
                    return;
                }

                result = true;
                name = txtName.getText();
                shlSalis.close();
            }
        });
        btnOk.setText("&OK");

        shlSalis.setDefaultButton(btnOk);
    }

    public String getName() {
        return name;
    }
}
