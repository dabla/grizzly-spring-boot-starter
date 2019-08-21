package be.dabla.boot.grizzly.jsp;

import be.dabla.boot.grizzly.config.GrizzlyProperties;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.jasper.servlet.JspServlet;
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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
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
@ConditionalOnClass(JspServlet.class)
@AutoConfigureAfter({ServletWebServerFactoryAutoConfiguration.class})
public class JspServletAutoConfiguration {
    public static final String DEFAULT_JSP_SERVLET_BEAN_NAME = "jspServlet";
    public static final String DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME = "jspServletRegistration";

    @Order(2147483637)
    private static class JspServletRegistrationCondition extends SpringBootCondition {
        private JspServletRegistrationCondition() {}

        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            ConditionOutcome outcome = checkDefaultDispatcherName(beanFactory);
            return !outcome.isMatch() ? outcome : checkServletRegistration(beanFactory);
        }

        private ConditionOutcome checkDefaultDispatcherName(ConfigurableListableBeanFactory beanFactory) {
            List<String> servlets = asList(beanFactory.getBeanNamesForType(JspServlet.class, false, false));
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
    @Conditional({JspServletAutoConfiguration.JspServletRegistrationCondition.class})
    @ConditionalOnClass({ServletRegistration.class})
    @EnableConfigurationProperties({GrizzlyProperties.class})
    @Import({JspServletAutoConfiguration.JspServletConfiguration.class})
    protected static class DispatcherServletRegistrationConfiguration {
        private final GrizzlyProperties grizzlyProperties;

        public DispatcherServletRegistrationConfiguration(GrizzlyProperties grizzlyProperties) {
            this.grizzlyProperties = grizzlyProperties;
        }

        @Bean(name = DEFAULT_JSP_SERVLET_REGISTRATION_BEAN_NAME)
        @ConditionalOnBean(value = JspServlet.class, name = DEFAULT_JSP_SERVLET_BEAN_NAME)
        public ServletRegistrationBean jspServletRegistration(JspServlet jspServlet) {
            ServletRegistrationBean registration = new ServletRegistrationBean(jspServlet, grizzlyProperties.getJsp().getUrlMapping());
            registration.setName(DEFAULT_JSP_SERVLET_BEAN_NAME);
            return registration;
        }
    }

    @Configuration
    @Conditional({JspServletAutoConfiguration.JspServletRegistrationCondition.class})
    @ConditionalOnClass({ServletRegistration.class})
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
        public JspServlet jspServlet(ServletContext servletContext,
                                     JspFactory jspFactory,
                                     InstanceManager instanceManager) {
            setDefaultFactory(jspFactory);
            JspServlet jspServlet = new JspServlet();
            servletContext.setAttribute(InstanceManager.class.getName(), instanceManager);
            servletContext.setAttribute(SERVLET_CLASSPATH, getProperty("java.class.path"));
            return jspServlet;
        }
    }
}

