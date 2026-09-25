package com.arun.Restaurantbackend.Config;

import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.servlet.MultipartConfigElement;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.unit.DataSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BeanCreation {

    @Value("${api_Url}")
    String api_url;

    @Value("${CLOUDINARY_CLOUD_NAME}")
    String cloudName;

    @Value("${CLOUDINARY_API_KEY}")
    String apiKey;

    @Value("${CLOUDINARY_API_SECRET}")
    String secretKey;

    @Value("${RAZOR_PAY_KEY}")
    String razorKeyId;

    @Value("${RAZOR_PAY_SECRET}")
    String razorSecretKey;


    // =====================================================
    // Existing Beans
    // =====================================================

    @Bean
    ModelMapper creationmodelmapper() {
        return new ModelMapper();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }



    @Bean
    Cloudinary cloudinary() {

        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", secretKey);

        return new Cloudinary(config);
    }

    @Bean
    RazorpayClient razorpayClient() throws RazorpayException {

        return new RazorpayClient(
                razorKeyId,
                razorSecretKey
        );
    }


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // =====================================================
    // Kafka Producer
    // =====================================================

    @Bean
    public ProducerFactory<String, EmailEvent> producerFactory() {

        Map<String, Object> props = new HashMap<>();

        // Kafka broker
        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        // Key: String
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        // Value: EmailEvent -> JSON
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JacksonJsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(props);
    }


    @Bean
    public KafkaTemplate<String, EmailEvent> kafkaTemplate(
            ProducerFactory<String, EmailEvent> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }


    // =====================================================
    // Kafka Consumer
    // =====================================================

    @Bean
    public ConsumerFactory<String, EmailEvent> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        // Kafka broker
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        // Consumer group
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "email-consumer"
        );

        // If no committed offset exists
        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        // Key: Kafka bytes -> String
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        /*
         * We don't depend only on the property here.
         * We explicitly provide the typed deserializer below.
         */
        JacksonJsonDeserializer<EmailEvent> deserializer =
                new JacksonJsonDeserializer<>(EmailEvent.class);

        deserializer.addTrustedPackages(
                "com.arun.Restaurantbackend.DTO"
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }


    // =====================================================
    // Kafka Listener Container
    // =====================================================

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, EmailEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, EmailEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, EmailEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    @Bean
    public NewTopic emailTopic() {
        return TopicBuilder.name("email-event")
                .partitions(3)
                .replicas(1)
                .build();
    }



    @Bean
    public OpenAPI openAPI(){

        return new OpenAPI()
                .info(
                        new Info().title("ProximityEats Apis")
                                .description("By Arun Rajput")
                )   .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .tags(List.of(
                        new Tag().name("1.  Authenication Apis"),
                        new Tag().name("2. Password Apis"),
                        new Tag().name("3. User  Apis"),
                        new Tag().name("4. Wallet Apis"),
                        new Tag().name("5. User Cart Apis"),
                        new Tag().name("6. Menu Apis"),
                        new Tag().name("7. Admin Apis"),
                        new Tag().name("8. Manager Apis"),
                        new Tag().name("9. Menu Apis"),
                        new Tag().name("10. Restaurant Apis"),
                        new Tag().name("11. Order Status Apis"),
                        new Tag().name("12. Delivery Boy Apis "),
                        new Tag().name("13. BundleOrder Apis"),
                        new Tag().name("14. Group Apis"),
                        new Tag().name("15. Subscription Apis")

                ));
    }


}