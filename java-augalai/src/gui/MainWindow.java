package gui;

import java.util.Calendar;
import java.util.Scanner;

import lib.Country;
import lib.CountryMap;
import lib.FileManager;
import lib.Plant;
import lib.PlantMap;
import lib.Seller;
import lib.SellerMap;
import lib.Selling;
import lib.SellingMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.graphics.Point;

public class MainWindow {

    protected Shell shlAugalai;
    private Table tableCountries;
    private Table tableSellers;
    private Table tablePlants;
    private Table tableSellings;

    private Label lblMonthlyPlantsCount;
    private Label lblMontlyPlantsSold;
    private Label lblMonthlyProfit;
    private Label lblTotalPlants;
    private Label lblTotalPlantsSold;
    private Label lblTotalEarned;

    protected boolean modified = false;
    protected String filePath = null;
    protected int year, month;

    protected CountryMap countryMap = new CountryMap();
    protected SellerMap sellerMap = new SellerMap();
    protected PlantMap plantMap = new PlantMap();
    protected SellingMap sellingMap = new SellingMap();
    private Text txtData;
    private Group grpStatistika;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MainWindow window = new MainWindow();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //-------------------------------------------------------------------------
    
    private void addCountry(Country country) {
        TableItem item = new TableItem(tableCountries, 0);
        updateCountry(country, item);
    }
    
    private void updateCountry(Country country, TableItem item) {
        item.setText(country.getName());
        item.setData(country);
    }
    
    protected void populateCountryTable(CountryMap countryMap) {
        tableCountries.setRedraw(false);

        for (TableItem item : tableCountries.getItems())
            item.dispose();

        for (Country country : countryMap.values())
            if (country != null)
                addCountry(country);
        
        tableCountries.setRedraw(true);
    }
    
    //-------------------------------------------------------------------------
    
    private void addSeller(Seller seller) {
        TableItem item = new TableItem(tableSellers, 0);
        updateSeller(seller, item);
    }
    
    private void updateSeller(Seller seller, TableItem item) {
        item.setText(0, seller.getName());
        try {
            item.setText(1, seller.getCountry().getName());
        } catch (Exception e) {
            item.setText(1, "<sunaikinta>");
        }
        item.setData(seller);
    }
    
    private void updateSellers() {
        tableSellers.setRedraw(false);
        for (TableItem item : tableSellers.getItems()) {
            if (item.getData() instanceof Seller) {
                Seller seller = (Seller) item.getData();
                updateSeller(seller, item);
            }
        }
        tableSellers.setRedraw(true);
    }
    
    protected void populateSellersTable(SellerMap sellerMap) {
        tableSellers.setRedraw(false);

        for (TableItem item : tableSellers.getItems())
            item.dispose();

        for (Seller seller : sellerMap.values())
            if (seller != null)
                addSeller(seller);
        
        tableSellers.setRedraw(true);
    }
    
    //-------------------------------------------------------------------------

    private void addPlant(Plant plant) {
        TableItem item = new TableItem(tablePlants, 0);
        updatePlant(plant, item);
    }

    private void updatePlant(Plant plant, TableItem item) {
        item.setText(0, plant.getName());
        item.setText(1, plant.getType());
        item.setText(2, (plant.isGrowing(month)) ? "Taip" : "Ne");
        item.setData(plant);
    }

    /**
     * Atnaujina visą augalų (katalogo) lentelę
     */
    private void updatePlants() {
        tablePlants.setRedraw(false);
        for (TableItem item : tablePlants.getItems()) {
            if (item.getData() instanceof Plant) {
                Plant plant = (Plant) item.getData();
                updatePlant(plant, item);
            }
        }
        tablePlants.setRedraw(true);
    }
    
    protected void populatePlantTable(PlantMap plantMap) {
        tableSellers.setRedraw(false);

        for (TableItem item : tablePlants.getItems())
            item.dispose();

        for (Plant plant : plantMap.values())
            if (plant != null)
                addPlant(plant);
        
        tableSellers.setRedraw(true);
    }
    
