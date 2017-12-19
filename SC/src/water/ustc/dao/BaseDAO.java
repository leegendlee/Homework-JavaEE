package water.ustc.dao;

import water.ustc.initiator.ORMappingInitiator;
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

    public void openDBConnection() {
        this.setDriver(ORMappingInitiator.getDriverClass());
        this.setUrl(ORMappingInitiator.getUrlPath());

        try {
            Class.forName(this.driver).newInstance();
            //效率不高，考虑使用连接池
            conn = DriverManager.getConnection(this.url, ORMappingInitiator.getDbUsername(), ORMappingInitiator.getDbPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (conn != null) {
            this.setStatement(conn);
        }
    }

    public void closeDBConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
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

    //用id查找
    public abstract Object query(int id) throws SQLException;

    public abstract boolean insert(Object obj) throws SQLException;

    public abstract boolean update(Object obj) throws SQLException;

    public abstract boolean delete(Object obj) throws SQLException;
}