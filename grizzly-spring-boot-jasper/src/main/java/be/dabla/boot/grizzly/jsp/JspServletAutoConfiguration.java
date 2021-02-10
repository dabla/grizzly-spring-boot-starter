package be.dabla.boot.grizzly.jsp;

import be.dabla.boot.grizzly.config.GrizzlyProperties;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static be.dabla.boot.grizzly.jsp.Constants.DEFAULT_JSP_SERVLET_BEAN_NAME;
import static be.dabla.boot.grizzly.jsp.Constants.DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME;
import static java.lang.Class.forName;
import static java.lang.System.getProperty;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static javax.servlet.jsp.JspFactory.setDefaultFactory;
import static org.apache.jasper.Constants.SERVLET_CLASSPATH;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.Style.QUOTE;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.forCondition;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@AutoConfigureOrder(-2147483648)
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
@AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)
public class JspServletAutoConfiguration {
    @Order(2147483637)
    private static class JspServletRegistrationCondition extends SpringBootCondition {
        private JspServletRegistrationCondition() {}

        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            ConditionOutcome outcome = checkDefaultDispatcherName(beanFactory);
            return !outcome.isMatch() ? outcome : checkServletRegistration(beanFactory);
        }

        private ConditionOutcome checkDefaultDispatcherName(ConfigurableListableBeanFactory beanFactory) {
            List<String> servlets = asList(beanFactory.getBeanNamesForType(HttpServlet.class, false, false));
            boolean containsDispatcherBean = beanFactory.containsBean(DEFAULT_JSP_SERVLET_BEAN_NAME);
            return containsDispatcherBean && !servlets.contains(DEFAULT_JSP_SERVLET_BEAN_NAME) ? noMatch(startMessage().found("non jsp servlet").items(new Object[]{DEFAULT_JSP_SERVLET_BEAN_NAME})) : match();
        }

        private ConditionOutcome checkServletRegistration(ConfigurableListableBeanFactory beanFactory) {
            Builder message = startMessage();
            List<String> registrations = asList(beanFactory.getBeanNamesForType(ServletRegistrationBean.class, false, false));
            boolean containsDispatcherRegistrationBean = beanFactory.containsBean(DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME);
            if (registrations.isEmpty()) {
                return containsDispatcherRegistrationBean ? noMatch(message.found("non servlet registration bean").items(new Object[]{DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME})) : match(message.didNotFind("servlet registration bean").atAll());
            }
            if (registrations.contains(DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME)) {
                return noMatch(message.found("servlet registration bean").items(new Object[]{DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME}));
            }
            return containsDispatcherRegistrationBean ? noMatch(message.found("non servlet registration bean").items(new Object[]{DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME})) : match(message.found("servlet registration beans").items(QUOTE, registrations).append("and none is named ").append(DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME));
        }

        private Builder startMessage() {
            return forCondition("JspServlet Registration", new Object[0]);
        }
    }

    @Configuration
    @Conditional(JspServletAutoConfiguration.JspServletRegistrationCondition.class)
    @ConditionalOnClass(ServletRegistration.class)
    @Import(JspServletAutoConfiguration.JspServletConfiguration.class)
    protected static class JspServletRegistrationConfiguration {
        @Bean(name = DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME)
        @ConditionalOnBean(value = HttpServlet.class, name = DEFAULT_JSP_SERVLET_BEAN_NAME)
        public ServletRegistrationBean jspServletRegistration(HttpServlet jspServlet, GrizzlyProperties grizzlyProperties) {
            ServletRegistrationBean registration = new ServletRegistrationBean(jspServlet, grizzlyProperties.getJsp().getUrlMapping());
            registration.setName(DEFAULT_JSP_SERVLET_BEAN_NAME);
            return registration;
        }
    }

    @Configuration
    @Conditional(JspServletAutoConfiguration.JspServletRegistrationCondition.class)
    @ConditionalOnClass(ServletRegistration.class)
    protected static class JspServletConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public JspFactory jspFactory() {
            return new JspFactoryImpl();
        }

        @Bean
        @ConditionalOnMissingBean
        public InstanceManager instanceManager() {
            return new SimpleInstanceManager();
        }

        @Bean(name = DEFAULT_JSP_SERVLET_BEAN_NAME)
        @ConditionalOnMissingBean
        public HttpServlet jspServlet(GrizzlyProperties grizzlyProperties,
                                      ServletContext servletContext,
                                      JspFactory jspFactory,
                                      InstanceManager instanceManager) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            setDefaultFactory(jspFactory);
            HttpServlet jspServlet = HttpServlet.class.cast(forName(grizzlyProperties.getJsp().getServlet()).newInstance());
            servletContext.setAttribute(InstanceManager.class.getName(), instanceManager);
            servletContext.setAttribute(SERVLET_CLASSPATH, getProperty("java.class.path"));
            if (servletContext.getAttribute("javax.servlet.context.tempdir") == null) {
                servletContext.setAttribute("javax.servlet.context.tempdir", new File(getTempDirectory(grizzlyProperties)));
            }
            return jspServlet;
        }

        private static String getTempDirectory(GrizzlyProperties grizzlyProperties) {
            return ofNullable(grizzlyProperties.getJsp().getTemporaryDirectory())
                  .orElseGet(() -> ofNullable(getProperty("scratchdir"))
                  .orElseGet(() -> ofNullable(getProperty("java.io.tmpdir"))
                  .orElseGet(() -> get(".").toAbsolutePath().normalize().toString())));
        }
    }
}

