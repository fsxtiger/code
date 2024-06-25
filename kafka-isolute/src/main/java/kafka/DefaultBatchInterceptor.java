package kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.BatchInterceptor;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shuoxuan.fang
 * @Date 2024/3/5
 **/
public class DefaultBatchInterceptor<K, V> implements BatchInterceptor<K, V> {
    @Resource
    private DefaultRecordInterceptor<K, V> defaultRecordInterceptor;
    @Override
    public ConsumerRecords<K, V> intercept(ConsumerRecords<K, V> records, Consumer<K, V> consumer) {
        Map<TopicPartition, List<ConsumerRecord<K, V>>> map = new HashMap<>();

        for (TopicPartition topicPartition : records.partitions()) {
            List<ConsumerRecord<K, V>> consumerRecords = records.records(topicPartition);
            consumerRecords = handleConsumerRecords(consumerRecords, consumer);
            if (!CollectionUtils.isEmpty(consumerRecords)) {
                map.put(topicPartition, consumerRecords);
            }
        }

        if (CollectionUtils.isEmpty(map)) {
            return null;
        }

        return new ConsumerRecords<>(map);
    }

    private List<ConsumerRecord<K, V>> handleConsumerRecords(List<ConsumerRecord<K, V>> consumerRecords, Consumer<K, V> consumer) {
        ConsumerRecord<K, V> tail = consumerRecords.get(consumerRecords.size() - 1);


        consumerRecords = consumerRecords.stream().filter(consumerRecord ->
            Objects.nonNull(defaultRecordInterceptor.intercept(consumerRecord))
        ).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(consumerRecords)) {
            //本次拉取全部都不是本系统的消息
            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            offsets.put(new TopicPartition(tail.topic(), tail.partition()), new OffsetAndMetadata(tail.offset() + 1));
            consumer.commitSync(offsets);

            return new ArrayList<>();
        }
        ConsumerRecord<K, V> tailValidRecord = consumerRecords.get(consumerRecords.size() - 1);
        if (tailValidRecord.equals(tail)) {
            return consumerRecords;
        }

        ConsumerRecord<K, V> newTail = new ConsumerRecord<>(tailValidRecord.topic(),
                tailValidRecord.partition(),
                tail.offset(),
                tailValidRecord.timestamp(),
                tailValidRecord.timestampType(),
                tailValidRecord.checksum(),
                tailValidRecord.serializedKeySize(),
                tailValidRecord.serializedValueSize(),
                tailValidRecord.key(),
                tailValidRecord.value());

        consumerRecords.remove(tailValidRecord);
        consumerRecords.add(newTail);

        return consumerRecords;
    }
}
