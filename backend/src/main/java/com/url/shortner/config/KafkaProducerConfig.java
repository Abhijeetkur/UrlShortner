package com.url.shortner.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol:#{null}}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.ssl.truststore.location:#{null}}")
    private String truststoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.password:#{null}}")
    private String truststorePassword;

    @Value("${spring.kafka.properties.ssl.keystore.location:#{null}}")
    private String keystoreLocation;

    @Value("${spring.kafka.properties.ssl.keystore.password:#{null}}")
    private String keystorePassword;

    @Value("${spring.kafka.properties.ssl.keystore.type:#{null}}")
    private String keystoreType;

    @Value("${spring.kafka.properties.ssl.key-password:#{null}}")
    private String keyPassword;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // SSL configuration (only if security.protocol is set)
        if (securityProtocol != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);
            if (keystoreType != null) {
                props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, keystoreType);
            }
        }

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
