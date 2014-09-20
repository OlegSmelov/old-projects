package lib;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;

public class Receipt extends DatabaseObject {

    private int id;
    private String fuel_code;
    private int trip_id;
    private double amount;
    private double price;
    private Date receipt_date;
    private String bank_account;
    private String seller_address;
    private String seller_company_code;
    private boolean is_paid;
    private String payer_ssn;

    @Override
    public Integer getPrimaryKey() {
        return new Integer(id);
    }

    @Override
    protected String getPrimaryKeyName() {
        return "id";
    }

    public static String[] getAccessibleAttributes() {
        return new String[] { "id", "fuel_code", "trip_id", "amount", "price",
          "receipt_date", "bank_account", "seller_address", "seller_company_code",
          "is_paid", "payer_ssn" };
    }

    public Receipt(String fuel_code, int trip_id, double amount, double price,
            Date receipt_date, String bank_account, String seller_address,
            String seller_company_code, boolean is_paid, String payer_ssn) {
        this.fuel_code = fuel_code;
        this.trip_id = trip_id;
        this.amount = amount;
        this.price = price;
        this.receipt_date = receipt_date;
        this.bank_account = bank_account;
        this.seller_address = seller_address;
        this.seller_company_code = seller_company_code;
        this.is_paid = is_paid;
        this.payer_ssn = payer_ssn;
    }

