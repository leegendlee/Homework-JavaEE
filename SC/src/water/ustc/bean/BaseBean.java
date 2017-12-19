package water.ustc.bean;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * Created by leegend on 2017/12/16.
 */
public abstract class BaseBean implements Dispatcher {
    protected static Object VALUE;

    public void lazyLoad(Map<String, Object> props) {
        if (!props.isEmpty()) {
            try {
                BeanInfo thisBI = Introspector.getBeanInfo(this.getClass(), Object.class);
                PropertyDescriptor[] thisProps = thisBI.getPropertyDescriptors();
                for (PropertyDescriptor thisProp : thisProps) {
                    if (props.containsKey(thisProp.getName())) {
                        //会有nullPointer的错误
                        //Q：如何对非集合、非对象的属性进行延迟加载
                        //Q：如何不使用动态类编写通用的适应所有Bean的延迟加载方法
                        thisProp.getWriteMethod().invoke(this, String.valueOf(this.lazyLoadDispatcher(props.get(thisProp.getName()))));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object lazyLoadDispatcher(Object value) {
        VALUE = value;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.getClass());
        Object obj = null;
        try {
            obj = (Class.forName(this.getClass().getName())).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return enhancer.create(this.getClass(), (Callback) obj);
    }

    @Override
    public Object loadObject() throws Exception {
        System.out.println("lazyload....");
        return VALUE;
    }
}
