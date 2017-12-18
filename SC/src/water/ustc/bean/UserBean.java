package water.ustc.bean;

import org.dom4j.DocumentException;
import water.ustc.dao.UserDAO;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by leegend on 2017/12/5.
 */
public class UserBean extends BaseBean {
    private String userId;
    private String userName;
    private String userPass;

    public UserBean() {
    }

    public boolean signIn(String userName, String userPass) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, DocumentException, NoSuchMethodException, InvocationTargetException {
        UserDAO userDAO = new UserDAO();
        //需要添加一个根据字段查询的方法
        UserBean returnUser = (UserBean) userDAO.query(2);
//        System.out.println(returnUser.getUserId());
        if (returnUser != null) {
            if (Objects.equals(returnUser.getUserPass(), userPass)) {
                return true;
            }
        }

        return false;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
