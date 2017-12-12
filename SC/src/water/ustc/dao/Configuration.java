package water.ustc.dao;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * Created by leegend on 2017/12/6.
 */
public class Configuration {
    protected Element rootElement = null;

    public Configuration() throws DocumentException {
        if (this.rootElement == null) {
            //路径问题
            File orMappingXml = new File("/Users/leegend/Downloads/UseSC/out/artifacts/SC_war_exploded/WEB-INF/classes/or_mapping.xml");
            SAXReader saxReader1 = new SAXReader();
            Document orMappingDocument = saxReader1.read(orMappingXml);
            this.rootElement = orMappingDocument.getRootElement();
        }
    }
}