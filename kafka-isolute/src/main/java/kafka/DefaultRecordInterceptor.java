package kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.listener.RecordInterceptor;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author shuoxuan.fang
 * @Date 2024/2/26
 **/
@Slf4j
public class DefaultRecordInterceptor<K, V> implements RecordInterceptor<K, V> {

    @Resource
    private GlspKafKaConfig glspKafKaConfig;

    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> record, Consumer<K, V> consumer) {

        ConsumerRecord<K, V> res = intercept(record);
        if (res == null) {
            log.debug("record = {} not belong this env = {}", record, glspKafKaConfig.getEnv());
            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            // TODO
            // 以及自动提交消息时如何, enable-auto-commit: false

            offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
            consumer.commitSync(offsets);
        }
        return res;
    }

    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> record) {
        Header envHeader = record.headers().lastHeader(Constants.ENV_KEY);

        if (Objects.isNull(envHeader) && StringUtils.isEmpty(glspKafKaConfig.getEnv())) {
            return record;
        }
        if (Objects.isNull(envHeader)) {
            return null;
        }
        if (glspKafKaConfig.getEnv().equals(new String(envHeader.value()).intern())) {
            return record;
        }
        return null;
    }
}
