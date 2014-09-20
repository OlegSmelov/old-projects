package lib;

import java.util.Iterator;

import org.eclipse.swt.widgets.Combo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Pagrindinė augalo klasė
 * 
 * Dėl mėnesių kuriais auga:
 * Jei from <= until, tai laikoma, kad tai tų pačių metų mėnesiai, priešingu atveju - skirtingų
 */
public class Plant {

    private String name;
    private String description;
    
    private int growsFrom;
    private int growsUntil;
    private CountryMap countryMap = new CountryMap();
    private Seller seller;

    public final String[] months = { "Sausis", "Vasaris", "Kovas", "Balandis",
            "Gegužė", "Birželis", "Liepa", "Rūgpjūtis", "Rugsėjis", "Spalis",
            "Lapkritis", "Gruodis" };

    public Plant(String name) {
        this.name = name;
        this.setGrowsFrom(0);
        this.setGrowsUntil(11);
    }

    public Plant(String name, int growsFrom, int growsUntil) {
        this.name = name;
        this.setGrowsFrom(growsFrom);
        this.setGrowsUntil(growsUntil);
    }

    public Plant(String name, String description, Seller seller, int growsFrom,
            int growsUntil) {
        this.name = name;
        this.description = description;
        this.setSeller(seller);
        this.setGrowsFrom(growsFrom);
        this.setGrowsUntil(growsUntil);
    }

    /**
     * Įrašo augalą į XML failą
     * 
     * @param doc - XML dokumentas
     */
    public void appendToXML(Document doc) {
        Element plantElement = doc.createElement("plant");
        plantElement.setAttribute("type", getType());
        plantElement.setAttribute("name", name);
        plantElement.setAttribute("growsFrom", Integer.toString(growsFrom));
        plantElement.setAttribute("growsUntil", Integer.toString(growsUntil));
        plantElement.setAttribute("description", description);
        if (seller != null)
            plantElement.setAttribute("seller", seller.getName());

        Iterator<Country> it = countryMap.values().iterator();
        while (it.hasNext()) {
            Country country = it.next();
            if (country != null) {
                Element countryElement = doc.createElement("plantCountry");
                countryElement.setAttribute("name", country.getName());
                plantElement.appendChild(countryElement);
            }
        }

        Element documentElement = doc.getDocumentElement();
        if (documentElement != null)
            documentElement.appendChild(plantElement);
    }

    /**
     * Nustato, ar augalas auga tam tikru mėnesiu
     * 
     * @param month - mėnuo (0 - 11)
     * @return boolean - ar auga
     */
    public boolean isGrowing(int month) {
        if (growsFrom <= growsUntil)
            return growsFrom <= month && month <= growsUntil;
        else
            return growsFrom <= month || month <= growsUntil;
    }

    public String getType() {
        return "Augalas";
    }

    public boolean isPoisonous() {
        return false;
    }

    public boolean isTree() {
        return false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGrowsFrom() {
        return growsFrom;
    }

    /**
     * @param growsFrom - mėnuo kurį pradeda augt (0-11)
     */
    public void setGrowsFrom(int growsFrom) {
        this.growsFrom = growsFrom;
    }

    public int getGrowsUntil() {
        return growsUntil;
    }

    /**
     * @param growsUntil - mėnuo kurį nustoja augt (0-11)
     */
    public void setGrowsUntil(int growsUntil) {
        this.growsUntil = growsUntil;
    }

    public CountryMap getCountryMap() {
        return countryMap;
    }

    public void setCountryMap(CountryMap countryMap) {
        this.countryMap = countryMap;
    }

    /**
     * Prideda šalį, kurioje gyvena augalas
     */
    public void addCountry(Country country) {
        countryMap.put(country);
    }

    @Override
    public String toString() {
        return name;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
    
    /**
     * Užpildo comboBox mėnesiais
     * 
     * @param cmb - ComboBox elementas, kurį užpildyti
     */
    public void fillMonthCombo(Combo cmb) {
        cmb.removeAll();
        for (String month : months)
            cmb.add(month);
    }

}
