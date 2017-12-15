package water.ustc.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

/**
 * Created by leegend on 2017/12/15.
 */
public class BaseListener extends HttpServlet implements ServletContextListener{
    //初始化需要加载，结束需要销毁

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
