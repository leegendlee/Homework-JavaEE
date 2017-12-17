package water.ustc.interceptor;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import water.ustc.interfaces.InterfaceInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leegend on 2017/12/4.
 */
public class LogInterceptor extends BaseInterceptor {
    private Element actionLog;
    private Document documentLog;

    private HttpServletRequest req;

    @Override
    public void init(HttpServletRequest req) {
        try {
            if (this.action == null) {
                throw new Exception("Null Action");
            }

            this.req = req;

            File logXml = new File(this.req.getServletContext().getRealPath("/WEB-INF/log.xml"));
            SAXReader saxReaderLog = new SAXReader();
            this.documentLog = saxReaderLog.read(logXml);

            Element rootLog = documentLog.getRootElement();

            this.actionLog = rootLog.addElement("action");

            actionLog.addElement("name");
            actionLog.addElement("s-time");
            actionLog.addElement("e-time");
            actionLog.addElement("result");

            actionLog.element("name").setText(this.action.attributeValue("name"));
            actionLog.element("result").setText("failure");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish(HttpServletRequest req) {
        try {
            this.write();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write() throws IOException {
        //可以把xml写入的格式美化一下
        FileWriter fileWriter = new FileWriter(this.req.getServletContext().getRealPath("/WEB-INF/log.xml"));
        XMLWriter xmlWriter = new XMLWriter(fileWriter);
        xmlWriter.write(this.documentLog);
        xmlWriter.close();
    }

    private String calcCurrTime() {
        Date date = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return date_format.format(date);
    }

    public boolean preAction() {
        this.actionLog.element("s-time").setText(this.calcCurrTime());
        this.actionLog.element("e-time").setText(this.calcCurrTime());

        return true;
    }

    public void afterAction() throws IOException {
        this.actionLog.element("e-time").setText(this.calcCurrTime());
        if (this.actionResult != null) {
            this.actionLog.element("result").setText(actionResult);
        }
    }
}
