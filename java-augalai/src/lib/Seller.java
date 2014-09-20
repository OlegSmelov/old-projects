package lib;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tiekėjo objektas
 */
public class Seller {
    private String name;
    private Country country;
    private SellingMap sellingMap;
    private PlantMap plantMap;

    public Seller(String name, Country country) {
        this.name = name;
        this.country = country;
        sellingMap = new SellingMap();
        plantMap = new PlantMap();
    }

    /**
     * Įrašo tiekėją į XML failą
     * 
     * @param doc - XML dokumentas
     */
    public void appendToXML(Document doc) {
        Element countryElement = doc.createElement("seller");
        countryElement.setAttribute("name", this.name);
        if (country != null)
            countryElement.setAttribute("country", country.getName());

        Element documentElement = doc.getDocumentElement();
        if (documentElement != null)
            documentElement.appendChild(countryElement);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setSellingMap(SellingMap sellingMap) {
        this.sellingMap = sellingMap;
    }

    public SellingMap getSellingMap() {
        return sellingMap;
    }

    public PlantMap getPlantMap() {
        return plantMap;
    }

    public void setPlantMap(PlantMap plantMap) {
        this.plantMap = plantMap;
    }

    /**
     * Ar tiekėjas yra tas pats tiekėjas kuris yra pateikiamas
     */
    public boolean equals(Seller seller) {
        if (this == seller) { // rodyklė į tą patį objektą
            return true;
        }
        return name.equals(seller.getName());
    }

    /**
     * Įdeda įrašą apie pardavimą
     * 
     * @param selling pardavimo objektas
     */
    public void addSelling(Selling selling) {
        sellingMap.put(selling);
    }

    /**
     * Suranda, kiek turi augalų duotu vardu
     * 
     * @param name augalo pavadinimas
     * @return integer turimų augalų skaičius
     */
    public Integer plantCount(String name) {
        Integer result = 0;
        Iterator<Plant> itr = plantMap.values().iterator();
        while (itr.hasNext()) {
            Plant plant = itr.next();
            String plantName = plant.getName();
            if (name.equals(plantName)) {
                result++;
            }
        }
        return result;
    }

    /**
     * @param pattern vardo dalis
     * @return boolean true jeigu tai ko ieskoma sis objektas tinka, false jei ne
     */
    public boolean matchPattern(String pattern) {
        if (pattern.contains(" ")) {
            return name.substring(0, pattern.length())
                    .equalsIgnoreCase(pattern);
        } else {
            String[] cmp = name.split(" ");
            for (int i = 0; i < cmp.length; i++) {
                if (name.substring(0, cmp[i].length())
                        .equalsIgnoreCase(pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Grąžina viso laikotarpio uždarbį
     */
    public Double getProfit() {
        return sellingMap.getProfit();
    }

    public String toString() {
        return name;
    }
}
