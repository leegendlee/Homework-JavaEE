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
                if (!interceptor.attributeValue("name").isEmpty()) {
                    Class interceptorClass = Class.forName(interceptor.attributeValue("class"));
                    Object interceptorObj = interceptorClass.newInstance();

                    Method setAction = interceptorClass.getMethod("setAction", Element.class);
                    setAction.invoke(interceptorObj, this.action);

                    Method init = interceptorClass.getMethod("init", HttpServletRequest.class);
                    init.invoke(interceptorObj, this.req);

                    if (!interceptor.attributeValue("predo").isEmpty()) {
                        Method targetInterceptorMethod = interceptorClass.getMethod(interceptor.attributeValue("predo"));

                        Boolean interceptorResultPre = (Boolean) targetInterceptorMethod.invoke(interceptorObj);
                        if (!interceptorResultPre) {
                            Method writeInterceptor = interceptorClass.getMethod("finish", HttpServletRequest.class);
                            writeInterceptor.invoke(interceptorObj, this.req);

                            return null;
                        }

                        if (interceptor.attributeValue("afterdo") != null
                                && !interceptor.attributeValue("afterdo").isEmpty()) {
                            queueInterceptors.put(interceptor.attributeValue("afterdo"), interceptorObj);
                        }
                    }
                }
            }

            String actionResult = (String) methodProxy.invokeSuper(o, objects);

            //result需要写入
            if (!this.queueInterceptors.isEmpty()) {
                for (Map.Entry<String, Object> stringObjectEntry : this.queueInterceptors.entrySet()) {
                    String key = (String) stringObjectEntry.getKey();
                    Object interceptorObj = (Object) stringObjectEntry.getValue();

                    Class interceptorClass = Class.forName(interceptorObj.getClass().getName());
                    Method setActionResult = interceptorClass.getMethod("setActionResult", String.class);
                    setActionResult.invoke(interceptorObj, actionResult);

                    Method targetInterceptorMethod = interceptorClass.getMethod(key);
                    targetInterceptorMethod.invoke(interceptorObj);

                    Method writeInterceptor = interceptorClass.getMethod("finish", HttpServletRequest.class);
                    writeInterceptor.invoke(interceptorObj, this.req);

                    this.queueInterceptors.remove(key);
                }
            }
        } else {
            methodProxy.invokeSuper(o, objects);
        }

        return null;
    }
}