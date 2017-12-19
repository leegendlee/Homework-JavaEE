package water.ustc.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by leegend on 2017/12/14.
 */
public class BaseFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String uri = req.getRequestURI();

        if (Objects.equals(uri, "/")) {
            req.getRequestDispatcher("index.jsp").forward(req, res);
        } else if (!Pattern.matches(".*\\.sc.*", uri)) {
            res.setStatus(404);
            PrintWriter writer = res.getWriter();
            writer.write("<html><body>" +
                    "Page Not Exist" +
                    "</body></html>");
        } else {
            filterChain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }
}