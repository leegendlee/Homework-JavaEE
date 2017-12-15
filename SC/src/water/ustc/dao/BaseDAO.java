package water.ustc.dao;

import org.dom4j.DocumentException;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * Created by leegend on 2017/12/5.
 */
public abstract class BaseDAO {
    protected Conversation conversation = null;
    //    为何要用driver
//    protected Driver driver;
    protected static Connection conn = null;
    protected String url;
    public Statement statement;

    public void openDBConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, DocumentException {
        String driver = "com.mysql.jdbc.Driver";
        this.setUrl("jdbc:mysql://localhost:3306/j2ee?useSSL=false");
        Class.forName(driver).newInstance();

        if (conn == null) {
            conn = DriverManager.getConnection(this.url, "root", "");
        }

        if (this.conversation == null) {
            this.conversation = new Conversation();
        }
    }

    public void closeDBConnection() throws SQLException {
        conn.close();
    }

    public abstract Object query(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;

    public abstract boolean insert(String sql) throws SQLException;

    public abstract boolean update(String sql) throws SQLException;

    public abstract boolean delete(String sql) throws SQLException;

//    public void setDriver(Driver driver) {
//        this.driver = driver;
//    }

    public void setUrl(String url) {
        this.url = url;

    }

//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public void setUserPassword(String userPassword) {
//        this.userPassword = userPassword;
//    }
}
