package water.ustc.dao;

import org.dom4j.DocumentException;
import water.ustc.initiator.ORMappingInitiator;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * Created by leegend on 2017/12/5.
 */
public abstract class BaseDAO {
    private static int CONN_LINKS = 0;
    //    为何要用Driver类
//    protected Driver driver;

    private String driver;
    private String url;
    private Statement statement;
    private Connection conn;

    public void openDBConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, DocumentException {
        this.setDriver(ORMappingInitiator.getDriverClass());
        this.setUrl(ORMappingInitiator.getUrlPath());

        Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(this.url, ORMappingInitiator.getDbUsername(), ORMappingInitiator.getDbPassword());

        if (conn != null) {
            this.setStatement(conn);
        }
    }

    public void closeDBConnection() throws SQLException {
        conn.close();
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setDriver(String driver) {
        this.driver = driver;
    }

    private void setStatement(Connection conn) {
        try {
            this.statement = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public static int getConnLinks() {
        return CONN_LINKS;
    }

    public static void setConnLinks(int connLinks) {
        CONN_LINKS = connLinks;
    }


    public abstract Object query(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;

    public abstract boolean insert(String sql) throws SQLException;

    public abstract boolean update(String sql) throws SQLException;

    public abstract boolean delete(String sql) throws SQLException;
}
