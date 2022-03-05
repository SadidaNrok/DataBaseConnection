import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

public class Main {
    public static void main(String[] args) {
        Connection conn = getConnection();


        System.out.println("use DriverManager");
        if (conn != null) {
            Statement myStatement = createStatement(conn);
            String sql = "select * from customers";
            ResultSet myResultSet = getResultSet(myStatement, sql);
            readResult(myResultSet, new ArrayList<>(Arrays.asList("customer_id", "first_name")));
            closeResultSet(myResultSet);
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

    public static Statement createStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
