package com.hdmon.uaa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Uaa.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final ApplicationProperties.Microservices microservice = new ApplicationProperties.Microservices();

    public Microservices getMicroservices() {
        return microservice;
    }

    public static class Microservices {
        private String notificationApiUrl = "http://localhost:8080/notificationservice";
        private String otpApiUrl = "http://localhost:8080/notificationservice";

        public Microservices() {
        }

        public String getNotificationApiUrl() {
            return notificationApiUrl;
        }

        public void setNotificationApiUrl(String notificationApiUrl) {
            this.notificationApiUrl = notificationApiUrl;
        }

        public String getOtpApiUrl() {
            return otpApiUrl;
        }

        public void setOtpApiUrl(String otpApiUrl) {
            this.otpApiUrl = otpApiUrl;
        }
    }
}
