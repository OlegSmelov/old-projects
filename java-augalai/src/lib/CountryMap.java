package lib;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Asociatyvus masyvas šalių pagal jų vardus
 */
@SuppressWarnings("serial")
public class CountryMap extends LinkedHashMap<String, Country> {

    public CountryMap() {
        super();
    }

    public void put(Country country) {
        put(country.getName(), country);
    }

    public void remove(Country country) {
        remove(country.getName());
    }
    
    /**
     * Išmeta ir vėl įdeda šalį
     * 
     * @param key - senas raktas (vardas)
     * @param country - šalis
     */
    public void change(String key, Country country) {
        remove(key);
        put(country);
    }

    /**
     * Sukuria PlantMap visų augalų augančių šalyje
     * 
     * @return augalų sąrašas 
     */
    public PlantMap getPlantMap() {
        PlantMap result = new PlantMap();
        Iterator<Country> countryItr = values().iterator();
        while (countryItr.hasNext()) {
            Iterator<Seller> sellerItr = countryItr.next().getSellerMap()
                    .values().iterator();
            while (sellerItr.hasNext()) {
                result.putAll(sellerItr.next().getPlantMap());
            }
        }
        return result;
    }
}
