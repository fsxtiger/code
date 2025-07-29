package generator;

import bo.ServiceParam;
import config.Config;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/22
 **/
public class UpStreamServiceImplCodeGenerator extends UpStreamServiceCodeGenerator {
    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        ServiceParam serviceParam = super.doGenerateCodeParam(methods);
        serviceParam.setServiceName(Config.DOWN_STREAM_SERVICE_NAME);
        List<String> importPaths = serviceParam.getImports();
        importPaths.add(Config.SERVICE_FULL_PATH);
        importPaths.add(Config.REMOTE_SERVICE_FULL_PATH);
        importPaths.add(Config.FULL_CONVERT_PATH);
        importPaths.addAll(Config.BEAN_ANNOTATIONS_IMPORTS);

        importPaths = importPaths.stream().sorted().collect(Collectors.toList());
        serviceParam.setImports(importPaths);

        return serviceParam;
    }

    @Override
    public String getTemplateFile() {
        return "UpStreamServiceImpl.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return Config.NAME + "ServiceImpl.java";
    }
}
