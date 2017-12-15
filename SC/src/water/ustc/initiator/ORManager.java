package water.ustc.initiator;

import org.dom4j.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by leegend on 2017/12/15.
 */
public class ORManager {
    private static List<Element> CLASSES = BaseInitiator.getOrMappingXmlRoot().elements("class");

    //以id查找，需要重载（或者使用泛型）
    public static Object getObject(Statement statement, String className, int id) {
        if (CLASSES == null) {
            return null;
        }

        for (Element orClass : CLASSES) {
            Element elementName = orClass.element("name");
            Element elementTable = orClass.element("name");
            if (elementName != null && elementTable != null) {
                String orClassName = elementName.getTextTrim();

                if (orClassName.equals(className)) {
                    try {
                        ResultSet result = statement.executeQuery("SELECT * FROM " + elementTable.getTextTrim() +
                                " WHERE id=" + id);

                        if (result.first()) {
                            List<Element> classProperties = orClass.elements("property");
                            if (!classProperties.isEmpty()) {
                                for (Element classProperty : classProperties) {
                                    //填充属性至返回的反射类中
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
//        for (Element singleClass : classes) {
//            if (Objects.equals(singleClass.element("name").getText(), object.getClass().getSimpleName())) {
//                this.statement = conn.createStatement();
//                ResultSet result = statement.executeQuery("SELECT * FROM " + singleClass.element("table").getText() +
//                        " WHERE id=1");
//                if (result.next()) {
//                    List<Element> returnClassProperties = singleClass.elements("property");
//
//                    //返回对象实例化
//                    Class rawClass = Class.forName(object.getClass().getName());
//                    Object returnObj = rawClass.newInstance();
//
//                    for (Element property : returnClassProperties) {
//                        //首字母大写问题
//                        String type = property.element("type").getText();
//
//                        Method returnClassMethod = rawClass.getMethod("set" + property.element("name").getText(), String.class);
//                        returnClassMethod.invoke(returnObj, result.getString(property.element("column").getText()));
//
//                    }
//
//                    return returnObj;
//                }
//
//                break;
//            }
//        }

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
