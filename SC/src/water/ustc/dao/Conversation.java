package water.ustc.dao;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

/**
 * Created by leegend on 2017/12/6.
 */
public class Conversation extends BaseDAO {
    protected Configuration configuration = null;

    public Conversation() throws DocumentException {
        if (this.configuration == null) {
            configuration = new Configuration();
        }
    }

    public Object getObject(Object object) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List<Element> classes = configuration.rootElement.elements("class");
        for (Element singleClass : classes) {
            if (Objects.equals(singleClass.element("name").getText(), object.getClass().getSimpleName())) {
                this.statement = conn.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM " + singleClass.element("table").getText() +
                        " WHERE id=1");
                if (result.next()) {
                    List<Element> returnClassProperties = singleClass.elements("property");

                    //返回对象实例化
                    Class rawClass = Class.forName(object.getClass().getName());
                    Object returnObj = rawClass.newInstance();

                    for (Element property : returnClassProperties) {
                        //首字母大写问题
                        String type = property.element("type").getText();

                        Method returnClassMethod = rawClass.getMethod("set" + property.element("name").getText(), String.class);
                        returnClassMethod.invoke(returnObj, result.getString(property.element("column").getText()));

                    }

                    return returnObj;
                }

                break;
            }
        }

        return null;
    }

    public boolean insertObject(Object object) {
        return false;
    }

    public boolean updateObject(Object object) {
        return false;
    }

    public boolean deleteObject(Object object) {
        return false;
    }

    @Override
    public Object query(String sql) throws SQLException {
        return null;
    }

    @Override
    public boolean insert(String sql) throws SQLException {
        return false;
    }

    @Override
    public boolean update(String sql) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(String sql) throws SQLException {
        return false;
    }
}
