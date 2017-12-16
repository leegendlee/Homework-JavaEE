package water.ustc.dao;

import org.dom4j.DocumentException;
import water.ustc.bean.UserBean;
import water.ustc.initiator.ORManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by leegend on 2017/12/5.
 */
public class UserDAO extends BaseDAO {
    @Override
    public Object query(int id) throws SQLException {
        //可以改善此繁琐的写法，使用注入
        super.openDBConnection();
        Object obj = ORManager.getObject(this.getStatement(), "UserBean", id);
        super.closeDBConnection();
        return obj;
    }

    @Override
    public boolean insert(Object obj) throws SQLException {
        return false;
    }

    @Override
    public boolean update(Object obj) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Object obj) throws SQLException {
        return false;
    }

//    @Override
//    public Object query(String sql) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        return this.conversation.getObject(new UserBean());
////        this.statement = conn.createStatement();
////        ResultSet result = statement.executeQuery(sql);
////        if (result.next()) {
////            UserBean userBean = new UserBean();
////            userBean.setUserName(result.getString("user_name"));
////            userBean.setUserPass(result.getString("user_password"));
////            return userBean;
////        }
//    }
//
//    @Override
//    public boolean insert(String sql) throws SQLException {
//        this.statement = conn.createStatement();
//        if (statement.executeUpdate(sql) != 0) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean update(String sql) throws SQLException {
//        this.statement = conn.createStatement();
//        if (statement.executeUpdate(sql) != 0) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean delete(String sql) throws SQLException {
//        this.statement = conn.createStatement();
//        if (statement.executeUpdate(sql) != 0) {
//            return true;
//        }
//
//        return false;
//    }
}