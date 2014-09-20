package gui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;

public class AboutDialog extends Dialog {

    protected Object result;
    protected Shell shlApie;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public AboutDialog(Shell parent, int style) {
        super(parent, style);
        setText("SWT Dialog");
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public Object open() {
        createContents();
        shlApie.pack();
        shlApie.layout();
        center();
        shlApie.open();
        Display display = getParent().getDisplay();
        while (!shlApie.isDisposed()) {
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
            Rectangle rect = shlApie.getBounds();

            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;
            shlApie.setLocation(x, y);
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlApie = new Shell(getParent(), SWT.DIALOG_TRIM);
        shlApie.setImage(SWTResourceManager.getImage(AboutDialog.class,
                "/resources/flower_icon.png"));
        shlApie.setSize(384, 105);
        shlApie.setText("Apie");
        shlApie.setLayout(new GridLayout(1, false));

        Label lblAbout = new Label(shlApie, SWT.WRAP);
        lblAbout.setText("Informacinės technologijos, 1 grupė\n\nOleg Smelov\nAndrius Janauskas\n\nVilniaus Universitetas,\nMatematikos ir informatikos fakultetas");

    }
}