    //-------------------------------------------------------------------------

    private void addSelling(Selling selling) {
        TableItem item = new TableItem(tableSellings, 0);
        updateSelling(selling, item);
    }

    private void updateSelling(Selling selling, TableItem item) {
        item.setText(0, selling.getDate());
        if (selling.getPlant() instanceof Plant)
            item.setText(1, selling.getPlant().getName());
        item.setText(2, Double.toString(selling.getAmount()));
        item.setText(3, Double.toString(selling.getPrice()));
        item.setText(4, Double.toString(selling.getAmount() * selling.getPrice()));
        item.setData(selling);
    }

    /**
     * Atnaujinti visą pardavimų sąrašą
     */
    private void updateSellings() {
        tableSellings.setRedraw(false);
        for (TableItem item : tableSellings.getItems()) {
            if (item.getData() instanceof Plant) {
                Selling selling = (Selling) item.getData();
                updateSelling(selling, item);
            }
        }
        tableSellings.setRedraw(true);
    }
    
    protected void populateSellingsTable(SellingMap sellingMap) {
        tableSellings.setRedraw(false);

        for (TableItem item : tableSellings.getItems())
            item.dispose();

        for (Selling selling : sellingMap.values())
            if (selling != null)
                addSelling(selling);
        
        tableSellings.setRedraw(true);
    }
    
    //-------------------------------------------------------------------------

    private void updateDate() {
        txtData.setText(String.format("%d-%02d", year, month + 1));
        updatePlants();
        updateStatistics();
    }

    private void parseDate() {
        try {
            Scanner scanner = new Scanner(txtData.getText());
            scanner.useDelimiter("-");
            int newYear = scanner.nextInt();
            int newMonth = scanner.nextInt() - 1;

            if (newMonth > 11)
                newMonth = 11;
            else if (newMonth < 0)
                newMonth = 0;

            year = newYear;
            month = newMonth;
        } catch (Exception e) {
            MessageBox message = new MessageBox(shlAugalai, SWT.PRIMARY_MODAL | SWT.OK | SWT.ERROR);
            message.setText("Klaida");
            message.setMessage("Neteisinga data");
            message.open();
        } finally {
            updateDate();
        }
    }
    
    private void nextDate() {
        month++;
        if (month > 11) {
            year++;
            month = 0;
        }
        updateDate();
    }

    private void prevDate() {
        month--;
        if (month < 0) {
            year--;
            month = 11;
        }
        updateDate();
    }
    
    //-------------------------------------------------------------------------
    
    private void updateStatistics() {
        lblMonthlyPlantsCount.setText(Integer.toString(plantMap.getPlantCount(month)));
        lblMonthlyProfit.setText(String.format("%.2f", sellingMap.getProfit(year, month)));
        lblMontlyPlantsSold.setText(Integer.toString(sellingMap.getPlantCount(year, month)));
        lblTotalEarned.setText(String.format("%.2f", sellingMap.getProfit()));
        lblTotalPlants.setText(Integer.toString(plantMap.size()));
        lblTotalPlantsSold.setText(Integer.toString(sellingMap.size()));
        grpStatistika.layout();
    }

    //-------------------------------------------------------------------------

    private void setModified(boolean modified) {
        this.modified = modified;

        String title = "";
        String separator = System.getProperty("file.separator");

        if (modified)
            title += "* ";

        if (filePath != null)
            title += filePath.substring(filePath.lastIndexOf(separator) + 1)
                    + " - ";

        title += "Augalai";

        shlAugalai.setText(title);
    }

    private boolean getModified() {
        return modified;
    }

    private int askToSave() {
        if (getModified()) {
            MessageBox message = new MessageBox(shlAugalai, SWT.PRIMARY_MODAL
                    | SWT.YES | SWT.NO | SWT.CANCEL);
            message.setText("Pakeitimai");
            message.setMessage("Ar norite išsaugoti pakeitimus?");

            int result = message.open();
            if (result == SWT.YES)
                saveFile(filePath);

            return result;
        }
        return 0;
    }
    
