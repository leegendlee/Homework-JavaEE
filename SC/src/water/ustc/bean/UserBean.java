package water.ustc.bean;

import net.sf.cglib.proxy.Enhancer;
import org.dom4j.DocumentException;
import water.ustc.dao.UserDAO;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
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

    @Override
    public void lazyLoad(List<String> propNames) {
        try {
            BeanInfo thisBI = Introspector.getBeanInfo(this.getClass(), Object.class);
            PropertyDescriptor[] thisPros = thisBI.getPropertyDescriptors();
            for (String name : propNames) {
                for (PropertyDescriptor thisProp : thisPros) {
                    if (thisProp.getName().equals(name)) {
                        thisProp.getWriteMethod().invoke(this, String.valueOf(this.lazyLoad()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object lazyLoad() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserBean.class);
        return enhancer.create(UserBean.class, new UserBean());
    }

    @Override
    public Object loadObject() throws Exception {
        System.out.println("lazyLoad...");
        //此处需要动态传参
        String prop = "123123123123123123123123123";
        return prop;
    }

    public boolean signIn(String userName, String userPass) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, DocumentException, NoSuchMethodException, InvocationTargetException {
        UserDAO userDAO = new UserDAO();
        //需要添加一个根据字段查询的方法
        UserBean returnUser = (UserBean) userDAO.query(2);
        System.out.println(returnUser.getUserPass());
//        System.out.println(returnUser.getUserName());
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
