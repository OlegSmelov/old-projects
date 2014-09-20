package lib;

import java.util.LinkedHashMap;

/**
 * Asociatyvus masyvas augalų pagal jų vardus
 */
@SuppressWarnings("serial")
public class PlantMap extends LinkedHashMap<String, Plant> {

    public PlantMap() {
        super();
    }

    public void put(Plant plant) {
        put(plant.getName(), plant);
    }

    public void remove(Plant plant) {
        remove(plant.getName());
    }

    /**
     * Išmeta ir vėl įdeda augalą
     * 
     * @param key - senas raktas (vardas)
     * @param plant - augalas
     */
    public void change(String key, Plant plant) {
        if (!plant.getName().equals(key)) {
            remove(key);
            put(plant);
        } else {
            put(plant);
        }
    }

    public PlantMap filterMonths(int growsFrom, int growsUntil) {
        PlantMap result = new PlantMap();
        for (Plant plant : values()) {
            if ((plant.getGrowsFrom() <= growsFrom)
                    && (plant.getGrowsUntil() >= growsUntil)) {
                result.put(plant);
            }
        }
        return result;
    }

    /**
     * Kiek augalų auga mėnesyje
     * 
     * @param menesis mėnuo (0-11)
     * @return augalų skaicius
     */
    public int getPlantCount(int month) {
        int numPlants = 0;
        for (Plant plant : values())
            if (plant.isGrowing(month))
                numPlants++;
        return numPlants;
    }

}
