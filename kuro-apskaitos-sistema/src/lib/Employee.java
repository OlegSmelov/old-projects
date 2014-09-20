package lib;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

public class Employee extends DatabaseObject {

    private String ssn;
    private String bank_account;
    private String first_name;
    private String last_name;

    @Override
    public String getPrimaryKey() {
        return ssn;
    }

    @Override
    protected String getPrimaryKeyName() {
        return "ssn";
    }

    public static String[] getAccessibleAttributes() {
        return new String[] { "ssn", "bank_account", "first_name", "last_name" };
    }

    public static LinkedList<Employee> fetchAll() {
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("table", "employee");
        options.put("order", "ssn");
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch(options));
    }

    public static LinkedList<Employee> fetchByCompanyCode(String company_code) {
        LinkedList<Employee> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT employee.* FROM employee, employee_works_for WHERE employee_works_for.employee_ssn = employee.ssn AND employee_works_for.company_code = ?;");
            ps.setString(1, company_code);
            result = Employee.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static Employee fetchBySsn(String ssn) {
        Employee result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM employee WHERE ssn = ? LIMIT 1;");
            ps.setString(1, ssn);
            LinkedList<Employee> results = Employee.fetch(ps);
            if (results != null && !results.isEmpty()) {
                result = results.getFirst();
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Employee> fetchListBySsn(String ssn) {
        LinkedList<Employee> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM employee WHERE ssn = ?;");
            ps.setString(1, ssn);
            result = Employee.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Employee> fetch(PreparedStatement ps) {
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch("employee", ps));
    }

    public Employee(String ssn, String bank_account, String first_name, String last_name) {
        super();
        this.ssn = ssn;
        this.bank_account = bank_account;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public Employee(ResultSet resultSet) throws SQLException{
        this(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
        this.isCreated = true;
    }

    public LinkedList<Company> fetchEmployers() {
        LinkedList<Company> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT company.* FROM employee, company, "
                    + "employee_works_for WHERE employee.ssn = employee_works_for.employee_ssn "
                    + "AND employee_works_for.company_code = company.code AND emplpoyee.ssn = ?;");
            ps.setString(1, this.ssn);
            result = Company.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public boolean addEmployer(Company employer) {
        if (!employer.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO employee_works_for VALUE (?, ?);");
            ps.setString(1, this.ssn);
            ps.setString(2, employer.getCode());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public boolean removeEmployer(Company employer) {
        if (!employer.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM employee_works_for WHERE employee_ssn = ? AND company_code = ?;");
            ps.setString(2, this.ssn);
            ps.setString(2, employer.getCode());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    @Override
    protected PreparedStatement prepareUpdateStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE employee SET ssn = ?, bank_account = ?, first_name = ?, last_name = ? WHERE ssn = ?");
        ps.setString(1, this.ssn);
        ps.setString(2, this.bank_account);
        ps.setString(3, this.first_name);
        ps.setString(4, this.last_name);
        ps.setString(5, this.ssn);
        return ps;
    }

    @Override
    protected PreparedStatement prepareInsertionStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO employee VALUES (?, ?, ?, ?);");

        ps.setString(1, this.ssn);
        ps.setString(2, this.bank_account);
        ps.setString(3, this.first_name);
        ps.setString(4, this.last_name);
        return ps;
    }

    @Override
    protected void destroyDependencies() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM employee_works_for WHERE employee_ssn = ?;"
                + "DELETE FROM employee_makes_trip WHERE employee_ssn = ?;");
        ps.setString(1, this.ssn);
        ps.setString(2, this.ssn);
        ps.execute();


        LinkedList<Car> cars_to_destroy = Car.fetchListByAccountableEmployeeSSN(this.ssn);
        for (Car car : cars_to_destroy) {
            car.destroy();
        }
    }

    @Override
    public boolean validate() {
        this.errors.clear();

        if (this.ssn == null || this.ssn.isEmpty()) {
            this.addError("Darbuotojo ssn negali būti tuščias");
        } else if ((this.ssn.length() < 6 || this.ssn.length() > 18)) {
            this.addError("Darbuotojo ssn negali būti trumpesnis nei 6 simboliai ir daugiau nei 18");
        } else if (!this.isCreated && Employee.fetchBySsn(this.ssn) != null) {
            this.addError("Darbuotojas su tokiu ssn jau egzistuoja");
        }
        if (this.first_name == null || this.first_name.isEmpty()) {
            this.addError("Darbuotojo vardas negali būti tuščias");
        } else if ((this.first_name.length() < 3 || this.first_name.length() > 18)) {
            this.addError("Darbuotojo vardas per trumpas arba per ilgas");
        }
        if (this.last_name == null || this.last_name.isEmpty()) {
            this.addError("Darbuotojo pavardė negali būti tuščia");
        } else if ((this.last_name.length() < 3 || this.last_name.length() > 18)) {
            this.addError("Darbuotojo pavardė per trumpa arba per ilga");
        }

        return !this.hasErrors();
    }

    private static LinkedList<Employee> convertFromDatabaseObjectList(LinkedList<DatabaseObject> objects) {
        if (objects == null) {
            return null;
        }
        LinkedList<Employee> result = new LinkedList<Employee>();
        for (DatabaseObject object: objects) {
            result.push((Employee) object);
        }
        return result;
    }

    public void setSSN(String ssn) {
        if (!this.isCreated && (this.ssn == null || !this.ssn.equals(ssn))) {
            this.ssn = ssn;
            this.isUpdated = false;
        }
    }

    public String getSSN() {
        return ssn;
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

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        if (this.first_name == null || !this.first_name.equals(first_name)) {
            this.isUpdated = false;
            this.first_name = first_name;
        }
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        if (this.last_name == null || !this.last_name.equals(last_name)) {
            this.isUpdated = false;
            this.last_name = last_name;
        }
    }
}
