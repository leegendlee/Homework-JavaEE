package water.ustc.initiator;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by leegend on 2017/12/15.
 */
public class ORManager {
    private static List<Element> CLASSES = BaseInitiator.getOrMappingXmlRoot().elements("class");
    //这里应该实时根据包名改变，因为这个类是jar包，代码不可改变
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

                        break;
                    }
                }
            }
        }

        return null;
    }

    public static boolean insertObject(Statement statement, String classNameArg, Object obj) {
        if (obj != null) {
            try {
                BeanInfo objBI = Introspector.getBeanInfo(obj.getClass(), Object.class);
                PropertyDescriptor[] objPropsRaw = objBI.getPropertyDescriptors();

                Map<String, PropertyDescriptor> objProps = new HashMap<String, PropertyDescriptor>();
                for (PropertyDescriptor objProp : objPropsRaw) {
                    objProps.put(objProp.getName(), objProp);
                }

                for (Element orClass : CLASSES) {
                    Element className = orClass.element("name");
                    Element classTable = orClass.element("table");
                    if (className != null && classTable != null) {
                        String textClassName = className.getTextTrim();

                        if (textClassName.equals(obj.getClass().getSimpleName())) {
                            List<Element> classProperties = orClass.elements("property");

                            List<String> insertKeys = new ArrayList<String>();
                            List<String> insertValues = new ArrayList<String>();
                            for (Element prop : classProperties) {
                                Element name = prop.element("name");
                                Element column = prop.element("column");

                                if (name != null && column != null) {
                                    if (orClass.element("id") != null
                                            && Objects.equals(name.getTextTrim(), orClass.element("id").getTextTrim())) {
                                        continue;
                                    }

                                    String valueName = name.getTextTrim();
                                    if (objProps.containsKey(valueName)) {
                                        //数据类型
                                        insertKeys.add(column.getTextTrim());
                                        insertValues.add("'" + (String) objProps.get(valueName).getReadMethod().invoke(obj) + "'");
                                    }
                                }
                            }

                            String sql = "INSERT INTO " + classTable.getTextTrim() + " (" +
                                    StringUtils.join(insertKeys.toArray(), ",") + ")" + " VALUES " +
                                    "(" + StringUtils.join(insertValues.toArray(), ",") + ")";

                            int result = statement.executeUpdate(sql);
                            return result != 0;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean updateObject(Statement statement, String classNameArg, Object object) {
        return false;
    }

    public static boolean deleteObject(Statement statement, String classNameArg, Object object) {
        return false;
    }
}