    private void showError(String message) {
        MessageBox messageBox = new MessageBox(shlAugalai, SWT.PRIMARY_MODAL | SWT.OK | SWT.ERROR);
        messageBox.setText("Klaida");
        messageBox.setMessage(message);
        messageBox.open();
    }

    /**
     * Returns filter path for file/open dialogs. Uses last file's path or
     * user's home folder when there is no file open.
     * 
     * @return String path
     */
    private String getFilterPath() {
        if (filePath != null) {
            String separator = System.getProperty("file.separator");
            return filePath.substring(0, filePath.lastIndexOf(separator));
        } else
            return System.getProperty("user.home");
    }

    private void saveFile(String filePath) {
        if (filePath == null) {
            FileDialog fileDialog = new FileDialog(shlAugalai, SWT.SAVE);
            fileDialog.setText("Išsaugoti");
            String[] filterExtensions = { "*.xml" };
            fileDialog.setFilterExtensions(filterExtensions);
            fileDialog.setFilterPath(getFilterPath());
            filePath = fileDialog.open();
        }

        if (filePath != null) {
            this.filePath = filePath;

            FileManager file = new FileManager(filePath);
            file.write(countryMap, sellerMap, plantMap, sellingMap);

            setModified(false);
        }
    }

    private void loadFile() {
        askToSave();
        FileDialog fileDialog = new FileDialog(shlAugalai, SWT.OPEN);
        fileDialog.setText("Atidaryti");
        String[] filterExtensions = { "*.xml" };
        fileDialog.setFilterExtensions(filterExtensions);
        fileDialog.setFilterPath(getFilterPath());
        String selected = fileDialog.open();
        if (selected != null) {
            filePath = selected;

            FileManager file = new FileManager(selected);
            file.read(countryMap, sellerMap, plantMap, sellingMap);
            populateCountryTable(countryMap);
            populateSellersTable(sellerMap);
            populatePlantTable(plantMap);
            populateSellingsTable(sellingMap);
            updateStatistics();

            setModified(false);
        }
    }

    /**
     * Center the window
     */
    private void center(Display display) {
        if (shlAugalai == null)
            return;
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shlAugalai.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shlAugalai.setLocation(x, y);
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();

        Calendar now = Calendar.getInstance();
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH);
        updateDate();

