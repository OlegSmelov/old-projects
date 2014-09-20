package lib;

import lib.Seller;
import lib.Plant;

import org.eclipse.swt.widgets.Combo;

/**
 * Kuria augalus
 */
public class PlantManager {

    public final int typePlant = 0;
    public final int typeTree = 1;
    public final int typePoisonous = 2;

    /**
     * Sukuria naują augalo objektą pagal nurodytą tipo kodą
     * 
     * @param plantType - augalo tipo kodas
     * @param name - augalo vardas
     * @return naujas augalo objektas
     */
    public Plant createPlant(int plantType, String name) {
        switch (plantType) {
        case typePlant:
            return new Plant(name);
        case typeTree:
            return new Tree(name);
        case typePoisonous:
            return new PoisonousPlant(name);
        }
        return null;
    }
    
    /**
     * Sukuria naują augalo objektą pagal nurodytą tipo pavadinimą
     * 
     * @param plantType - augalo tipo pavadinimas
     * @param name - augalo vardas
     * @return naujas augalo objektas
     */
    public Plant createPlant(String plantType, String name) {
        return createPlant(getPlantType(plantType), name);
    }
    
    /**
     * Sukuria naują augalo objektą pagal nurodytą tipo kodą ir priskiria
     * perduotas reikšmes
     * 
     * @param plantType - tipo kodas
     * @param name - augalo pavadinimas
     * @param description - augalo aprašymas
     * @param seller - augalo tiekėjas
     * @param growsFrom - mėnuo kada pradeda augt (0-11)
     * @param growsUntil - mėnuo kada baigia augt (0-11)
     * @return naujas augalo objektas
     */
    public Plant createPlant(int plantType, String name, String description,
            Seller seller, int growsFrom, int growsUntil) {
        Plant plant = createPlant(plantType, name);
        plant.setDescription(description);
        plant.setSeller(seller);
        plant.setGrowsFrom(growsFrom);
        plant.setGrowsUntil(growsUntil);
        return plant;
    }

    /**
     * Užpildo comboBox augalų tipais
     * @param combo elementas
     */
    public void fillPlantCombo(Combo combo) {
        combo.removeAll();
        combo.add("Augalas", typePlant);
        combo.add("Medis", typeTree);
        combo.add("Nuodingas", typePoisonous);
    }

    /**
     * Grąžina augalo tipo kodą pagal tipo pavadinimą
     * @param type tipo pavadinimas
     * @return tipo kodas
     */
    public int getPlantType(String type) {
        if (type.equals("Augalas")) {
            return typePlant;
        } else if (type.equals("Medis")) {
            return typeTree;
        } else if (type.equals("Nuodingas")) {
            return typePoisonous;
        } else {
            return -1;
        }
    }
    
    /**
     * Grąžina augalo tipo kodą
     * @param p augalas
     * @return tipo kodas
     */
    public int getPlantType(Plant p) {
        return getPlantType(p.getType());
    }
}
