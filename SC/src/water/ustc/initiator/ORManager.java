package water.ustc.initiator;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
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
                                Class returnClass = Class.forName(BEAN_PATH + textClassName);
                                Object returnObj = returnClass.newInstance();
                                BeanInfo objBI = Introspector.getBeanInfo(returnObj.getClass(), Object.class);
                                PropertyDescriptor[] objPropsRaw = objBI.getPropertyDescriptors();

                                Map<String, PropertyDescriptor> objProps = new HashMap<String, PropertyDescriptor>();
                                for (PropertyDescriptor objProp : objPropsRaw) {
                                    objProps.put(objProp.getName(), objProp);
                                }

                                Map<String, Object> lazyLoads = new HashMap<String, Object>();

                                List<Element> classProperties = orClass.elements("property");
                                for (Element prop : classProperties) {
                                    Element name = prop.element("name");
                                    Element column = prop.element("column");
                                    Element type = prop.element("type");
                                    Element lazy = prop.element("lazy");

                                    if (name != null && column != null) {
                                        String valueName = name.getTextTrim();
                                        String valueColumn = result.getString(column.getTextTrim());
                                        if (objProps.containsKey(valueName) && (valueColumn != null)) {
                                            //数据类型
                                            if (lazy != null && Objects.equals(lazy.getTextTrim(), "true")) {
                                                lazyLoads.put(valueName, valueColumn);
                                            } else {
                                                objProps.get(valueName).getWriteMethod().invoke(returnObj, valueColumn);
                                            }
                                        }
                                    }
                                }

                                Method lazyLoad = returnClass.getMethod("lazyLoad", Map.class);
                                lazyLoad.invoke(returnObj, lazyLoads);
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

    public static boolean updateObject(Statement statement, String classNameArg, Object obj) {
        //需要保持数据一致性，取数据时将原始数据保存在缓存区
        if (obj != null) {
            try {
                BeanInfo objBI = Introspector.getBeanInfo(obj.getClass(), Object.class);
                PropertyDescriptor[] objPropsRaw = objBI.getPropertyDescriptors();

                for (Element orClass : CLASSES) {
                    Element className = orClass.element("name");
                    Element classTable = orClass.element("table");
                    if (className != null && classTable != null) {
                        String textClassName = className.getTextTrim();

                        if (textClassName.equals(obj.getClass().getSimpleName())) {
                            Object objRaw = new Object();
                            int id = 0;

                            Map<String, PropertyDescriptor> objProps = new HashMap<String, PropertyDescriptor>();
                            for (PropertyDescriptor objProp : objPropsRaw) {
                                String propName = objProp.getName();
                                //效率问题
                                if (orClass.element("id") != null && propName.equals(orClass.element("id").getTextTrim())) {
                                    //数据类型
                                    id = Integer.parseInt((String) objProp.getReadMethod().invoke(obj));
                                    objRaw = getObject(statement, classNameArg, id);
                                    if (objRaw == null) {
                                        return false;
                                    }
                                }

                                objProps.put(objProp.getName(), objProp);
                            }

                            //更好的做法是先检测原始数据和新数据是否相等，不相等时再进行更新，并且相同的数据不更新，麻烦。。

                            List<String> updateKeyValues = new ArrayList<String>();

                            List<Element> classProperties = orClass.elements("property");
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
                                        updateKeyValues.add(column.getTextTrim() + "='"
                                                + (String) objProps.get(valueName).getReadMethod().invoke(obj) + "'");
                                    }
                                }
                            }

                            int result = statement.executeUpdate("UPDATE " + classTable.getTextTrim()
                                    + " SET " + StringUtils.join(updateKeyValues.toArray(), ",") + " WHERE id=" + id);
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

    //通过主键删？卧艹没自己写过ORM，好方啊。。。
    public static boolean deleteObject(Statement statement, String classNameArg, Object obj) {
        if (obj != null) {
            try {
                BeanInfo objBI = Introspector.getBeanInfo(obj.getClass(), Object.class);
                PropertyDescriptor[] objPropsRaw = objBI.getPropertyDescriptors();

                for (Element orClass : CLASSES) {
                    Element className = orClass.element("name");
                    Element classTable = orClass.element("table");
                    if (className != null && classTable != null) {
                        String textClassName = className.getTextTrim();

                        if (textClassName.equals(obj.getClass().getSimpleName())) {
                            Object objRaw = new Object();
                            int id = 0;

                            Map<String, PropertyDescriptor> objProps = new HashMap<String, PropertyDescriptor>();
                            for (PropertyDescriptor objProp : objPropsRaw) {
                                String propName = objProp.getName();
                                //效率问题
                                if (orClass.element("id") != null && propName.equals(orClass.element("id").getTextTrim())) {
                                    //数据类型
                                    id = Integer.parseInt((String) objProp.getReadMethod().invoke(obj));
                                    objRaw = getObject(statement, classNameArg, id);
                                    if (objRaw == null) {
                                        return false;
                                    }
                                }
                            }

                            //更好的做法是先检测原始数据和新数据是否相等，不相等时再进行更新，并且相同的数据不更新，麻烦。。
                            int result = statement.executeUpdate("DELETE FROM " + classTable.getTextTrim()
                                    + " WHERE id=" + id);
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
}