        center(display);
        shlAugalai.open();
        shlAugalai.layout();
        while (!shlAugalai.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shlAugalai = new Shell();
        shlAugalai.setMinimumSize(new Point(500, 350));
        shlAugalai.setImage(SWTResourceManager.getImage(MainWindow.class,
                "/resources/flower_icon.png"));
        shlAugalai.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                e.doit = (askToSave() != SWT.CANCEL);
            }
        });
        shlAugalai.setSize(696, 428);
        GridLayout gl_shlAugalai = new GridLayout(1, false);
        shlAugalai.setLayout(gl_shlAugalai);
        setModified(false);

        Menu menu = new Menu(shlAugalai, SWT.BAR);
        shlAugalai.setMenuBar(menu);

        MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
        mntmNewSubmenu.setText("&Failas");

        Menu menuFailas = new Menu(mntmNewSubmenu);
        mntmNewSubmenu.setMenu(menuFailas);

        MenuItem mntmAtidaryti = new MenuItem(menuFailas, SWT.NONE);
        mntmAtidaryti.setImage(SWTResourceManager.getImage(MainWindow.class,
                "/resources/open_icon.png"));
        mntmAtidaryti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadFile();
            }
        });
        mntmAtidaryti.setText("&Atidaryti");

        MenuItem mntmIssaugoti = new MenuItem(menuFailas, SWT.NONE);
        mntmIssaugoti.setImage(SWTResourceManager.getImage(MainWindow.class,
                "/resources/save_icon.png"));
        mntmIssaugoti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFile(filePath);
            }
        });
        mntmIssaugoti.setText("Iš&saugoti");

        MenuItem mntmIssaugotiKaip = new MenuItem(menuFailas, SWT.NONE);
        mntmIssaugotiKaip.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFile(null);
            }
        });
        mntmIssaugotiKaip.setText("&Išsaugoti kaip...");

        MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
        mntmHelp.setText("&Pagalba");

        Menu menu_2 = new Menu(mntmHelp);
        mntmHelp.setMenu(menu_2);

        MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
        mntmAbout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AboutDialog dialog = new AboutDialog(shlAugalai,
                        SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
                dialog.open();
            }
        });
        mntmAbout.setText("Apie");

        Composite composite_6 = new Composite(shlAugalai, SWT.NONE);
        composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
                false, 1, 1));
        GridLayout gl_composite_6 = new GridLayout(4, false);
        gl_composite_6.marginWidth = 0;
        gl_composite_6.marginHeight = 0;
        composite_6.setLayout(gl_composite_6);

        ToolBar toolBar_1 = new ToolBar(composite_6, SWT.FLAT | SWT.RIGHT);
        toolBar_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
                1, 1));

        ToolItem toolItem = new ToolItem(toolBar_1, SWT.NONE);
        toolItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadFile();
            }
        });
        toolItem.setImage(SWTResourceManager.getImage(MainWindow.class,
                "/resources/open_icon.png"));

        ToolItem toolItem_1 = new ToolItem(toolBar_1, SWT.NONE);
        toolItem_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFile(filePath);
            }
        });
        toolItem_1.setImage(SWTResourceManager.getImage(MainWindow.class,
                "/resources/save_icon.png"));

        ToolItem toolItem_2 = new ToolItem(toolBar_1, SWT.NONE);
        toolItem_2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFile(null);
            }
        });
        toolItem_2.setText("&Išsaugoti kaip...");

        Label lblData = new Label(composite_6, SWT.NONE);
        lblData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
                1, 1));
        lblData.setText("Data:");

        txtData = new Text(composite_6, SWT.BORDER);
        txtData.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                parseDate();
            }
        });
        txtData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        Composite composite_7 = new Composite(composite_6, SWT.NONE);
        RowLayout rl_composite_7 = new RowLayout(SWT.HORIZONTAL);
        rl_composite_7.spacing = 0;
        rl_composite_7.marginTop = 0;
        rl_composite_7.marginRight = 0;
        rl_composite_7.marginLeft = 0;
        rl_composite_7.marginBottom = 0;
        composite_7.setLayout(rl_composite_7);

        Button btnPrevMonth = new Button(composite_7, SWT.NONE);
        btnPrevMonth.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevDate();
            }
        });
        btnPrevMonth.setText("<");

        Button btnNextMonth = new Button(composite_7, SWT.NONE);
        btnNextMonth.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextDate();
            }
        });
        btnNextMonth.setText(">");

        TabFolder tabFolder = new TabFolder(shlAugalai, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                2));

        TabItem tbtmSalys = new TabItem(tabFolder, SWT.NONE);
        tbtmSalys.setText("Šalys");

        Composite composite = new Composite(tabFolder, SWT.NONE);
        tbtmSalys.setControl(composite);
        composite.setLayout(new GridLayout(2, false));

        tableCountries = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.MULTI);
        tableCountries.setLinesVisible(true);
        tableCountries.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                true, 1, 1));
        tableCountries.setHeaderVisible(true);

        TableColumn tblclmnPavadinimas = new TableColumn(tableCountries,
                SWT.NONE);
        tblclmnPavadinimas.setWidth(100);
        tblclmnPavadinimas.setText("Pavadinimas");

        Composite composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
                1, 1));
        FillLayout fl_composite_1 = new FillLayout(SWT.VERTICAL);
        fl_composite_1.spacing = 5;
        composite_1.setLayout(fl_composite_1);

        Button btnPrideti = new Button(composite_1, SWT.NONE);
        btnPrideti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CountryDialog dialog = new CountryDialog(shlAugalai,
                        SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
                if (dialog.open()) {
                    String name = dialog.getName();
                    if (countryMap.get(name) != null)
                        showError("Šalis su tokiu pavadinimu jau yra!");
                    else {
                        Country country = new Country(name);
                        addCountry(country);

                        countryMap.put(country);
                        setModified(true);
                    }
                }
            }
        });
        btnPrideti.setText("&Pridėti");

        Button btnRedaguoti = new Button(composite_1, SWT.NONE);
        btnRedaguoti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = tableCountries.getSelection();
                if (selectedItems.length > 0) {
                    TableItem firstItem = selectedItems[0];
                    Country country = (Country) firstItem.getData();
                    if (country != null) {
                        CountryDialog dialog = new CountryDialog(shlAugalai,
                                SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
                        if (dialog.open(country.getName())) {
                            String newName = dialog.getName();
                            
                            if (!newName.equals(country.getName()) &&
                                    countryMap.get(newName) != null)
                                showError("Šalis su tokiu pavadinimu jau yra!");
                            else {
                                countryMap.remove(country);
                                country.setName(newName);
                                countryMap.put(country);
                                updateCountry(country, firstItem);

                                updateSellers();
                                setModified(true);
                            }
                        }
                    }
                }
            }
        });
        btnRedaguoti.setText("&Redaguoti");

        Button btnNaikinti = new Button(composite_1, SWT.NONE);
        btnNaikinti.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = tableCountries.getSelection();
                if (selectedItems.length > 0) {

                    MessageBox message = new MessageBox(shlAugalai,
                            SWT.PRIMARY_MODAL | SWT.YES | SWT.NO);
                    message.setText("Ar tikrai norite?");
                    message.setMessage("Ar tikrai norite panaikinti?");
                    if (message.open() == SWT.NO)
                        return;

                    tableCountries.setRedraw(false);
                    for (TableItem item : selectedItems) {
                        Country country = (Country) item.getData();
                        SellerMap list = country.getSellerMap();

                        if (list != null)
                            for (Seller seller : list.values())
                                seller.setCountry(null);

                        if (country != null)
                            countryMap.remove(country);
                        item.dispose();

                        setModified(true);
                    }

                    updateSellers();
                    tableCountries.setRedraw(true);
                }
            }
        });
        btnNaikinti.setText("&Naikinti");

        TabItem tbtmTiekejai = new TabItem(tabFolder, SWT.NONE);
        tbtmTiekejai.setText("Tiekėjai");

        Composite composite_3 = new Composite(tabFolder, SWT.NONE);
        tbtmTiekejai.setControl(composite_3);
        composite_3.setLayout(new GridLayout(2, false));

        tableSellers = new Table(composite_3, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.MULTI);
        tableSellers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                1, 1));
        tableSellers.setHeaderVisible(true);
        tableSellers.setLinesVisible(true);

        TableColumn tblclmnPavadinimas_1 = new TableColumn(tableSellers,
                SWT.NONE);
        tblclmnPavadinimas_1.setWidth(223);
        tblclmnPavadinimas_1.setText("Pavadinimas");

        TableColumn tblclmnalis = new TableColumn(tableSellers, SWT.NONE);
        tblclmnalis.setWidth(100);
        tblclmnalis.setText("Šalis");

        Composite composite_4 = new Composite(composite_3, SWT.NONE);
        FillLayout fl_composite_4 = new FillLayout(SWT.VERTICAL);
        fl_composite_4.spacing = 5;
        composite_4.setLayout(fl_composite_4);
        composite_4.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
                1, 1));

        Button button = new Button(composite_4, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SellerDialog dialog = new SellerDialog(shlAugalai,
                        SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
                if (dialog.open(countryMap)) {
                    
                    Country country = countryMap.get(dialog.getCountryName());
                    String name = dialog.getSellerName();
                    
                    if (sellerMap.get(name) != null)
                        showError("Tiekėjas su tokiu pavadinimu jau yra!");
                    else {
                        Seller seller = new Seller(name, country);
                        country.addSeller(seller);

                        addSeller(seller);

                        sellerMap.put(seller);
                        setModified(true);
                    }
                }
            }
        });
        button.setText("&Pridėti");

        Button button_1 = new Button(composite_4, SWT.NONE);
        button_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = tableSellers.getSelection();
                if (selectedItems.length > 0) {
                    TableItem item = selectedItems[0];
                    SellerDialog dialog = new SellerDialog(shlAugalai,
                            SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
                    Seller seller = (Seller) item.getData();
                    Country country = seller.getCountry();
                    String countryName = (country != null) ? country.getName()
                            : null;

                    if (dialog.open(countryMap, item.getText(), countryName)) {
                        Country newCountry = countryMap.get(dialog.getCountryName());

                        if (newCountry != null) {
                            if (newCountry != country) {
                                if (country != null)
                                    country.removeSeller(seller);
                                newCountry.addSeller(seller);
                            }
                            
                            String newName = dialog.getSellerName();
                            
                            if (!newName.equals(seller.getName())
                                    && sellerMap.get(newName) != null)
                                showError("Tiekėjas su tokiu pavadinimu jau yra!");
                            else {
                                sellerMap.remove(seller);
                                seller.setName(newName);
                                sellerMap.put(seller);
                                seller.setCountry(newCountry);
                                
                                updateSeller(seller, item);
    
                                setModified(true);
                            }
                        }
                    }
                }
            }
        });
        button_1.setText("&Redaguoti");

        Button button_2 = new Button(composite_4, SWT.NONE);
        button_2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = tableSellers.getSelection();
                if (selectedItems.length > 0) {

                    MessageBox message = new MessageBox(shlAugalai,
                            SWT.PRIMARY_MODAL | SWT.YES | SWT.NO);
                    message.setText("Ar tikrai norite?");
                    message.setMessage("Ar tikrai norite panaikinti?");
                    if (message.open() == SWT.NO)
                        return;

                    tableSellers.setRedraw(false);
                    for (TableItem item : selectedItems) {
                        Seller seller = (Seller) item.getData();
                        Country country = seller.getCountry();
                        if (seller != null) {
                            if (country != null)
                                country.removeSeller(seller);
                            sellerMap.remove(seller);
                        }
                        item.dispose();

                        setModified(true);
                    }
                    tableSellers.setRedraw(true);
                }
            }
        });
        button_2.setText("&Naikinti");

        TabItem tbtmKatalogas = new TabItem(tabFolder, SWT.NONE);
        tbtmKatalogas.setText("Katalogas");

        Composite composite_2 = new Composite(tabFolder, SWT.NONE);
        tbtmKatalogas.setControl(composite_2);
        composite_2.setLayout(new GridLayout(2, false));

        tablePlants = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.MULTI);
        tablePlants.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                1, 1));
        tablePlants.setHeaderVisible(true);
        tablePlants.setLinesVisible(true);

        TableColumn tblclmnPavadinimas_2 = new TableColumn(tablePlants,
                SWT.NONE);
        tblclmnPavadinimas_2.setWidth(150);
        tblclmnPavadinimas_2.setText("Pavadinimas");

        TableColumn tblclmnRusis = new TableColumn(tablePlants, SWT.NONE);
        tblclmnRusis.setWidth(115);
        tblclmnRusis.setText("Rūšis");

        TableColumn tblclmnGrows = new TableColumn(tablePlants, SWT.NONE);
        tblclmnGrows.setWidth(159);
        tblclmnGrows.setText("Auga");

        Composite composite_5 = new Composite(composite_2, SWT.NONE);
        composite_5.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
                1, 1));
        FillLayout fl_composite_5 = new FillLayout(SWT.VERTICAL);
        fl_composite_5.spacing = 5;
        composite_5.setLayout(fl_composite_5);

        Button btnPridetiAugala = new Button(composite_5, SWT.NONE);
        btnPridetiAugala.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PlantDialog dialog = new PlantDialog(shlAugalai,
                        SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL, sellerMap);
                if (dialog.open()) {
                    Plant plant = dialog.getPlant();
                    
                    if (plantMap.get(plant.getName()) != null)
                        showError("Augalas su tokiu pavadinimu jau yra!");
                    else {
                        plantMap.put(plant);
                        addPlant(plant);
                        updateStatistics();
                        setModified(true);
                    }
                }
            }
        });
        btnPridetiAugala.setText("Pridėti");

        Button btnRedaguotiAugala = new Button(composite_5, SWT.NONE);
        btnRedaguotiAugala.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] items = tablePlants.getSelection();
                if (items.length > 0) {
                    TableItem item = items[0];
                    Plant plant = (Plant) item.getData();

                    if (plant != null) {
                        PlantDialog dialog = new PlantDialog(shlAugalai,
                                SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL, sellerMap);

                        if (dialog.open(plant, true)) {
                            String oldName = plant.getName();
                            plant = dialog.getPlant();
                            
                            if (!oldName.equals(plant.getName())
                                    && plantMap.get(plant.getName()) != null)
                                showError("Augalas su tokiu pavadinimu jau yra!");
                            else {
                                plantMap.change(oldName, plant);
                                updatePlant(plant, item);
                                updateStatistics();
                                setModified(true);
                            }
                        }
                    }
                }
            }
        });
        btnRedaguotiAugala.setText("Redaguoti");

        Button btnNaikintiaugala = new Button(composite_5, SWT.NONE);
        btnNaikintiaugala.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = tablePlants.getSelection();
                if (selectedItems.length > 0) {

                    MessageBox message = new MessageBox(shlAugalai,
                            SWT.PRIMARY_MODAL | SWT.YES | SWT.NO);
                    message.setText("Ar tikrai norite?");
                    message.setMessage("Ar tikrai norite panaikinti?");
                    if (message.open() == SWT.NO)
                        return;

                    tablePlants.setRedraw(false);
                    for (TableItem item : selectedItems) {
                        Plant plant = (Plant) item.getData();
                        if (plant != null)
                            plantMap.remove(plant);
                        item.dispose();

                        setModified(true);
                    }
                    tablePlants.setRedraw(true);
                    updateStatistics();
                }
            }
        });
        btnNaikintiaugala.setText("Naikinti");

        TabItem tbtmPardavimai = new TabItem(tabFolder, SWT.NONE);
        tbtmPardavimai.setText("Pardavimai");

        Composite composite_8 = new Composite(tabFolder, SWT.NONE);
        tbtmPardavimai.setControl(composite_8);
        composite_8.setLayout(new GridLayout(2, false));

        tableSellings = new Table(composite_8, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        GridData gd_tableSellings = new GridData(SWT.FILL, SWT.FILL, true,
                true, 1, 1);
        gd_tableSellings.widthHint = 411;
        gd_tableSellings.heightHint = 303;
        tableSellings.setLayoutData(gd_tableSellings);
        tableSellings.setHeaderVisible(true);
        tableSellings.setLinesVisible(true);

        TableColumn tblclmnSellingDate = new TableColumn(tableSellings,
                SWT.NONE);
        tblclmnSellingDate.setWidth(100);
        tblclmnSellingDate.setText("Data");

        TableColumn tblclmnSellingPlant = new TableColumn(tableSellings,
                SWT.NONE);
        tblclmnSellingPlant.setWidth(142);
        tblclmnSellingPlant.setText("Parduotas augalas");
        
        TableColumn tblclmnKiekis = new TableColumn(tableSellings, SWT.NONE);
        tblclmnKiekis.setWidth(77);
        tblclmnKiekis.setText("Kiekis");

        TableColumn tblclmnSellingProfit = new TableColumn(tableSellings,
                SWT.NONE);
        tblclmnSellingProfit.setWidth(93);
        tblclmnSellingProfit.setText("Kaina");
        
        TableColumn tblclmnSuma = new TableColumn(tableSellings, SWT.NONE);
        tblclmnSuma.setWidth(100);
        tblclmnSuma.setText("Suma");

        Composite composite_9 = new Composite(composite_8, SWT.NONE);
        FillLayout fl_composite_9 = new FillLayout(SWT.VERTICAL);
        fl_composite_9.spacing = 5;
        composite_9.setLayout(fl_composite_9);
        composite_9.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true,
                1, 1));

        Button btnAddSelling = new Button(composite_9, SWT.NONE);
        btnAddSelling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SellingDialog dialog = new SellingDialog(shlAugalai,
                        SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM, plantMap);
                if (dialog.open()) {
                    Plant plant = plantMap.get(dialog.getSellingPlant());
                    String date = dialog.getSellingDate();
                    double amount = dialog.getSellingAmount();
                    double price = dialog.getSellingPrice();
                    Seller seller = plant.getSeller();

                    Selling selling = sellingMap.createSelling(plant, date,
                            amount, price, seller);
                    sellingMap.put(selling);

                    addSelling(selling);

                    updateStatistics();
                    setModified(true);
                }
            }
        });
        btnAddSelling.setText("Pridėti");

        Button btnRemoveSelling = new Button(composite_9, SWT.NONE);
        btnRemoveSelling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = tableSellings.getSelection();
                if (selectedItems.length > 0) {

                    MessageBox message = new MessageBox(shlAugalai,
                            SWT.PRIMARY_MODAL | SWT.YES | SWT.NO);
                    message.setText("Ar tikrai norite?");
                    message.setMessage("Ar tikrai norite panaikinti?");
                    if (message.open() == SWT.NO)
                        return;

                    tableCountries.setRedraw(false);
                    for (TableItem item : selectedItems) {
                        Selling selling = (Selling) item.getData();

                        if (selling != null)
                            sellingMap.remove(selling);
                        item.dispose();

                        setModified(true);
                    }

                    updateSellings();
                    updateStatistics();
                    tableCountries.setRedraw(true);
                }
            }
        });
        btnRemoveSelling.setText("Anuliuoti");

        grpStatistika = new Group(shlAugalai, SWT.NONE);
        grpStatistika.setLayout(new FillLayout(SWT.HORIZONTAL));
        grpStatistika.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
                false, 1, 1));
        grpStatistika.setText("Statistika");

        Composite composite_10 = new Composite(grpStatistika, SWT.NONE);
        composite_10.setLayout(new GridLayout(2, false));

        Label lblNewLabel = new Label(composite_10, SWT.NONE);
        lblNewLabel.setText("Augančių augalų šį mėnesį:");

        lblMonthlyPlantsCount = new Label(composite_10, SWT.NONE);
        lblMonthlyPlantsCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        lblMonthlyPlantsCount.setText("0");

        Label lblNewLabel_1 = new Label(composite_10, SWT.NONE);
        lblNewLabel_1.setText("Parduotų augalų šį mėnesį:");

        lblMontlyPlantsSold = new Label(composite_10, SWT.NONE);
        lblMontlyPlantsSold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        lblMontlyPlantsSold.setText("0");

        Label lblNewLabel_2 = new Label(composite_10, SWT.NONE);
        lblNewLabel_2.setText("Uždirbta pinigų šį mėnesį:");

        lblMonthlyProfit = new Label(composite_10, SWT.NONE);
        lblMonthlyProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        lblMonthlyProfit.setText("0");

        Composite composite_11 = new Composite(grpStatistika, SWT.NONE);
        composite_11.setLayout(new GridLayout(2, false));

        Label lblNewLabel_3 = new Label(composite_11, SWT.NONE);
        lblNewLabel_3.setText("Iš viso augalų:");

        lblTotalPlants = new Label(composite_11, SWT.NONE);
        lblTotalPlants.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        lblTotalPlants.setText("0");

        Label lblNewLabel_4 = new Label(composite_11, SWT.NONE);
        lblNewLabel_4.setText("Iš viso parduota augalų:");

        lblTotalPlantsSold = new Label(composite_11, SWT.NONE);
        lblTotalPlantsSold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        lblTotalPlantsSold.setText("0");

        Label lblNewLabel_5 = new Label(composite_11, SWT.NONE);
        lblNewLabel_5.setText("Iš viso uždirbta:");

        lblTotalEarned = new Label(composite_11, SWT.NONE);
        lblTotalEarned.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        lblTotalEarned.setText("0.0");
    }
}
