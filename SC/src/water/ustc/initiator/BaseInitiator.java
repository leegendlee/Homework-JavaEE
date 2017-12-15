package water.ustc.initiator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.Set;

/**
 * Created by leegend on 2017/12/15.
 */
public class BaseInitiator extends HttpServlet implements ServletContainerInitializer {
    private static String ROOT_PATH;

    private static Element CONTROLLER_XML_ROOT;
    private static Element OR_MAPPING_XML_ROOT;
    private static Element DI_XML_ROOT;

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        setRootPath(getServletContext().getRealPath("/"));

        try {
            setControllerXmlRoot(ROOT_PATH + "WEB-INF/classes/controller.xml");
            setOrMappingXmlRoot(ROOT_PATH + "WEB-INF/classes/or_mapping.xml");
            setDiXmlRoot(ROOT_PATH + "WEB-INF/classes/di.xml");

            if (CONTROLLER_XML_ROOT == null || OR_MAPPING_XML_ROOT == null || DI_XML_ROOT == null) {
                throw new Exception("XML File Not Initiated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element setElementRoot(String relativePath) {
        Document document = null;
        try {
            File xml = new File(getRootPath() + relativePath);
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        if (document == null) {
            return null;
        }
        return document.getRootElement();
    }

    private static void setRootPath(String rootPath) {
        ROOT_PATH = rootPath;
    }

    private static void setControllerXmlRoot(String controllerXmlPath) {
        CONTROLLER_XML_ROOT = setElementRoot(controllerXmlPath);
    }

    private static void setOrMappingXmlRoot(String orMappingXmlPath) {
        OR_MAPPING_XML_ROOT = setElementRoot(orMappingXmlPath);

    }

    private static void setDiXmlRoot(String diXmlPath) {
        DI_XML_ROOT = setElementRoot(diXmlPath);
    }

    private static String getRootPath() {
        return ROOT_PATH;
    }

    public static Element getControllerXmlRoot() {
        return CONTROLLER_XML_ROOT;
    }

    public static Element getOrMappingXmlRoot() {
        return OR_MAPPING_XML_ROOT;
    }

    public static Element getDiXmlRoot() {
        return DI_XML_ROOT;
    }
}
