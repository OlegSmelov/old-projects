package lib;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Country {
    private String name;
    private SellerMap sellerMap = new SellerMap();

    public Country(String name) {
        this.name = name;
        sellerMap = new SellerMap();
    }

    /**
     * Sukuria šalį pagal duomenis iš XML failo elemento
     * 
     * @param element
     */
    public Country(Element element) {
        name = element.getAttribute("name");
    }

    /**
     * Įrašo šalį į XML failą
     * 
     * @param doc - XML dokumentas
     */
    public void appendToXML(Document doc) {
        Element countryElement = doc.createElement("country");
        countryElement.setAttribute("name", this.name);
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

    public SellerMap getSellerMap() {
        return sellerMap;
    }

    public void setSellerMap(SellerMap sellerMap) {
        this.sellerMap = sellerMap;
    }

    /**
     * Grąžina ar šalis yra ta pati šalis, kuri yra pateikiama
     */
    public boolean equals(Country country) {
        if (this == country) { // rodykle į tą patį objektą
            return true;
        }
        return this.name.equalsIgnoreCase(country.getName());
    }

    public void addSeller(Seller seller) {
        sellerMap.put(seller);
    }

    public void removeSeller(Seller seller) {
        sellerMap.remove(seller);
    }

    /**
     * Kiek šalies prekeiviai uždirbo iš augalų prekybos
     */
    public Double getProfit() {
        Double result = 0.0;
        for (Seller seller : sellerMap.values())
            result += seller.getProfit();
        return result;
    }

    public String toString() {
        return name;
    }
}
