package water.ustc.action;

/**
 * Created by leegend on 2017/12/3.
 */

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import org.dom4j.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import water.ustc.initiator.BaseInitiator;
import water.ustc.interceptor.ProxyInterceptor;

public class SimpleController extends HttpServlet {
    public static Element action = null;
    public static ProxyInterceptor proxyInterceptor = null;
    public static String uri = "";
    public static Map<String, String> params = new HashMap<String, String>();

    public SimpleController() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=utf-8");
        PrintWriter writer = res.getWriter();
        String origin = "<html><head><title>water.ustc.SimpleController</title>" +
                "</head><body>欢迎使用SimpleController!</body></html>";
        writer.print(origin);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            BaseAction baseAction = (BaseAction) proxyInterceptor.getInstance(new BaseAction());
            baseAction.newAction(action, req, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            uri = request.getRequestURI();

            if (params.isEmpty()) {
//                params = request.getParameterMap();
            }

            this.parse(request, response);
            this.intercept(request, response);

            String method = request.getMethod();
            if (method.equals("GET")) {
                this.doGet(request, response);
            } else if (method.equals("POST")) {
                this.doPost(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, TransformerException, IntrospectionException {
        Element rootController = BaseInitiator.getControllerXmlRoot();
        Element controller = rootController.element("controller");

        String reqAction = this.calcActionName(uri);
        Boolean identify = false;

        for (Iterator j = controller.elementIterator("action"); j.hasNext(); ) {
            Element elementAction = (Element) j.next();

            if (Objects.equals(elementAction.attributeValue("name"), reqAction)) {
                action = elementAction;
                proxyInterceptor = new ProxyInterceptor(action, req);

                for (Element interceptorRef : action.elements("interceptor-ref")) {
                    for (Element interceptor : rootController.elements("interceptor")) {
                        if (interceptorRef.attributeValue("name").equals(interceptor.attributeValue("name"))) {
                            proxyInterceptor.addInterceptors(interceptor);
                        }
                    }
                }

                identify = true;
                break;
            }
        }

        if (!identify) {
            PrintWriter writer = res.getWriter();
            writer.write("Cannot Identify");
        }
    }

    private void intercept(HttpServletRequest req, HttpServletResponse res) throws TransformerException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, IntrospectionException, IOException, ClassNotFoundException, ServletException, DocumentException {
        //需要适配get，post更为通用
    }

    private String calcActionName(String uri) {
        if (!Pattern.matches(".*\\.sc.*", uri)) {
            return "";
        }

        return uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
    }
}