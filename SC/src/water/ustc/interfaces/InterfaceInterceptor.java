package water.ustc.interfaces;

import org.dom4j.Element;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by leegend on 2017/12/15.
 */
public interface InterfaceInterceptor {
    public abstract void init(HttpServletRequest req);

    public abstract void finish(HttpServletRequest req);

    public abstract void setAction(Element action);

    public abstract void setActionResult(String actionResult);
}