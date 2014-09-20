package lib;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Realizuoja duomenų rašymą ir skaitymą į XML failus
 */
public class FileManager {

    private String fileName;
    private final String rootNodeName = "augalai";

    public FileManager(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Skaito iš nurodyto failo duomenis ir įdeda juos į atitinkamus mapus, seni duomenys
     * mapuose sunaikinami
     * 
     * @param countryMap - šalių mapas
     * @param sellerMap - tiekėjų mapas
     * @param plantMap - augalų mapas
     * @param sellingMap - pardavimų mapas
     */
    public void read(CountryMap countryMap, SellerMap sellerMap,
            PlantMap plantMap, SellingMap sellingMap) {
        try {
            File file = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);
            Element documentElement = doc.getDocumentElement();

            if (documentElement != null) {
                documentElement.normalize();
                if (documentElement.getNodeName().equals(rootNodeName)) {
                    countryMap.clear();
                    NodeList nodeList = doc.getElementsByTagName("country");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            Country country = new Country(element);
                            countryMap.put(country);
                        }
                    }

                    sellerMap.clear();
                    nodeList = doc.getElementsByTagName("seller");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String name = element.getAttribute("name");
                            String countryName = element
                                    .getAttribute("country");
                            Country country = countryMap.get(countryName);

                            Seller seller = new Seller(name, country);
                            sellerMap.put(seller);
                            if (country != null)
                                country.addSeller(seller);
                        }
                    }

                    plantMap.clear();
                    nodeList = doc.getElementsByTagName("plant");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String name = element.getAttribute("name");
                            String plantType = element.getAttribute("type");

                            PlantManager plantManager = new PlantManager();
                            Plant plant = plantManager.createPlant(plantType,
                                    name);

                            try {
                                int growsFrom = Integer.parseInt(element
                                        .getAttribute("growsFrom"));
                                int growsUntil = Integer.parseInt(element
                                        .getAttribute("growsUntil"));

                                plant.setGrowsFrom(growsFrom);
                                plant.setGrowsUntil(growsUntil);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Seller seller = sellerMap.get(element
                                    .getAttribute("seller"));
                            plant.setSeller(seller);

                            plant.setDescription(element
                                    .getAttribute("description"));

                            if (plant != null) {
                                if (plant.isPoisonous()) {
                                    ((PoisonousPlant) plant).setPoison(element
                                            .getAttribute("poison"));
                                }
                                NodeList countryNodeList = element
                                        .getElementsByTagName("plantCountry");
                                for (int j = 0; j < countryNodeList.getLength(); j++) {
                                    Node countryNode = countryNodeList.item(j);
                                    if (countryNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element countryElement = (Element) node;
                                        String plantCountryName = countryElement
                                                .getAttribute("name");
                                        Country country = countryMap
                                                .get(plantCountryName);

                                        if (country != null)
                                            plant.addCountry(country);
                                    }
                                }

                                plantMap.put(plant);
                            }
                        }
                    }
                    sellingMap.clear();
                    nodeList = doc.getElementsByTagName("selling");

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            int id = Integer.parseInt(element
                                    .getAttribute("id"));

                            double amount, price;

                            try {
                                amount = Double.parseDouble(element
                                        .getAttribute("amount"));
                            } catch (Exception e) {
                                amount = 1.0;
                            }

                            try {
                                price = Double.parseDouble(element
                                        .getAttribute("price"));
                            } catch (Exception e) {
                                price = 0.0;
                            }

                            String date = element.getAttribute("date");
                            String plantName = element.getAttribute("plant");
                            String sellerName = element.getAttribute("seller");

                            Seller seller = sellerMap.get(sellerName);
                            Plant plant = plantMap.get(plantName);

                            Selling selling = new Selling(id, plant, date,
                                    amount, price, seller);

                            sellingMap.put(selling);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Rašo į failą duomenis iš mapų
     * 
     * @param countryMap - šalių mapas
     * @param sellerMap - tiekėjų mapas
     * @param plantMap - augalų mapas
     * @param sellingMap - pardavimų mapas
     */
    public void write(CountryMap countryMap, SellerMap sellerMap,
            PlantMap plantMap, SellingMap sellingMap) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(rootNodeName);
            doc.appendChild(rootElement);

            for (Country country : countryMap.values())
                country.appendToXML(doc);
            for (Seller seller : sellerMap.values())
                seller.appendToXML(doc);
            for (Plant plant : plantMap.values())
                plant.appendToXML(doc);
            for (Selling selling : sellingMap.values())
                selling.appendToXML(doc);

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
