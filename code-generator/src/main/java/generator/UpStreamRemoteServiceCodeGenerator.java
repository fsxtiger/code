package generator;

import bo.MethodInfo;
import bo.ServiceParam;
import config.Config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/24
 **/
public class UpStreamRemoteServiceCodeGenerator extends UpStreamServiceCodeGenerator {
    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        ServiceParam serviceParam = super.doGenerateCodeParam(methods);
        serviceParam.setServiceName(Config.DOWN_STREAM_SERVICE_NAME);
        serviceParam.setMethods(transformMethodInfo(serviceParam.getMethods()));
        serviceParam.setImports(transformImports(serviceParam));

        return serviceParam;
    }

    private List<String> transformImports(ServiceParam serviceParam) {
        List<String> importPaths = new ArrayList<>();
        for (MethodInfo methodInfo : serviceParam.getMethods()) {
            if (!methodInfo.getReturnValue().equalsIgnoreCase("void")) {
                importPaths.add(Config.IMPORT_GRPC_MODEL_PATH_PREFIX + methodInfo.getReturnValue());
            }
            String param = methodInfo.getParam();
            param = param.split(" ")[0];
            importPaths.add(Config.IMPORT_GRPC_MODEL_PATH_PREFIX + param);
        }
        importPaths = importPaths.stream().distinct().sorted().collect(Collectors.toList());
        return importPaths;
    }

    private List<MethodInfo> transformMethodInfo(List<MethodInfo> methodInfos) {
        for(MethodInfo methodInfo : methodInfos) {
            String returnValue = methodInfo.getReturnValue();
            methodInfo.setReturnValue(transformReturnValueToGrpc(returnValue));
            String param = methodInfo.getParam();
            methodInfo.setParam(transformParamToGrpc(param));
        }
        return methodInfos;
    }

    @Override
    public String getTemplateFile() {
        return "UpStreamRemoteService.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return "Remote"+ Config.DOWN_STREAM_SERVICE_NAME + "Service.java";
    }
}
