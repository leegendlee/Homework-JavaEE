package water.ustc.interceptor;

import org.dom4j.Element;
import water.ustc.interfaces.InterfaceInterceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by leegend on 2017/12/16.
 */
public class BaseInterceptor implements InterfaceInterceptor {
    protected Element action;
    protected String actionResult;

    @Override
    public void init(HttpServletRequest req) {

    }

    @Override
    public void finish(HttpServletRequest req) {

    }

    public void setAction(Element action) {
        this.action = action;
    }

    public void setActionResult() {
        this.actionResult = actionResult;
    }
}
