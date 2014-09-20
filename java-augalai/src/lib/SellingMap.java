package lib;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Asociatyvus masyvas pardavimų pagal jų ID
 */
@SuppressWarnings("serial")
public class SellingMap extends LinkedHashMap<Integer, Selling> {
    private int lastId;

    public SellingMap() {
        super();
        this.lastId = 0;
    }

    public SellingMap(int lastId) {
        super();
        this.lastId = lastId;
    }

    public void put(Selling selling) {
        int id = selling.getId();
        updateLastId(id);
        put(id, selling);
    }

    @Override
    public Selling put(Integer id, Selling selling) {
        updateLastId(id);
        super.put(id, selling);
        return selling;
    }

    public void remove(Selling selling) {
        remove(selling.getId());
    }

    public Selling createSelling(Plant plant, String date, double amount, double price,
            Seller seller) {
        return new Selling(++lastId, plant, date, amount, price, seller);
    }

    /**
     * Suranda pardavimus tam tikru mėnesiu
     * 
     * @param year - metai
     * @param month - mėnuo (0-11)
     * @return pardavimų mapas
     */
    public SellingMap findSellings(int year, int month) {
        SellingMap result = new SellingMap();
        Iterator<Selling> itr = values().iterator();
        while (itr.hasNext()) {
            Selling selling = itr.next();
            String date = selling.getDate();

            try {
                Scanner scanner = new Scanner(date);
                scanner.useDelimiter("-");

                int plantYear = scanner.nextInt();
                int plantMonth = scanner.nextInt() - 1;

                if (plantYear == year && plantMonth == month)
                    result.put(selling);

            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * Suranda visus pirkimus, per kuriuos buvo parduotas augalas
     * 
     * @param plant augalas
     */
    public SellingMap findSellings(Plant plant) {
        SellingMap result = new SellingMap();
        for (Selling selling : values())
            if (selling.getPlant() == plant)
                result.put(new Integer(selling.getId()), selling);
        return result;
    }

    public Double getProfit() {
        Double result = 0.00;
        for (Selling selling : values())
            result += selling.getAmount() * selling.getPrice();
        return result;
    }

    /**
     * Gauna visų parduotų tam tikru mėnesiu augalų kainų sumą
     * 
     * @param year - metai
     * @param month - mėnuo (0-11)
     * @return augalų kainų suma
     */
    public Double getProfit(int year, int month) {
        return findSellings(year, month).getProfit();
    }

    /**
     * Skaičiuoja kiek augalų parduota tam tikru mėnesiu
     * 
     * @param year - metai
     * @param month - mėnuo (0-11)
     * @return augalų skaičius
     */
    public int getPlantCount(int year, int month) {
        return findSellings(year, month).size();
    }

    private void updateLastId(int id) {
        if (lastId < id)
            lastId = id;
    }
}
