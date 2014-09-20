package lib;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DatabaseObjectFetcher {

    private static final Map<String, Class<?>> supportedClasses;

    static {
        Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
        classMap.put("company", Company.class);
        classMap.put("car", Car.class);
        classMap.put("employee", Employee.class);
        classMap.put("receipt", Receipt.class);
        classMap.put("fuel", Fuel.class);
        classMap.put("trip", Trip.class);
        supportedClasses = Collections.unmodifiableMap(classMap);
    }
    ;

    private static final String[] allowedFetchOptions = {"table", "filtering",
        "searching", "order", "descOrder", "page", "perPage"};
    private static final int defaultPerPage = 5;

    /**
     * creates new databaseObject. Not all tables are supported
     * @param table
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static DatabaseObject createDatabaseObject(String table, ResultSet resultSet) throws SQLException {
        DatabaseObject result = null;
        try {
            Class[] constructorArguments = {ResultSet.class};
            result = (DatabaseObject) supportedClasses.get(table).getConstructor(constructorArguments).newInstance(resultSet);
        } catch (Exception ex) {
            throw new SQLException("Erorr creating database object: UNSUPPORTED TABLE or ERROR in CONSTRUCTOR");
        }
        return result;
    }

    /**
     * fetches LinkedList of objects from table using prepared statement
     * @param table - table name. Not all tables are supported
     * @param ps - prepared statement
     * @return
     */
    public static LinkedList<DatabaseObject> fetch(String table, PreparedStatement ps) {
        LinkedList<DatabaseObject> list = null;

        try {
            ResultSet resultSet = ps.executeQuery();
            list = new LinkedList<DatabaseObject>();

            while (resultSet.next()) {
                DatabaseObject object = createDatabaseObject(table, resultSet);
                list.push(object);
            }

        } catch (SQLException ex) {
            System.out.println(ex);
            return null;
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                return null;
            }
        }

        return list;
    }

    /**
     *
     * @param options - hash
     * Supported keys:
     * table - referenced DB table
     * filtering - HashMap<String, Object> for WHERE statements
     * perPage, page - paging options
     * order, descOrder - result ordering
     *
     * @return
     */
    public static LinkedList<DatabaseObject> fetch(HashMap<String, Object> options) {
        try {
            String table = (String) options.get("table");

            if (table == null) {
                System.err.println("Required fetch option table non-existant or invalid");
                return null;
            }

            Class objectClass = supportedClasses.get(table);
            Method[] methods = objectClass.getMethods();
            String[] accessibleAttributes = null;

            for (Method method : methods) {
                if (method.getName().equals("getAccessibleAttributes")) {
                    accessibleAttributes = (String[]) method.invoke(null, new Object[0]);
                    break;
                }
            }

            if (accessibleAttributes == null) {
                throw new Exception("fatal error");
            }

            for (Object option : options.keySet().toArray()) {
                if (!Arrays.asList(allowedFetchOptions).contains((String) option)) {
                    throw new Exception("Disallowed fetch option");
                }
            }

            String builtStatement = "";
            builtStatement += "SELECT " + table + ".* FROM " + table;
            builtStatement += " WHERE (0=0) ";
            LinkedList<Object> statementAttributes = new LinkedList<Object>();

            if (options.get("filtering") != null) {
                HashMap filtering = (HashMap) options.get("filtering");
                for (Object key: filtering.keySet()) {
                    if (!Arrays.asList(accessibleAttributes).contains((String) key)) {
                        throw new Exception("Attribute is not allowed to be accessed");
                    }
                    builtStatement += " AND " + key + " = ? ";
                    statementAttributes.push(filtering.get(key));
                }
            }

	    if (options.get("searching") != null) {
                HashMap searching = (HashMap) options.get("filtering");
                for (Object key: searching.keySet()) {
                    if (!Arrays.asList(accessibleAttributes).contains((String) key)) {
                        throw new Exception("Attribute is not allowed to be accessed");
                    }
                    builtStatement += " AND " + key + " like '?%' ";
                    statementAttributes.push(searching.get(key));
                }
            }

            if (options.get("order") != null) {
                builtStatement += "ORDER BY " + options.get("order") + " ASC ";
            } else if (options.get("descOrder") != null) {
                builtStatement += "ORDER BY " + options.get("descOrder") + " DESC ";
            }

            if (options.get("page") != null) {
                int page = ((Integer) options.get("page")).intValue();
                int perPage = defaultPerPage;
                if (options.get("perPage") != null && options.get("perPage") instanceof Integer) {
                    perPage = ((Integer) options.get("perPage")).intValue();
                }
                builtStatement += "LIMIT " + ((page - 1) * perPage) + ", " + (page * perPage) + " ";
            }

            builtStatement += ";";

            PreparedStatement ps = DatabaseObject.connection.prepareStatement(builtStatement);
            int psIndex = 1;
            for (Object attr: statementAttributes) {
                if (attr instanceof String) {
                    ps.setString(psIndex++, (String) attr);
                } else if (attr instanceof Integer) {
                    ps.setInt(psIndex++, ((Integer) attr).intValue());
                }
            }

            return fetch(table, ps);

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return null;
    }
}
