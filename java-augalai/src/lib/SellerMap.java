package lib;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.eclipse.swt.widgets.Combo;

/**
 * Asociatyvus masyvas tiekėjų pagal jų vardus
 */
@SuppressWarnings("serial")
public class SellerMap extends LinkedHashMap<String, Seller> {

    public SellerMap() {
        super();
    }

    public void put(Seller seller) {
        put(seller.getName(), seller);
    }

    public void remove(Seller seller) {
        remove(seller.getName());
    }

    public void change(String key, Seller seller) {
        remove(key);
        put(seller);
    }

    /**
     * Suranda visus tiekėjus, kurių vardai prasideda duota eilute
     * 
     * @param pattern dalis tiekėjo vardo
     * @return tiekėjų mapas
     */
    public SellerMap findSellers(String pattern) {
        Iterator<Seller> itr = values().iterator();
        SellerMap result = new SellerMap();
        while (itr.hasNext()) {
            Seller seller = itr.next();
            if (seller.matchPattern(pattern)) {
                result.put(seller);
            }
        }
        return result;
    }

    public Double getProfit() {
        Iterator<Seller> itr = values().iterator();
        Double result = 0.0;
        while (itr.hasNext()) {
            result += itr.next().getProfit();
        }
        return result;
    }

    /**
     * Grąžina visus pirkimus iš esamo sąraso tiekėjų
     * 
     * @return pirkimų mapas
     */
    public SellingMap getSellingMap() {
        SellingMap result = new SellingMap();
        Iterator<Seller> itr = values().iterator();
        while (itr.hasNext()) {
            result.putAll(itr.next().getSellingMap());
        }
        return result;
    }

    /**
     * Visi augalai iš esamo sąraso tiekėjų
     * 
     * @return augalų mapas
     */

    public PlantMap getPlantMap() {
        PlantMap result = new PlantMap();
        Iterator<Seller> itr = values().iterator();
        while (itr.hasNext()) {
            result.putAll(itr.next().getPlantMap());
        }
        return result;
    }

    /**
     * Užpildo comboBox tiekėjais
     * 
     * @param cmb elementas, kurį pildyti
     */
    public void fillSellerCombo(Combo cmb) {
        cmb.removeAll();
        Iterator<Seller> itr = values().iterator();
        while (itr.hasNext()) {
            cmb.add(itr.next().getName());
        }
    }
}
