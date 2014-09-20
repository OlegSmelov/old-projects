package lib;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Pardavimo klasė
 */
public class Selling {
    private int id;
    private String date;
    private double amount;
    private double price;
    private Plant plant;
    private Seller seller;

    /**
     * Sukuria pardavimo irasa: parduotas augalas, pardavimo data, kiek gavo
     * pinigu, kas buvo pardavejas
     */
    public Selling(int id, Plant plant, String date, double amount, double price, Seller seller) {
        this.id = id;
        this.plant = plant;
        this.date = date;
        this.amount = amount;
        this.price = price;
        this.seller = seller;
    }

    public Plant getPlant() {
        return plant;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public Seller getSeller() {
        return seller;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    /**
     * Įrašo pardavimą į XML failą
     * 
     * @param doc - XML dokumentas
     */
    public void appendToXML(Document doc) {
        Element sellingElement = doc.createElement("selling");
        sellingElement.setAttribute("id", Integer.toString(id));
        sellingElement.setAttribute("date", date);
        sellingElement.setAttribute("amount", Double.toString(amount));
        sellingElement.setAttribute("price", Double.toString(price));
        if (seller != null)
            sellingElement.setAttribute("seller", seller.getName());
        if (plant != null)
            sellingElement.setAttribute("plant", plant.getName());
        Element documentElement = doc.getDocumentElement();
        if (documentElement != null)
            documentElement.appendChild(sellingElement);
    }

    @Override
    public String toString() {
        return date.toString() + " Parduotas: " + plant.toString()
                + ", pardave: " + seller.toString() + ", uz: " + price;
    }

}
