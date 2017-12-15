package water.ustc.initiator;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Set;

/**
 * Created by leegend on 2017/12/15.
 */
public class BaseInitiator extends HttpServlet implements ServletContainerInitializer {
    //初始化注册的常量

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

    }
}
