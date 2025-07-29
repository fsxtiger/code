package generator;

import bo.MethodInfo;
import bo.ServiceParam;
import config.Config;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static config.Config.MAP_STRUCT_IMPORTS;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/24
 **/
public class UpStreamConvertCodeGenerator extends UpStreamServiceCodeGenerator {
    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        ServiceParam serviceParam = super.doGenerateCodeParam(methods);
        List<MethodInfo> methodInfos = transformMethodInfos(serviceParam.getMethods());
        serviceParam.setMethods(methodInfos);

        List<String> addedImportPaths = parseAddedImportPaths(methodInfos);
        List<String> importPaths = serviceParam.getImports();
        importPaths.addAll(addedImportPaths);
        importPaths.addAll(MAP_STRUCT_IMPORTS);
        importPaths = importPaths.stream().distinct().sorted().collect(Collectors.toList());
        serviceParam.setImports(importPaths);

        return serviceParam;
    }

    private List<String> parseAddedImportPaths(List<MethodInfo> methodInfos) {
        if (methodInfos.isEmpty()) {
            return Lists.newArrayList();
        }
        List<String> addedImportPath = new ArrayList<>();
        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.getName().equalsIgnoreCase(Config.PARAM_TO_MODEL)) {
                addedImportPath.add(Config.IMPORT_GRPC_MODEL_PATH_PREFIX + methodInfo.getReturnValue());
            }
            if (methodInfo.getParam().equalsIgnoreCase(Config.DTO_TO_VO)) {
                addedImportPath.add(Config.IMPORT_GRPC_MODEL_PATH_PREFIX + methodInfo.getParam().split(" ")[0]);
            }
        }

        return addedImportPath;
    }

    private List<MethodInfo> transformMethodInfos(List<MethodInfo> methodInfos) {
        if (methodInfos.isEmpty()) {
            return methodInfos;
        }
        List<MethodInfo> newMethodInfos = new ArrayList<>();
        for (MethodInfo methodInfo : methodInfos) {
            String returnValue = methodInfo.getReturnValue();
            MethodInfo returnMethodInfo = parseReturnValue(returnValue);
            if (Objects.nonNull(returnMethodInfo)) {
                newMethodInfos.add(returnMethodInfo);
            }
            newMethodInfos.add(parseInputParam(methodInfo));
        }
        return newMethodInfos;
    }

    private MethodInfo parseInputParam(MethodInfo param) {
        return new MethodInfo(param.getParamModel(), Config.PARAM_TO_MODEL, param.getParam());
    }

    private MethodInfo parseReturnValue(String returnValue) {
        if (returnValue.equalsIgnoreCase("void")) {
            return null;
        }
        String angleContent = parseAngleContent(returnValue);
        if (returnValue.startsWith("PageResultWrapper") && !angleContent.isEmpty()) {
            return parseReturnValue(angleContent);
        }
        String newReturnValue = returnValue.replace("VO", "DTO");
        newReturnValue = newReturnValue.replace("Vo", "DTO");
        String param = composeParam(returnValue);

        return new MethodInfo(param, Config.DTO_TO_VO, newReturnValue);
    }

    private String composeParam(String returnValue) {
        return returnValue+ " " + StringUtils.uncapitalize(returnValue);
    }

    @Override
    public String getTemplateFile() {
        return "UpStreamConvert.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return Config.NAME + "Convert.java";
    }
}