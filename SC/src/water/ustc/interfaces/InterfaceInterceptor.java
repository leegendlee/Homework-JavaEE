package water.ustc.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Element;

/**
 * Created by leegend on 2017/12/15.
 */
public interface InterfaceInterceptor {
    public abstract void init(HttpServletRequest req, Object... obj);

    public abstract void finish(HttpServletRequest req, Object... obj);
}
