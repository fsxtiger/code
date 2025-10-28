package generator;

import bo.MethodInfo;
import bo.ServiceParam;
import config.Config;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/22
 **/
public class UpStreamServiceCodeGenerator extends AbstractCodeGenerator<ServiceParam> {

    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        ServiceParam upStreamServiceParam = new ServiceParam();
        upStreamServiceParam.setName(Config.NAME);

        Tuple2<List<String>, List<String>> importPathsGroupByPosition = parseImportPath(methods);
        List<String> importPaths = mergeImportPath(importPathsGroupByPosition);
        importPaths = importPaths.stream().distinct().sorted().collect(Collectors.toList());

        List<MethodInfo> methodInfos = parseMethodInfo(methods);

        upStreamServiceParam.setMethods(methodInfos);
        upStreamServiceParam.setImports(importPaths);

        return upStreamServiceParam;
    }

    private List<MethodInfo> parseMethodInfo(List<Method> methods) {
        if (methods.isEmpty()) {
            return Lists.newArrayList();
        }
        List<MethodInfo> methodInfos = new ArrayList<>();
        for (Method method : methods) {
            methodInfos.add(parseMethodInfo(method));
        }

        return methodInfos;
    }

    private MethodInfo parseMethodInfo(Method method) {
        String genericString = method.toGenericString();
        String[] parts = genericString.split(" ");
        String returnValue = parseAngleContent(parts[1]);
        returnValue = normalize(returnValue);
        String methodName = normalize(parts[2].substring(0, parts[2].indexOf("(")));

        String paramContent = parseCurvesContent(genericString);
        String paramString = "";
        if (StringUtils.isNotBlank(paramContent)) {
            paramString = normalizeMethodString(paramContent, method, 0);
        }

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(methodName);
        methodInfo.setReturnValue(returnValue);
        methodInfo.setParam(paramString);
        methodInfo.setParamModel(getParamModelName(paramString, methodName));
        methodInfo.setParamDTO(getParamDTOName(paramString, methodName));

        return methodInfo;
    }

    protected String getParamDTOName(String param, String methodName) {
        String className = param.split(" ")[0];
        if (TYPE_MAP.containsKey(className) || StringUtils.isBlank(param)) {
            // 是基本类型，走另外的方式
            return WordUtils.capitalize(methodName) + "DTO";
        } else {
            return className.replace("Param", "DTO");
        }
    }

    protected String getParamModelName(String param, String methodName) {
        String className = param.split(" ")[0];
        if (TYPE_MAP.containsKey(className) || StringUtils.isBlank(param)) {
            // 是基本类型，走另外的方式
            return WordUtils.capitalize(methodName) + "Model";
        } else {
            return className.replace("Param", "Model");
        }
    }

    private String normalize(String str) {
        if (!str.contains(".")) {
            //基本类型
            return str;
        }
        if (!str.contains("<")) {
            //没有包含泛型
            String res = str.substring(str.lastIndexOf(".") + 1);
            if (res.equalsIgnoreCase("Void")) {
                return "void";
            } else {
                return res;
            }
        }
        StringBuilder builder = new StringBuilder();
        Matcher matcher = ANGLE_PATTERN.matcher(str);
        while (matcher.find()) {
            String genericString = matcher.group(1);
            str = str.replace("<" + genericString + ">", "");
            str = str.substring(str.lastIndexOf(".") + 1);
            if (builder.length() > 0) {
                builder.append("<").append(str).append(">");
            } else {
                builder.append(str);
            }
            str = genericString;
            matcher = ANGLE_PATTERN.matcher(str);
        }
        str = str.substring(str.lastIndexOf(".") + 1);
        builder.append("<").append(str).append(">");
        return builder.toString();
    }



    private List<String> mergeImportPath(Tuple2<List<String>, List<String>> importPaths) {
        List<String> importPath = new ArrayList<>();
        for (String path : importPaths.getT1()) {
            if (pathNeedImport(path)) {
                importPath.add(path);
            }
        }
        for (String path : importPaths.getT2()) {
            if (pathNeedImport(path)) {
                importPath.add(path);
            }
        }
        return importPath;
    }

    public String normalizeMethodString(String param, Method method, int index) {
        if (param.contains(",")) {
            String[] params = param.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (int x = 0; x < params.length; x++) {
                stringBuilder.append(normalizeMethodString(params[x], method, x));
                stringBuilder.append(",");
            }
            param = stringBuilder.toString();
            param = param.substring(0, param.length() - 1);
            return param;
        }
        param = normalize(param);
        String paramName = generateParamName(param, method, index);
        return param + " " + paramName;
    }

    private String generateParamName(String param, Method method, int index) {
        if (TYPE_MAP.containsKey(param)) {
            // 说明是基本类型，需要从注解
            Parameter parameter = method.getParameters()[index];
            ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
            if (apiParam != null) {
                if (StringUtils.isNotBlank(apiParam.name())) {
                    return apiParam.name();
                }
                if (StringUtils.isNotBlank(apiParam.value()) && StringUtils.isAlpha(apiParam.value())) {
                    return apiParam.value();
                }
            }
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null && StringUtils.isNotBlank(requestParam.value())) {
                return requestParam.value();
            }
        }
        int end = param.length();
        if (param.contains("<")) {
            end = param.indexOf("<");
        }
        String paramName = param.substring(0, end);
        paramName = paramName.substring(0, 1).toLowerCase() + paramName.substring(1);

        return paramName;
    }


    private boolean pathNeedImport(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (!path.contains(".")) {
            return false;
        }
        if (path.startsWith("java.lang.")) {
            return false;
        }
        return true;
    }



    public Tuple2<List<String>, List<String>> parseImportPath(List<Method> methods) {
        if (methods.isEmpty()) {
            return null;
        }
        List<String> outputTypes = new ArrayList<>();
        List<String> inputTypes = new ArrayList<>();

        for (Method method : methods) {
            List<String> outputType = parseReturnImportPath(method);
            if (!outputType.isEmpty()) {
                outputTypes.addAll(outputType);
            }
            String inputType = parseInputType(method);
            if (!inputType.isEmpty()) {
                inputTypes.add(inputType);
            }
        }
        outputTypes = outputTypes.stream().distinct().collect(Collectors.toList());
        inputTypes = inputTypes.stream().distinct().collect(Collectors.toList());

        return Tuples.of(outputTypes, inputTypes);
    }



    private List<String> parseReturnImportPath(Method method) {
        String angleContent = parseReturnType(method);

        String subAngleContent = parseAngleContent(angleContent);
        if (subAngleContent.isEmpty()) {
            return Lists.newArrayList(angleContent);
        }
        List<String> returnImportPaths = new ArrayList<>();
        returnImportPaths.add(subAngleContent);

        angleContent = angleContent.substring(0, angleContent.indexOf("<"));
        returnImportPaths.add(angleContent);

        return returnImportPaths;
    }



    @Override
    public String getTemplateFile() {
        return "UpStreamService.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return Config.NAME + "Service.java";
    }
}
