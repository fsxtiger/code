package kafka;

import kafka.exception.GlspMqRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;


/**
 * @Author shuoxuan.fang
 * @Date 2024/2/22
 **/
@Slf4j
public class GlspKafkaTemplate<K, V> extends KafkaTemplate<K, V> {

    @Resource
    private GlspKafKaConfig glspKafKaConfig;

    public GlspKafkaTemplate(ProducerFactory<K, V> producerFactory) {
        super(producerFactory, false);
    }

    @Override
    protected ListenableFuture<SendResult<K, V>> doSend(final ProducerRecord<K, V> producerRecord) {
        log.debug("begin send record");
        if (producerRecord.headers().lastHeader(Constants.ENV_KEY) != null) {
            throw new GlspMqRuntimeException(Constants.ENV_KEY + " is inner key");
        }

        if (!StringUtils.isEmpty(glspKafKaConfig.getEnv())) {
            log.debug("add env header:{}", glspKafKaConfig.getEnv());
            producerRecord.headers().add(Constants.ENV_KEY, glspKafKaConfig.getEnv().getBytes());
        }

        return super.doSend(producerRecord);
    }

}
