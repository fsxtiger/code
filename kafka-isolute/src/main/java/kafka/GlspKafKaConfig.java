package kafka;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.BatchInterceptor;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

/**
 * @Author shuoxuan.fang
 * @Date 2024/2/23
 **/

@ConfigurationProperties(
        prefix = Constants.PREFIX_NAME
        )
public class GlspKafKaConfig {

    private String env;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Bean
    @ConditionalOnProperty(
            value= Constants.ENV_NAME
    )
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
                                             ProducerListener<Object, Object> kafkaProducerListener,
                                             ObjectProvider<RecordMessageConverter> messageConverter) {
        KafkaTemplate<Object, Object> kafkaTemplate = new GlspKafkaTemplate<>(kafkaProducerFactory);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        return kafkaTemplate;
    }

    @Bean
    public RecordInterceptor<?, ?> recordInterceptor(){
        return new DefaultRecordInterceptor<>();
    }

    @Bean
    public BatchInterceptor<?, ?> batchInterceptor(){
        return new DefaultBatchInterceptor<>();
    }

    @Bean
    @ConditionalOnProperty(
            value= Constants.ENV_NAME
    )
    public KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer annotationEnhancer(){
        return new DefaultAnnotationEnhancer();
    }
}
