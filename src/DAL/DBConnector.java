package dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;



public class SQLController {
    private static final String PROP_FILE = ".idea/config/config.settings";
    private SQLServerDataSource dataSource;

    public SQLController() throws IOException{
        Properties databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream(new File(PROP_FILE)));

        dataSource = new SQLServerDataSource();
        dataSource.setServerName(databaseProperties.getProperty("server"));
        dataSource.setDatabaseName(databaseProperties.getProperty("database"));
        dataSource.setUser(databaseProperties.getProperty("user"));
        dataSource.setPassword(databaseProperties.getProperty("password"));
        dataSource.setPortNumber(1433);
        dataSource.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }


    public static void main(String[] args) throws SQLException, IOException {

        SQLController databaseConnector = new SQLController();

        try (Connection connection = databaseConnector.getConnection()) {

            System.out.println("Is it open? " + !connection.isClosed());

        } //Connection gets closed here
    }
}
