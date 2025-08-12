package generator;

import bo.MethodInfo;
import bo.ServiceParam;
import config.Config;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/24
 **/
public class DownStreamServiceProtoCodeGenerator extends UpStreamServiceCodeGenerator {
    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        ServiceParam serviceParam = super.doGenerateCodeParam(methods);
        List<MethodInfo> methodInfos = transformMethodInfos(serviceParam.getMethods());

        serviceParam.setMethods(methodInfos);
        return serviceParam;
    }

    private List<MethodInfo> transformMethodInfos(List<MethodInfo> methodInfos) {
        if (methodInfos.isEmpty()) {
            return methodInfos;
        }
        List<MethodInfo> newMethodInfos = new ArrayList<>();
        for (MethodInfo methodInfo : methodInfos) {
            String name = methodInfo.getName();
            String returnValue = null;
            if (methodInfo.getReturnValue().equalsIgnoreCase("void")) {
                returnValue = Config.GRPC_EMPTY;
            } else {
                returnValue = Config.IMPORT_GRPC_MODEL_PATH_PREFIX + transformReturnValueToGrpc(methodInfo.getReturnValue(), Config.GRPC_EMPTY);
            }
            String param = methodInfo.getParam();
            if (StringUtils.isNotBlank(param)) {
                param = param.split(" ")[0];
                param = Config.IMPORT_GRPC_MODEL_PATH_PREFIX + transformParamToGrpc(param);
            } else {
                param = Config.GRPC_EMPTY;
            }
            newMethodInfos.add(new MethodInfo(returnValue, name, param));
        }

        return newMethodInfos;
    }

    @Override
    public String getTemplateFile() {
        return "DownStreamServiceProto.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return Config.NAME + "Service.proto";
    }
}
