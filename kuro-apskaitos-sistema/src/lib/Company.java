package lib;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

public class Company extends DatabaseObject {

    private String code;
    private String name;
    private String address;

    @Override
    public String getPrimaryKey() {
        return code;
    }

    @Override
    protected String getPrimaryKeyName() {
        return "code";
    }

    public static String[] getAccessibleAttributes() {
        return new String[] { "code", "name", "address" };
    }

    public Company(String code, String name, String address) {
        super();
        this.code = code;
        this.name = name;
        this.address = address;
    }

    public Company(ResultSet resultSet) throws SQLException{
        this(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
        this.isCreated = true;
    }

    public static LinkedList<Company> fetchAll() {
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("table", "company");
        options.put("order", "code");
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch(options));
    }

    public static Company fetchByCode(String code) {
        Company result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM company WHERE code = ? LIMIT 1;");
            ps.setString(1, code);
            LinkedList<Company> results = Company.fetch(ps);
            if (results != null && !results.isEmpty()) {
                result = results.getFirst();
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Company> fetch(PreparedStatement ps) {
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch("company", ps));
    }

    public LinkedList<Employee> fetchEmployees() {
        LinkedList<Employee> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT employee.* FROM employee, company, "
                    + "employee_works_for WHERE employee.ssn = employee_works_for.employee_ssn "
                    + "AND employee_works_for.company_code = company.code AND company.code = ?;");
            ps.setString(1, this.code);
            result = Employee.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public boolean addEmployee(Employee employee) {
        if (!employee.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO employee_works_for VALUES (?, ?);");
            ps.setString(1, employee.getSSN());
            ps.setString(2, this.code);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public boolean removeEmployee(Employee employee) {
        if (!employee.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM employee_works_for WHERE employee_ssn = ? AND company_code = ?;");
            ps.setString(1, employee.getSSN());
            ps.setString(2, this.code);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    @Override
    protected void destroyDependencies() throws SQLException {
        LinkedList<Car> cars_to_destroy = Car.fetchListByCompanyCode(this.code);
        for (Car car : cars_to_destroy) {
            car.destroy();
        }

        PreparedStatement ps = connection.prepareStatement("DELETE FROM employee_works_for WHERE company_code = ?;");
        ps.setString(1, this.code);
        ps.execute();

        LinkedList<Employee> employees_to_destroy = Employee.fetchByCompanyCode(this.code);
        for (Employee employee : employees_to_destroy) {
            employee.destroy();
        }
    }

    @Override
    protected PreparedStatement prepareUpdateStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE company SET code = ?, name = ?, address = ? WHERE code = ?");
        ps.setString(1, this.code);
        ps.setString(2, this.name);
        ps.setString(3, this.address);
        ps.setString(4, this.code);
        return ps;
    }

    @Override
    protected PreparedStatement prepareInsertionStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO company VALUES (?, ?, ?);");
        ps.setString(1, this.code);
        ps.setString(2, this.name);
        ps.setString(3, this.address);
        return ps;
    }

    @Override
    public boolean validate(){
        this.errors.clear();

        if (this.code == null || this.code.isEmpty()) {
            this.addError("Įmonės kodas negali būti tuščias");
        } else if (this.code.length() > 10) {
            this.addError("Įmonės kodas turi būti sudarytas iš mažiau nei 10 simbolių");
        } else if (!this.isCreated && Company.fetchByCode(this.code) != null) {
            this.addError("Įmonė su tokiu kodu jau egzistuoja");
        }
        if (this.name == null || this.name.isEmpty()) {
            this.addError("Įmonės vardas negali būti tuščias");
        }

        return !this.hasErrors();
    }

    private static LinkedList<Company> convertFromDatabaseObjectList(LinkedList<DatabaseObject> objects) {
        if (objects == null) {
            return null;
        }
        LinkedList<Company> result = new LinkedList<Company>();
        for (DatabaseObject object: objects) {
            result.push((Company) object);
        }
        return result;
    }

    public void setName(String name) {
        if (this.name == null ? name == null : this.name.equals(name)) {
            return;
        }
        this.isUpdated = false;
        this.name = name;
    }

    public void setAddress(String address) {
        if (this.address == null ? address == null : this.address.equals(address)) {
            return;
        }
        this.isUpdated = false;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        if (this.code == null ? code == null : this.code.equals(code)) {
            return;
        }
        this.isUpdated = false;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getAddress() {
        return address;
    }
}
