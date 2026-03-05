package com.url.shortner.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:analytics-group}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

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
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

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

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}