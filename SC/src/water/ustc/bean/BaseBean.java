package water.ustc.bean;

import net.sf.cglib.proxy.Dispatcher;
import java.util.List;

/**
 * Created by leegend on 2017/12/16.
 */
public abstract class BaseBean implements Dispatcher {
    public void lazyLoad(List<String> propNames) {
    }

    @Override
    public Object loadObject() throws Exception {
        return null;
    }
}
