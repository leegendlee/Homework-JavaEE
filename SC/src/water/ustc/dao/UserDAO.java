package water.ustc.dao;

import water.ustc.initiator.ORManager;
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
        super.openDBConnection();
        boolean insert = ORManager.insertObject(this.getStatement(), "UserBean", obj);
        super.closeDBConnection();
        return insert;
    }

    @Override
    public boolean update(Object obj) throws SQLException {
        super.openDBConnection();
        boolean update = ORManager.updateObject(this.getStatement(), "UserBean", obj);
        super.closeDBConnection();
        return update;
    }

    @Override
    public boolean delete(Object obj) throws SQLException {
        super.openDBConnection();
        boolean delete = ORManager.deleteObject(this.getStatement(), "UserBean", obj);
        super.closeDBConnection();
        return delete;
    }
}