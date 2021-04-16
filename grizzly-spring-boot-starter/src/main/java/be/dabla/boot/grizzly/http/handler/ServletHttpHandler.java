package be.dabla.boot.grizzly.http.handler;

import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import static org.glassfish.grizzly.http.server.HttpHandlerRegistration.builder;

public class ServletHttpHandler extends org.glassfish.grizzly.servlet.ServletHandler {
    public ServletHttpHandler(WebappContext webappContext, ServletRegistrationBean servletRegistration) {
        super(new ServletConfigImpl(webappContext, servletRegistration.getServletName()));
        setContextPath(webappContext.getContextPath());
        setServletInstance(servletRegistration.getServlet());
    }

    @Override
    public String getName() {
        return getServletConfig().getServletName();
    }

    public ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }

    public HttpHandlerRegistration[] getRegistrations() {
        ServletRegistration registration = getServletContext().getServletRegistration(getName());
        return registration
                .getMappings()
                .stream()
                .map(mapping -> builder().contextPath(getServletContext().getContextPath()).urlPattern(mapping).build())
                .toArray(HttpHandlerRegistration[]::new);
    }

    private static class ServletConfigImpl extends org.glassfish.grizzly.servlet.ServletConfigImpl {
        private ServletConfigImpl(WebappContext webappContext, String name) {
            super(webappContext);
            setServletName(name);
        }
    }
}
