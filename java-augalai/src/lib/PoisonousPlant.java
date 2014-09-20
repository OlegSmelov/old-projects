package lib;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Nuodingas augalas
 */
public class PoisonousPlant extends Plant {

    private String poison;

    public PoisonousPlant(String name) {
        super(name);
    }

    public PoisonousPlant(String name, String poison) {
        super(name);
        this.poison = poison;
    }
    
    public String getPoison() {
        return poison;
    }

    public void setPoison(String poison) {
        this.poison = poison;
    }

    /**
     * Įrašo augalą į XML failą
     * 
     * Metodas perrašomas, nes nuodingas augalas turi papildomų laukų
     * 
     * @param doc - XML dokumentas
     */
    @Override
    public void appendToXML(Document doc) {
        Element plantElement = doc.createElement("plant");
        plantElement.setAttribute("type", getType());
        plantElement.setAttribute("name", getName());
        plantElement.setAttribute("growsFrom", Integer.toString(getGrowsFrom()));
        plantElement.setAttribute("growsUntil", Integer.toString(getGrowsUntil()));
        plantElement.setAttribute("description", getDescription());
        plantElement.setAttribute("poison", poison);
        if (getSeller() != null)
            plantElement.setAttribute("seller", getSeller().getName());

        Iterator<Country> it = getCountryMap().values().iterator();
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
    
    @Override
    public boolean isPoisonous() {
        return true;
    }

    @Override
    public String getType() {
        return "Nuodingas";
    }
}
