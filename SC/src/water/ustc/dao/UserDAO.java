package water.ustc.dao;

import org.dom4j.DocumentException;
import water.ustc.bean.UserBean;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by leegend on 2017/12/5.
 */
public class UserDAO extends BaseDAO {
//    protected String userName;
//    protected String userPassword;

    //泛型+不定数量的参数
    public UserDAO(String userName, String userPass) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, DocumentException {
        this.openDBConnection();
//        if (conn == null) {
//            return;
//        }
    }



    @Override
    public Object query(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return this.conversation.getObject(new UserBean());
//        this.statement = conn.createStatement();
//        ResultSet result = statement.executeQuery(sql);
//        if (result.next()) {
//            UserBean userBean = new UserBean();
//            userBean.setUserName(result.getString("user_name"));
//            userBean.setUserPass(result.getString("user_password"));
//            return userBean;
//        }
    }

    @Override
    public boolean insert(String sql) throws SQLException {
        this.statement = conn.createStatement();
        if (statement.executeUpdate(sql) != 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean update(String sql) throws SQLException {
        this.statement = conn.createStatement();
        if (statement.executeUpdate(sql) != 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(String sql) throws SQLException {
        this.statement = conn.createStatement();
        if (statement.executeUpdate(sql) != 0) {
            return true;
        }

        return false;
    }
}