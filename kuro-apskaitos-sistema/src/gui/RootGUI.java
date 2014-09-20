package gui;

import javax.swing.UIManager;

public class RootGUI {

    public static void main(String[] args) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Kuro apskaitos valdymo sistema");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (Exception e) {

        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DatabaseLoginForm().setVisible(true);
            }
        });
    }
}
