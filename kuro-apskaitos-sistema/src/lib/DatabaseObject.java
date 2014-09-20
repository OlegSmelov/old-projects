package lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseObject {

		protected static Connection connection;
		protected boolean isUpdated;   // is out of synch with database
		protected boolean isCreated;   // is created in database
		protected ArrayList<String> errors;

		public DatabaseObject() {
				this.isUpdated = true;
				this.isCreated = false;
				this.errors = new ArrayList<String>();
		}

    /**
     * Set connection to database that all classes inheriting DatabaseObject will use
     * @param connection
     */
		public static void setConnection(Connection connection) {
				DatabaseObject.connection = connection;
		}

		public static void closeConnetion() {
				try {
						DatabaseObject.connection.close();
				} catch (SQLException ex) {
						Logger.getLogger(DatabaseObject.class.getName()).log(Level.SEVERE, null, ex);
				}
		}

		public boolean isUpdated() {
				return isUpdated;
		}

		public boolean isCreated() {
				return isCreated;
		}

    /**
     * Inserts object in database if it's new object
     */
		public boolean create() {
				if (this.isCreated) {
						return true;
				}

				if (!this.validate()) {
						return false;
				}

				PreparedStatement ps = null;

				try {

						ps = prepareInsertionStatement();
						ps.executeUpdate();
            this.doAfterCreate(ps);
						this.isUpdated = true;
						this.isCreated = true;

				} catch (SQLException ex) {

						System.err.println(ex);
						return false;

				} finally {
						try {
								ps.close();
						} catch (SQLException ex) {
						}
				}

				return true;
		}

    /**
     * Submits changes to database if values have been updated
     */
		public boolean submitChanges() {
				if (this.isUpdated) {
						return true;
				}

				if (!this.validate()) {
						return false;
				}

				PreparedStatement ps = null;

				try {

						ps = this.prepareUpdateStatement();
						ps.executeUpdate();
						this.isUpdated = true;

				} catch (SQLException ex) {

						System.err.println(ex);
						return false;

				} finally {
						try {
								ps.close();
						} catch (SQLException ex) {
						}
				}

				return true;
		}

    /**
     * destroy object from database
     */
		public boolean destroy() {
				if (!this.isCreated) {
						return true;
				}

				PreparedStatement ps = null;

				try {
						this.destroyDependencies();

						ps = connection.prepareStatement("DELETE FROM " + this.getClass().getSimpleName() + " WHERE " + this.getPrimaryKeyName() + " = ?;");
						Object objectPrimaryKey = this.getPrimaryKey();
						if (objectPrimaryKey instanceof String) {
								ps.setString(1, (String) objectPrimaryKey);
						} else if (objectPrimaryKey instanceof Integer) {
								ps.setInt(1, ((Integer) objectPrimaryKey).intValue());
						}
						ps.execute();

						this.isUpdated = true;
						this.isCreated = false;

				} catch (SQLException ex) {
						System.err.println(ex);
						return false;
				} finally {
						try {
								ps.close();
						} catch (SQLException ex) {
						}
				}

				return true;
		}

    /**
     * Get errors that occurred while validating object
     */
		public ArrayList<String> getErrors() {
				return this.errors;
		}

		protected void addError(String error) {
				this.errors.add(error);
		}

    /**
     * Check if record can be submitted to database
     */
		public boolean validate() {
				this.errors.clear();
				return !this.hasErrors();
		}

    /**
     * does object has errors that prevents it from saving data to database
     */
		public boolean hasErrors() {
				return !this.errors.isEmpty();
		}

    /**
     * get primary key value
     */
		public Object getPrimaryKey() {
				return null;
		}

    /**
     * get primary key name in database
     */
		protected String getPrimaryKeyName() {
				return null;
		}

		protected PreparedStatement prepareInsertionStatement() throws SQLException {
				throw new SQLException("prepareInsertionStatement must be overiden!");
		}

		protected PreparedStatement prepareUpdateStatement() throws SQLException {
				throw new SQLException("prepareUpdateStatement must be overiden!");
		}

    protected void doAfterCreate(PreparedStatement ps) throws SQLException {
    }

    /**
     * destroy all dependencies in database.
     * @throws SQLException
     */
		protected void destroyDependencies() throws SQLException {
		}
}
