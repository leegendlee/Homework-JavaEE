package water.ustc.action;

/**
 * Created by leegend on 2017/12/3.
 */

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;

import water.ustc.interceptor.InterceptorProxy;

public class SimpleController extends HttpServlet {
    public static String basePath = null;

    public SimpleController() {
    }

    private void initAttrs() {
        basePath = getServletContext().getRealPath("/");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=utf-8");
        PrintWriter writer = res.getWriter();
        String origin = "<html><head><title>water.ustc.SimpleController</title>" +
                "</head><body>欢迎使用SimpleController!</body></html>";
        writer.print(origin);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        String url = req.getRequestURI();
//
//        System.out.println(url);
//        if (!Pattern.matches(".*\\.sc", url) && !Pattern.matches(".*\\.html", url)) {
//            System.out.println(false);
//            this.doPost(req, res);
//
//            return;
//        }
    }

    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (basePath == null) {
            this.initAttrs();
        }

        try {
            this.parse(req, res);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    private void parse(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, TransformerException, IntrospectionException {
        String url = req.getRequestURI();
        String reqAction = url.substring(url.indexOf("/") + 1, url.lastIndexOf("."));

        File inputXml = new File(getServletContext().getRealPath("/WEB-INF/classes/controller.xml"));
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputXml);
        Element rootController = document.getRootElement();

        Element controller = rootController.element("controller");

        Boolean identify = false;
        for (Iterator j = controller.elementIterator("action"); j.hasNext(); ) {
            Element action = (Element) j.next();

            if (Objects.equals(action.attributeValue("name"), reqAction)) {
                InterceptorProxy interceptorProxy = new InterceptorProxy(action, req);

                for (Element interceptorRef : action.elements("interceptor-ref")) {
                    for (Element interceptor : rootController.elements("interceptor")) {
                        if (interceptorRef.attributeValue("name").equals(interceptor.attributeValue("name"))) {
                            interceptorProxy.addInterceptors(interceptor);
                        }
                    }
                }

                BaseAction baseAction = (BaseAction) interceptorProxy.getInstance(new BaseAction());
                baseAction.newAction(action, req, res);

                identify = true;
                break;
            }
        }

        if (!identify) {
            PrintWriter writer = res.getWriter();
            writer.write("Cannot Identify");
        }
    }
}