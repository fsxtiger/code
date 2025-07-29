package generator;

import com.google.common.collect.ImmutableMap;
import config.Config;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Config.METHODS;


/**
 * @Author shuoxuan.fang
 * @Date 2025/4/22
 **/
public abstract class AbstractCodeGenerator<T> implements CodeGenerator {
    protected static final Map<String, String> TYPE_MAP = ImmutableMap.of(
            "int", "int32",
            "Integer", "int32",
            "long", "int64",
            "Long", "int64",
            "String", "string"
    );
    protected final static Pattern CURVES_PATTERN = Pattern.compile("\\((.*?)\\)");
    protected final static Pattern ANGLE_PATTERN = Pattern.compile("<(.*)>");
    protected List<Method> parseMethod() throws Exception {
        Class<?> clazz = Class.forName(Config.CLASS_NAME);
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (METHODS.isEmpty() || METHODS.contains(method.getName()))
                methods.add(method);
        }
        return methods;
    }

    @Override
    public void generateCode() throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(Config.TEMPLATE_DIRS));
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate(getTemplateFile());


        List<Method> methods = parseMethod();

        T t = doGenerateCodeParam(methods);
        Map<String, Object> param = parseParam(t);

        File outputFile = new File(Config.CREATE_DIR + getGenerateFileName());
        outputFile.getParentFile().mkdirs(); // Ensure directories exist
        try (FileWriter writer = new FileWriter(outputFile)) {
            template.process(param, writer);
            System.out.println("Java class generated successfully: " + outputFile.getAbsolutePath());
        }
    }

    protected Map<String, Object> parseParam(T t) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields(); // 获取类的所有字段

        for (Field field : fields) {
            field.setAccessible(true); // 设置可访问私有字段
            resultMap.put(field.getName(), field.get(t)); // 将字段名和字段值放入Map
        }
        return resultMap;
    }

    protected String parseCurvesContent(String content) {
        Matcher returnTypeMatcher = CURVES_PATTERN.matcher(content);
        if (returnTypeMatcher.find()) {
            return returnTypeMatcher.group(1);
        }
        return "";
    }

    protected String parseAngleContent(String content) {
        Matcher returnTypeMatcher = ANGLE_PATTERN.matcher(content);
        if (returnTypeMatcher.find()) {
            return returnTypeMatcher.group(1);
        }
        return "";
    }

    protected String parseInputType(Method method) {
        String genericString = method.toGenericString();
        String methodSign = genericString.split(" ")[2];
        return parseCurvesContent(methodSign);
    }

    protected String parseReturnType(Method method) {
        String genericString = method.toGenericString();
        String returnType = genericString.split(" ")[1];
        return parseAngleContent(returnType);
    }

    protected String transformReturnValueToGrpc(String returnValue) {
        return transformReturnValueToGrpc(returnValue, "void");
    }
    protected String transformReturnValueToGrpc(String returnValue, String voidReturnValue) {
        if (returnValue.equalsIgnoreCase("void")) {
            return voidReturnValue;
        }
        String angleContent = parseAngleContent(returnValue);
        if (returnValue.startsWith("PageResultWrapper") && !angleContent.isEmpty()) {
            String transforms = angleContent.replace("VO", "PageDTO");
            transforms = transforms.replace("Vo", "PageDTO");
            return transforms;
        }
        String transforms = returnValue.replace("VO", "DTO");
        transforms = transforms.replace("Vo", "DTO");

        return transforms;
    }

    protected String transformParamToGrpc(String param) {
        return param.replaceAll("Param", "Model");
    }

    protected abstract T doGenerateCodeParam(List<Method> methods) throws Exception;
}
