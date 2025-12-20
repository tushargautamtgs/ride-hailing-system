//package com.tushargautamtgs.matching_service.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//
//@Configuration
//public class KafkaConfig {
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String,Object>
//    kafkaListenerContainerFactory(ConsumerFactory<String,Object> factory){
//        ConcurrentKafkaListenerContainerFactory<String,Object> f =
//        new ConcurrentKafkaListenerContainerFactory<>();
//
//        f.setConsumerFactory(factory);
//        return f;
//    }
//}
