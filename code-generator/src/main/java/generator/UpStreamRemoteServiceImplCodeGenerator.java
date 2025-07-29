package generator;

import bo.ServiceParam;
import config.Config;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/24
 **/
public class UpStreamRemoteServiceImplCodeGenerator extends UpStreamRemoteServiceCodeGenerator{
    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        ServiceParam serviceParam = super.doGenerateCodeParam(methods);
        List<String> importPaths = serviceParam.getImports();
        importPaths.add(Config.REMOTE_SERVICE_FULL_PATH);
        importPaths.addAll(Config.BEAN_ANNOTATIONS_IMPORTS);
        importPaths.add(Config.FULL_GRPC_PATH);

        importPaths = importPaths.stream().sorted().collect(Collectors.toList());
        serviceParam.setImports(importPaths);

        return serviceParam;
    }

    @Override
    public String getTemplateFile() {
        return "UpStreamRemoteServiceImpl.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return "Remote" + Config.DOWN_STREAM_SERVICE_NAME + "ServiceImpl.java";
    }
}
