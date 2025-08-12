package generator;

import cn.unipus.glsp.base.wrapper.PageQueryWrapper;
import bo.MessageField;
import bo.ProtoMessage;
import bo.ProtoParam;
import config.Config;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/23
 **/
public class DownStreamModelProtoFileCodeGenerator extends AbstractCodeGenerator<ProtoParam> {
    @Override
    protected ProtoParam doGenerateCodeParam(List<Method> methods) throws Exception {
        Tuple2<List<String>, List<String>> requestResponseObject = parseRequestResponseObject(methods);

        ProtoParam protoParam = new ProtoParam();
        protoParam.setServiceName(StringUtils.uncapitalize(Config.DOWN_STREAM_SERVICE_NAME));
        protoParam.setName(StringUtils.uncapitalize(Config.NAME));

        List<ProtoMessage> messages = composeProtoMessage(requestResponseObject.getT2(), requestResponseObject.getT1());
        messages = messages.stream().distinct().collect(Collectors.toList());
        protoParam.setMessages(messages);

        return protoParam;
    }

    private List<ProtoMessage> composeProtoMessage(List<String> requestObjects, List<String> responseObjects) throws Exception{
        List<ProtoMessage> messages = new ArrayList<>();
        for (String requestObject : requestObjects) {
            ProtoMessage protoMessage = composeRequestProtoMessage(requestObject);
            if (protoMessage != null) {
                messages.add(protoMessage);
            }
        }
        for (String responseObject : responseObjects) {
            if (responseObject.startsWith("java.lang")) {
                continue;
            }
            messages.addAll(composeResponseProtoMessage(responseObject));
        }
        return messages;
    }

    private ProtoMessage composeRequestProtoMessage(String requestObject) throws Exception {
        Class<?> clazz = Class.forName(requestObject);
        if (TYPE_MAP.containsKey(clazz.getSimpleName())) {
            return null;
        }
        ProtoMessage protoMessage = new ProtoMessage();
        protoMessage.setName(composeRequestName(clazz.getSimpleName()));
        List<MessageField> messageFields = composeRequestMessageFields(clazz);
        protoMessage.setFields(messageFields);
        return protoMessage;

    }

    private List<MessageField> composeRequestMessageFields(Class<?> clazz) {
        if (clazz.equals(PageQueryWrapper.class)) {
            // 如果是PageQueryWrapper，特殊处理一下，不用传递这么多属性
            List<MessageField> messageFields = new ArrayList<>();
            messageFields.add(new MessageField("int32", "current"));
            messageFields.add(new MessageField("int32", "size"));

            return messageFields;
        }

        List<MessageField> messageFields = new ArrayList<>();
        Class<?> parentClass = clazz.getSuperclass();
        if (parentClass != Object.class) {
            messageFields.addAll(composeRequestMessageFields(parentClass));
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            messageFields.add(composeMessageField(field));
        }

        return messageFields;
    }

    private MessageField composeMessageField(Field field) {
        MessageField messageField = new MessageField();
        messageField.setType(TYPE_MAP.getOrDefault(field.getType().getSimpleName(), "string"));
        messageField.setName(field.getName());
        return messageField;
    }

    private String composeRequestName(String className) {
        String name = className.replace("Param", "");
        return name + "Model";
    }


    private List<ProtoMessage> composeResponseProtoMessage(String responseObject) throws Exception {
        List<ProtoMessage> protoMessages = new ArrayList<>();
        String angleContent = parseAngleContent(responseObject);
        if (responseObject.contains("PageResultWrapper") && !angleContent.isEmpty()) {
            //分页返回，需要构建两个ProtoMessage
            protoMessages.add(createPageProtoMessage(angleContent));
            protoMessages.add(createResponseProtoMessage(angleContent));
            return protoMessages;
        } else if (angleContent.isEmpty()) {
            //普通对象，只需构建一个就行
            protoMessages.add(createResponseProtoMessage(responseObject));
            return protoMessages;
        } else {
            System.err.println("invalid response object: " + responseObject);
            return protoMessages;
        }
    }

    private ProtoMessage createResponseProtoMessage(String responseObject) throws Exception {
        Class<?> clazz = Class.forName(responseObject);

        ProtoMessage protoMessage = new ProtoMessage();
        protoMessage.setName(createResponseName(responseObject));
        if (responseObject.startsWith("java.lang")) {
            protoMessage.setFields(Lists.newArrayList());
        } else {
            protoMessage.setFields(composeRequestMessageFields(clazz));
        }
        return protoMessage;
    }


    private ProtoMessage createPageProtoMessage(String angleContent) throws Exception {
        ProtoMessage protoMessage = new ProtoMessage();
        protoMessage.setName(createPageWrapperName(angleContent));

        List<MessageField> messageFields = createPageWrapperField(angleContent);
        protoMessage.setFields(messageFields);

        return protoMessage;
    }

    private List<MessageField> createPageWrapperField(String angleContent) throws Exception{
        List<MessageField> messageFields = new ArrayList<>();
        messageFields.add(new MessageField("int64", "total"));
        String name = createResponseName(angleContent);
        messageFields.add(new MessageField(name, StringUtils.uncapitalize(name) + "s", Boolean.TRUE));
        return messageFields;
    }

    private String createPageWrapperName(String angleContent) throws Exception{
        Class<?> clazz = Class.forName(angleContent);
        String name = clazz.getSimpleName().replace("VO", "PageDTO");
        name = name.replace("Vo", "PageDTO");
        name = name.replace("vo", "PageDTO");
        return name;
    }

    private String createResponseName(String angleContent) throws Exception{
        Class<?> clazz = Class.forName(angleContent);
        String name = clazz.getSimpleName().replace("VO", "DTO");
        name = name.replace("Vo", "DTO");
        name = name.replace("vo", "DTO");
        return name;
    }

    public Tuple2<List<String>, List<String>> parseRequestResponseObject(List<Method> methods) {
        if (methods.isEmpty()) {
            return null;
        }
        List<String> outputTypes = new ArrayList<>();
        List<String> inputTypes = new ArrayList<>();

        for (Method method : methods) {
            String outputType = parseReturnType(method);
            if (!outputType.isEmpty()) {
                outputTypes.add(outputType);
            }
            String inputType = parseInputType(method);
            if (!inputType.isEmpty()) {
                if (inputType.contains(",")) {
                    for (String type : inputType.split(",")) {
                        inputTypes.add(type);
                    }
                } else {
                    inputTypes.add(inputType);
                }
            }
        }
        outputTypes = outputTypes.stream().distinct().collect(Collectors.toList());
        inputTypes = inputTypes.stream().distinct().collect(Collectors.toList());

        return Tuples.of(outputTypes, inputTypes);
    }

    @Override
    public String getTemplateFile() {
        return "DownStreamModelProto.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return Config.NAME + "Model.proto";
    }
}
