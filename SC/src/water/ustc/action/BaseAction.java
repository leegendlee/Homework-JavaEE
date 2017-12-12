package water.ustc.action;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by leegend on 2017/12/11.
 */
public class BaseAction {
    public String newAction(Element action, HttpServletRequest req, HttpServletResponse res) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, DocumentException, TransformerException, IOException, ServletException, InvocationTargetException {
        Class targetActionClass = Class.forName(action.attributeValue("class"));
        Method targetActionMethod = targetActionClass.getMethod(action.attributeValue("method"), HttpServletRequest.class);
        Object targetActionObj = targetActionClass.newInstance();

        //根据di.xml查找DI
//        File diXml = new File(req.req.getServletContext().getRealPath("/WEB-INF/classes/di.xml"));
//        Document diDocument = (new SAXReader()).read(diXml);
//        Element diRootElement = diDocument.getRootElement();
//
//        List<Element> diBeans = diRootElement.elements("bean");
//        List<Element> diMappings = diRootElement.element("di-mapping").elements("field");
//
//        for (Element field : diMappings) {
//            if (Objects.equals(field.attributeValue("name"), action.attributeValue("name"))) {
//                for (Element diBean : diBeans) {
//                    if (Objects.equals(diBean.attributeValue("name"), field.attributeValue("bean-ref"))) {
//                        Object beanRef = (Class.forName(diBean.attributeValue("class"))).newInstance();
//
//                        BeanInfo actionBI = Introspector.getBeanInfo(targetActionObj.getClass(), Object.class);
//                        PropertyDescriptor[] actionProps = actionBI.getPropertyDescriptors();
//                        for (PropertyDescriptor actionProp : actionProps) {
//                            if (Objects.equals(actionProp.getName(), field.attributeValue("bean-name"))) {
//                                //action
//                                actionProp.getWriteMethod().invoke(targetActionObj, beanRef);
//                            }
//                        }
//                    }
//                }
//            }
//        }

        //没有找到DI，则进行action的转发
        //action
        String methodResult = (String) targetActionMethod.invoke(targetActionObj, req);

        //action result工作
        for (Iterator k = action.elementIterator("result"); k.hasNext(); ) {
            Element result = (Element) k.next();
            if (Objects.equals(result.attributeValue("name"), methodResult)) {

                String pattern = ".*_view\\.xml";
                String value = result.attributeValue("value");
                if (Pattern.matches(pattern, value)) {

                    TransformerFactory factory = TransformerFactory.newInstance();

                    Source xslPage = new StreamSource(req.getServletContext().getRealPath("/pages/success_view.xsl"));
                    Transformer transformer = factory.newTransformer(xslPage);

                    File xmlPage = new File(req.getServletContext().getRealPath("/" + value));
                    File htmlPage = new File(req.getServletContext().getRealPath("/pages/success_view.html"));
                    Source source = new StreamSource(xmlPage);
                    Result result1 = new StreamResult(htmlPage);

                    transformer.transform(source, result1);
                    req.getRequestDispatcher("/pages/success_view.html").forward(req, res);
                } else {
                    RequestDispatcher dispatcher = req.getRequestDispatcher(value);
                    dispatcher.forward(req, res);
                }

                break;
            } else {
                PrintWriter writer = res.getWriter();
                writer.write("No Response");

                return null;
            }
        }

        return methodResult;
    }
}
