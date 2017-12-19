package water.ustc.initiator;

import org.dom4j.Element;
import java.util.List;

/**
 * Created by leegend on 2017/12/15.
 */
public class ORMappingInitiator {
    private static String DRIVER_CLASS;
    private static String URL_PATH;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    public ORMappingInitiator() {
        try {
            if (BaseInitiator.getOrMappingXmlRoot() != null) {
                this.setDriverClass("driver_class");
                this.setUrlPath("url_path");
                this.setDbUsername("db_username");
                this.setDbPassword("db_password");
            }

            if (DRIVER_CLASS == null || URL_PATH == null || DB_USERNAME == null) {
                throw new Exception("DataBase Not Set");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseORMappingXmlRoot(String propertyName) {
        List<Element> properties = BaseInitiator.getOrMappingXmlRoot().element("jdbc").elements("property");

        if (properties != null) {
            for (Element property : properties) {
                Element elementName = property.element("name");

                if (elementName != null) {
                    String valueName = property.element("name").getTextTrim();
                    if (valueName.equals(propertyName)) {
                        Element elementValue = property.element("value");

                        if (elementValue != null) {
                            String value = property.element("value").getTextTrim();
                            if (!value.isEmpty())
                                return value;
                        }
                    }
                }
            }
        }

        return null;
    }

    private void setDriverClass(String driverClass) {
        DRIVER_CLASS = this.parseORMappingXmlRoot(driverClass);
    }

    private void setUrlPath(String urlPath) {
        URL_PATH = this.parseORMappingXmlRoot(urlPath);
    }

    private void setDbUsername(String dbUsername) {
        DB_USERNAME = this.parseORMappingXmlRoot(dbUsername);
    }

    private void setDbPassword(String dbPassword) {
        String passWord = this.parseORMappingXmlRoot(dbPassword);
        DB_PASSWORD = passWord == null ? "" : passWord;
    }

    public static String getDriverClass() {
        return DRIVER_CLASS;
    }

    public static String getUrlPath() {
        return URL_PATH;
    }

    public static String getDbUsername() {
        return DB_USERNAME;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }
}