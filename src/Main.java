import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.sql.CharacterSet;

public class Main {
    public static void main(String[] args) {
        Connection conn = getConnection();

        //Test comments
        System.out.println("use DriverManager");
        if (conn != null) {
            Statement myStatement = createStatement(conn);
            String sql = "select * from customers";
            ResultSet myResultSet = getResultSet(myStatement, sql);
            readResult(myResultSet, new ArrayList<>(Arrays.asList("customer_id", "first_name")));
            closeResultSet(myResultSet);
            closeStatement(myStatement);
            closeConnection(conn);
        }

        System.out.println("use OracleDataSource");
        Connection conn2 = getDataSource();
        if (conn2 != null) {
            Statement myStatement = createStatement(conn2);
            String sql = "select * from product_changes t";
            ResultSet myResultSet = getResultSet(myStatement, sql);
            readResult(myResultSet, new ArrayList<>(Arrays.asList("name", "description", "price")));
            closeResultSet(myResultSet);
            insertData(myStatement);
            closeStatement(myStatement);
            closeConnection(conn2);
        }
    }

    public static Connection getConnection() {
        try {
            System.out.println("try connect");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@(description=(address=(host=localhost)" +
                            "(protocol=tcp)(port=1521))(connect_data=(sid=orcl)))",
                    "sys as sysdba",
                    "123"
            );
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Connection getDataSource() {
        try {
            OracleDataSource myDataSource = new OracleDataSource();
            myDataSource.setServerName("localhost");
            myDataSource.setDatabaseName("orcl");
            myDataSource.setDriverType("thin");
            myDataSource.setNetworkProtocol("tcp");
            myDataSource.setPortNumber(1521);
            myDataSource.setUser("sys as sysdba");
            myDataSource.setPassword("123");
            return myDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Statement createStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ResultSet getResultSet(Statement statement, String sql) {
        if (statement == null)
            return null;

        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readResult(ResultSet resultSet, ArrayList<String> columns) {
        try {
            while (resultSet.next()) {
                for (String column : columns) {
                    System.out.println(column + ": " + resultSet.getObject(column));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertData(Statement statement) {
        try {
            statement.executeUpdate(
                    "insert into customers values (32, 'Jhon', 'Rambo', to_date('06.07.1947', 'dd.mm.yyyy'), '1232-32-11')"
            );
            System.out.println("success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

class BasicExample {
    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            Connection connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:orcl",
                    "sys as sysdba",
                    "123"
            );
            connection.setAutoCommit(false);
            oracle.sql.NUMBER customerID = new oracle.sql.NUMBER(8);
            int customerIDInt = customerID.intValue();
            System.out.println("customerIDInt = " + customerIDInt);
            oracle.sql.CharacterSet characterSet = CharacterSet.make(CharacterSet.US7ASCII_CHARSET);
            oracle.sql.CHAR firstName = new oracle.sql.CHAR("Jason", characterSet);
            String firstNameString = firstName.stringValue();
            System.out.println("firstNameString = " + firstNameString);
            oracle.sql.CHAR lastName = new oracle.sql.CHAR("Price", characterSet);
            System.out.println("lastName = " + lastName);
            oracle.sql.DATE dob = new oracle.sql.DATE("1969-02-22 13:54:12");
            String dobString = dob.stringValue();
            System.out.println("dobString = " + dobString);
            OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) connection.prepareStatement(
                    "insert into customers values (?, ?, ?, ?, ?)"
            );
            oraclePreparedStatement.setNUMBER(1, customerID);
            oraclePreparedStatement.setCHAR(2, firstName);
            oraclePreparedStatement.setCHAR(3, lastName);
            oraclePreparedStatement.setDATE(4, dob);
            oraclePreparedStatement.setNull(5, Types.CHAR);
            oraclePreparedStatement.execute();
            System.out.println("Added row to customer table");

            Statement statement = connection.createStatement();
            OracleResultSet oracleResultSet = (OracleResultSet) statement.executeQuery(
                    "select rowid, t.* from customers t where t.customer_id = 8"
            );
            System.out.println("Retrieved row from customers table");
            oracle.sql.ROWID rowid;
            oracle.sql.CHAR phone = new oracle.sql.CHAR("", characterSet);
            while (oracleResultSet.next()) {
                rowid = oracleResultSet.getROWID("rowid");
                customerID = oracleResultSet.getNUMBER("customer_id");
                firstName = oracleResultSet.getCHAR("first_name");
                lastName = oracleResultSet.getCHAR("last_name");
                dob = oracleResultSet.getDATE("dob");
                phone = oracleResultSet.getCHAR("phone");
                System.out.println("rowid = " + rowid.stringValue());
                System.out.println("customerID = " + customerID.stringValue());
                System.out.println("firstName = " + firstName);
                System.out.println("lastName = " + lastName);
                System.out.println("dob = " + dob.stringValue());
                System.out.println("phone = " + phone);
            }

            oracleResultSet.close();
            connection.rollback();
            oraclePreparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}