    public Receipt(ResultSet resultSet) throws SQLException{
        this(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3),
                resultSet.getDouble(4), resultSet.getDouble(5), resultSet.getDate(6),
                resultSet.getString(7), resultSet.getString(8), resultSet.getString(9),
                resultSet.getBoolean(10), resultSet.getString(11));
        this.isCreated = true;
    }

    public Receipt(int id, String fuel_code, int trip_id, double amount, double price,
            Date receipt_date, String bank_account, String seller_address,
            String seller_company_code, boolean is_paid, String payer_ssn) {
        this.id = id;
        this.fuel_code = fuel_code;
        this.trip_id = trip_id;
        this.amount = amount;
        this.price = price;
        this.receipt_date = receipt_date;
        this.bank_account = bank_account;
        this.seller_address = seller_address;
        this.seller_company_code = seller_company_code;
        this.is_paid = is_paid;
        this.payer_ssn = payer_ssn;
    }

    public static LinkedList<Receipt> fetchAll() {
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("table", "receipt");
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch(options));
    }

    public static Receipt fetchByID(int id) {
        Receipt result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM receipt WHERE id = ?");
            ps.setInt(1, id);
            LinkedList<Receipt> results = Receipt.fetch(ps);
            if (results != null && !results.isEmpty()) {
                result = results.getFirst();
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Receipt> fetchListByTripID(int trip_id) {
        LinkedList<Receipt> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM receipt WHERE trip_id = ?");
            ps.setInt(1, trip_id);
            result = Receipt.fetch(ps);
        } catch(SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Receipt> fetchListByFuelCode(String fuel_code) {
        LinkedList<Receipt> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM receipt WHERE fuel_code = ?");
            ps.setString(1, fuel_code);
            result = Receipt.fetch(ps);
        } catch(SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Receipt> fetch(PreparedStatement ps) {
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch("receipt", ps));
    }

    @Override
    protected PreparedStatement prepareUpdateStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE receipt SET fuel_code = ?, trip_id = ?, "
                    + "amount = ?, price = ?, receipt_date = ?, bank_account = ?, "
                    + "seller_address = ?, seller_company_code = ?, is_paid = ?,"
                    + "payer_ssn = ? WHERE id = ?");

        ps.setString(1, this.fuel_code);
        ps.setInt(2, this.trip_id);
        ps.setDouble(3, this.amount);
        ps.setDouble(4, this.price);
        ps.setDate(5, this.receipt_date);
        ps.setString(6, this.bank_account);
        ps.setString(7, this.seller_address);
        ps.setString(8, this.seller_company_code);
        ps.setBoolean(9, this.is_paid);
        ps.setString(10, this.payer_ssn);
        ps.setInt(11, this.id);

        return ps;
    }

    @Override
    protected PreparedStatement prepareInsertionStatement() throws SQLException {
         PreparedStatement ps = connection.prepareStatement("INSERT INTO receipt "
                 + "(fuel_code, trip_id, amount, price, receipt_date, bank_account, "
                 + "seller_address, seller_company_code, is_paid, payer_ssn) "
                 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

         ps.setString(1, this.fuel_code);
         ps.setInt(2, this.trip_id);
         ps.setDouble(3, this.amount);
         ps.setDouble(4, this.price);
         ps.setDate(5, this.receipt_date);
         ps.setString(6, this.bank_account);
         ps.setString(7, this.seller_address);
         ps.setString(8, this.seller_company_code);
         ps.setBoolean(9, this.is_paid);
         ps.setString(10, this.payer_ssn);

         return ps;
    }


    @Override
    protected void doAfterCreate(PreparedStatement ps) throws SQLException {
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.id = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Nepavyko gauti sugeneruoto id");
        }
    }

    @Override
    public boolean validate() {
        this.errors.clear();

        if (!this.isCreated && Receipt.fetchByID(this.id) != null) {
            this.addError("Sąskaita su tokiu id jau egzistuoja");
        }

        Fuel fuel = null;

        if (this.fuel_code == null || this.fuel_code.isEmpty()) {
            this.addError("Degalų kodas negali būti tuščias");
        } else {
            fuel = Fuel.fetchByCode(this.fuel_code);
            if (fuel == null) {
                this.addError("Degalų kodas neegzistuoja");
            }
        }

        Trip trip = Trip.fetchByID(this.trip_id);

        if (trip == null) {
            this.addError("Kelionė neegzistuoja");
        } else {
            if (fuel != null) {
                boolean fuel_used_in_trip = false;
                LinkedList<Car> cars = Car.fetchListByTripID(this.trip_id);
                if (cars != null) {
                    LinkedList<Fuel> fuels = Fuel.fetchListByCars(cars);
                    if (fuels != null) {
                        for(Fuel f: fuels) {
                            if (f.getCode().equals(fuel.getCode())) {
                                fuel_used_in_trip = true;
                                break;
                            }
                        }
                    }
                }
                if (!fuel_used_in_trip) {
                    this.addError("Kelionėje nebuvo naudojami automobiliai su įraše įrašytam degalų tipu");
                }
            }
        }

        if ((this.bank_account == null || this.bank_account.isEmpty()) && (this.payer_ssn ==  null)) {
            this.addError("Nenurodytas moketojo kodas arba mokejusio darbuotojo asmens kodas");
        }

        if (this.payer_ssn != null && Employee.fetchBySsn(this.payer_ssn) == null) {
            this.addError("Nurodytas neteisingas darbuotojo asmens kodas");
        }

        if (this.seller_address == null || this.seller_company_code.isEmpty()
                || this.seller_company_code == null || this.seller_company_code.isEmpty()) {
            this.addError("Visa informacija apie pardavėja turi būti užpildyta");
        }

        if (this.amount <= 0 || this.price <= 0) {
            this.addError("Neteisinga apmokėjimo sumos informacija");
        }

        return !this.hasErrors();
    }

    private static LinkedList<Receipt> convertFromDatabaseObjectList(LinkedList<DatabaseObject> objects) {
        if (objects == null) {
            return null;
        }
        LinkedList<Receipt> result = new LinkedList<Receipt>();
        for (DatabaseObject object: objects) {
            result.push((Receipt) object);
        }
        return result;
    }

    public int getID() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (this.amount != amount) {
            this.isUpdated = false;
            this.amount = amount;
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (this.price != price) {
            this.isUpdated = false;
            this.price = price;
        }
    }

    public Date getReceiptDate() {
        return receipt_date;
    }

    public void setReceiptDate(Date receipt_date) {
        if (this.receipt_date != receipt_date) {
            this.isUpdated = false;
            this.receipt_date = receipt_date;
        }
    }

    public String getBankAccount() {
        return bank_account;
    }

    public void setBankAccount(String bank_account) {
        if (this.bank_account == null || !this.bank_account.equals(bank_account)) {
            this.isUpdated = false;
            this.bank_account = bank_account;
        }
    }

    public String getSellerAddress() {
        return seller_address;
    }

    public void setSellerAddress(String seller_address) {
        if (this.seller_address != null && this.seller_address.equals(seller_address)) {
            return;
        }
        this.isUpdated = false;
        this.seller_address = seller_address;
    }

    public String getSellerCompanyCode() {
        return seller_company_code;
    }

    public void setSellerCompanyCode(String seller_company_code) {
        if (this.seller_company_code == null || !this.seller_company_code.equals(seller_company_code)) {
            this.isUpdated = false;
            this.seller_company_code = seller_company_code;
        }
    }

    public boolean getIsPaid() {
        return is_paid;
    }

    public void setIsPaid(boolean is_paid) {
        if (this.is_paid != is_paid) {
            this.isUpdated = false;
            this.is_paid = is_paid;
        }
    }

    public String getFuelCode() {
        return fuel_code;
    }

    public void setFuelCode(String fuel_code) {
        if (this.fuel_code == null || !this.fuel_code.equals(fuel_code)) {
            this.isUpdated = false;
            this.fuel_code = fuel_code;
        }
    }

    public int getTripID() {
        return trip_id;
    }

    public void setTripID(int trip_id) {
        if (this.trip_id != trip_id) {
            this.isUpdated = false;
            this.trip_id = trip_id;
        }
    }

    public String getPayerSSN() {
        return payer_ssn;
    }

    public void setPayerSSN(String payer_ssn) {
        if (this.payer_ssn == null || !this.payer_ssn.equals(payer_ssn)) {
            this.isUpdated = false;

            // if an empty string, assume null
            if (payer_ssn != null && payer_ssn.length() == 0) {
                this.payer_ssn = null;
            } else {
                this.payer_ssn = payer_ssn;
            }
        }
    }
}
