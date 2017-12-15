package water.ustc.bean;

import org.dom4j.DocumentException;
import water.ustc.dao.UserDAO;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by leegend on 2017/12/5.
 */
public class UserBean {
    public int id;
    protected String userName;
    protected String userPass;

    //构造函数参数个数问题
    public UserBean() {

    }

    public boolean signIn(String userName, String userPass) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, DocumentException, NoSuchMethodException, InvocationTargetException {
        UserDAO userDAO = new UserDAO(userName, userPass);
        UserBean returnUser = (UserBean) userDAO.query();
        if (returnUser != null) {
            if (Objects.equals(returnUser.getUserPass(), userPass)) {
                return true;
            }
        }

        return false;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserPass() {
        return this.userPass;
    }
}
