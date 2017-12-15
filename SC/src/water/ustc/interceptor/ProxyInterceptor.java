package water.ustc.interceptor;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by leegend on 2017/12/11.
 */
public class ProxyInterceptor implements MethodInterceptor {
    private List<Element> interceptors = new ArrayList<Element>();
    private Element action;
    private HttpServletRequest req;
    private Map<String, Object> queueInterceptors = new HashMap<String, Object>();

    public ProxyInterceptor(Element action, HttpServletRequest req) {
        this.action = action;
        this.req = req;
    }

    public void addInterceptors(Element interceptor) {
        this.interceptors.add(interceptor);
    }

    public Object getInstance(Object interceptorProxy) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interceptorProxy.getClass());
        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (!this.interceptors.isEmpty()) {
            for (Element interceptor : this.interceptors) {

//

                if (!interceptor.attributeValue("name").isEmpty()) {
                    Class interceptorClass = Class.forName(interceptor.attributeValue("class"));
                    Object interceptorObj = interceptorClass.newInstance();

                    Method initInterceptor = interceptorClass.getMethod("init", Element.class, HttpServletRequest.class);
                    initInterceptor.invoke(interceptorObj, this.action, this.req);

                    if (!interceptor.attributeValue("predo").isEmpty()) {
                        Method targetInterceptorMethod = interceptorClass.getMethod(interceptor.attributeValue("predo"));

                        Boolean interceptorResultPre = (Boolean) targetInterceptorMethod.invoke(interceptorObj);
                        if (!interceptorResultPre) {
                            Method writeInterceptor = interceptorClass.getMethod("write");
                            writeInterceptor.invoke(interceptorObj);

                            return null;
                        }

                        if (!interceptor.attributeValue("afterdo").isEmpty()) {
                            queueInterceptors.put(interceptor.attributeValue(interceptor.attributeValue("afterdo")), interceptorObj);
                        }
                    }
                }
            }

            String actionResult = (String) methodProxy.invokeSuper(o, objects);

            if (!this.queueInterceptors.isEmpty()) {
                for (Iterator i = this.queueInterceptors.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) i.next();
                    String key = (String) entry.getKey();
                    Object interceptorObj = (Object) entry.getValue();

                    Class interceptorClass = Class.forName(interceptorObj.getClass().getName());
                    Method targetInterceptorMethod = interceptorClass.getMethod(key, String.class);
                    targetInterceptorMethod.invoke(interceptorObj, actionResult);

                    Method writeInterceptor = interceptorClass.getMethod("finish");
                    writeInterceptor.invoke(interceptorObj);

                    this.queueInterceptors.remove(key);
                }
            }
        } else {
            methodProxy.invokeSuper(o, objects);
        }

        return null;
    }
}