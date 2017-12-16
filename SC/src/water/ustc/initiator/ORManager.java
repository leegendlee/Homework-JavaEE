package water.ustc.initiator;

import org.dom4j.Element;
import water.ustc.bean.UserBean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leegend on 2017/12/15.
 */
public class ORManager {
    private static List<Element> CLASSES = BaseInitiator.getOrMappingXmlRoot().elements("class");
    //这里应该实时根据包名改变
    private static String BEAN_PATH = "water.ustc.bean.";

    //以id查找，需要重载（或者使用泛型）
    public static Object getObject(Statement statement, String classNameArg, int id) {
        if (CLASSES != null) {
            for (Element orClass : CLASSES) {
                Element className = orClass.element("name");
                Element classTable = orClass.element("table");
                if (className != null && classTable != null) {
                    String textClassName = className.getTextTrim();

                    if (textClassName.equals(classNameArg)) {
                        try {
                            ResultSet result = statement.executeQuery("SELECT * FROM " + classTable.getTextTrim() +
                                    " WHERE id=" + id);
                            if (result.first()) {
                                Object returnObj = (Class.forName(BEAN_PATH + textClassName)).newInstance();
                                BeanInfo objBI = Introspector.getBeanInfo(returnObj.getClass(), Object.class);
                                PropertyDescriptor[] objPropsRaw = objBI.getPropertyDescriptors();

                                Map<String, PropertyDescriptor> objProps = new HashMap<String, PropertyDescriptor>();
                                for (PropertyDescriptor objProp : objPropsRaw) {
                                    objProps.put(objProp.getName(), objProp);
                                }

                                List<Element> classProperties = orClass.elements("property");
                                for (Element prop : classProperties) {
                                    Element name = prop.element("name");
                                    Element column = prop.element("column");

                                    if (name != null && column != null) {
                                        String valueName = name.getTextTrim();
                                        String valueColumn = result.getString(column.getTextTrim());
                                        if (objProps.containsKey(valueName) && (valueColumn != null)) {
                                            //数据类型、懒加载
                                            objProps.get(valueName).getWriteMethod().invoke(returnObj, valueColumn);
                                        }
                                    }
                                }

                                return returnObj;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
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
}
