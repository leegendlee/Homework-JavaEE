package water.ustc.action;

import org.dom4j.DocumentException;
import water.ustc.bean.UserBean;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by leegend on 2017/12/1.
 */
public class LoginAction {
    private final static String SUCCESS = "success";
    private final static String FAILURE = "failure";
    //for DI
    private UserBean userBean;

    public String login(HttpServletRequest req) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, DocumentException, NoSuchMethodException, InvocationTargetException {
        String userName = req.getParameter("userName") != null ? req.getParameter("userName") : "";
        String userPass = req.getParameter("userPass") != null ? req.getParameter("userPass") : "";
        //将signIn写成静态
//        UserBean userBean = new UserBean();
        if (this.userBean.signIn(userName, userPass)) {
            return SUCCESS;
        }

        return FAILURE;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public UserBean getUserBean() {
        return this.userBean;
    }
}
