package io.jmix.petclinic.portal.config;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import io.jmix.restds.impl.RestAuthenticator;
import io.jmix.restds.impl.RestInvoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClient;

/**
 * Configuration to override the default RestInvoker with one that properly configures observability.
 * 
 * The default RestInvoker creates its own RestClient.Builder internally, which doesn't have access
 * to the Spring application context and therefore uses a NoOpObservationRegistry. 
 * 
 * This configuration creates a custom RestInvoker that:
 * 1. Gets the RestClient.Builder from the application context (which has the proper ObservationRegistry configured)
 * 2. Uses reflection to override the private restClient field in the parent class
 * 3. Ensures that REST calls are properly traced and observed
 */
@Configuration
public class RestInvokerWithObservationRegistryConfiguration {

    @Bean("restds_RestInvoker")
    @Primary
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public RestInvoker restInvoker(String dataStoreName) {
        return new RestInvokerWithObservationRegistrySet(dataStoreName);
    }

    public static class RestInvokerWithObservationRegistrySet extends RestInvoker {

        private final String dataStoreName;

        private RestAuthenticator authenticator;


        @Autowired
        private ApplicationContext applicationContext;


        public RestInvokerWithObservationRegistrySet(String dataStoreName) {
            super(dataStoreName);
            this.dataStoreName = dataStoreName;
        }

        /**
         * Override afterPropertiesSet to replace the RestClient with one that has proper observability.
         * 
         * The parent class creates a RestClient using RestClient.builder() directly, which doesn't
         * have access to the configured ObservationRegistry from the application context.
         * 
         * We use reflection to replace the private restClient field with one built using the
         * RestClient.Builder bean from the application context, which has the proper ObservationRegistry configured.
         */
        @Override
        public void afterPropertiesSet() {
            super.afterPropertiesSet();

            Environment environment = applicationContext.getEnvironment();

            String authenticatorBeanName = environment.getProperty(
                    dataStoreName + ".authenticator", DEFAULT_AUTHENTICATOR);

            authenticator = (RestAuthenticator) applicationContext.getBean(authenticatorBeanName);
            authenticator.setDataStoreName(dataStoreName);

            String baseUrl = environment.getRequiredProperty(dataStoreName + ".baseUrl");

            try {
                // Use reflection to access the private restClient field from parent class
                Field restClientField = RestInvoker.class.getDeclaredField("restClient");
                restClientField.setAccessible(true);

                // Get RestClient.Builder from application context - this bean has the proper ObservationRegistry configured
                // Unlike RestClient.builder() which creates a new instance with NoOpObservationRegistry
                RestClient.Builder restClientBuilder = applicationContext.getBean(RestClient.Builder.class);

                // Build RestClient with the same configuration as parent class but with observability enabled
                RestClient restClientWithObservationRegistry = restClientBuilder
                        .baseUrl(baseUrl)
                        .messageConverters(converters ->
                                converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)))
                        .requestInterceptor(authenticator.getAuthenticationInterceptor())
                        .build();

                // Replace the private restClient field with our observability-enabled RestClient
                // This ensures all REST calls made by the parent class methods will be traced
                restClientField.set(this, restClientWithObservationRegistry);

            } catch (Exception e) {
                throw new RuntimeException("Failed to override RestClient", e);
            }
        }
    }

}