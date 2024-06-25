package kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * @Author shuoxuan.fang
 * @Date 2024/2/23
 **/
@Slf4j
public class DefaultAnnotationEnhancer implements KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer {

    @Override
    public Map<String, Object> apply(Map<String, Object> stringObjectMap, AnnotatedElement annotatedElement) {
        if (stringObjectMap.containsKey("groupId")) {
            String groupId = stringObjectMap.get("groupId").toString();
            String newGroupId = groupId + "-${" + Constants.ENV_NAME + "}";

            stringObjectMap.put("groupId", newGroupId);
        }
        return stringObjectMap;
    }
}
