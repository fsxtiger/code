package generator;

import java.lang.reflect.Method;
import java.util.List;
import bo.ServiceParam;
import config.Config;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/24
 **/
public class DownStreamServiceCodeGenerator extends UpStreamRemoteServiceCodeGenerator {
    @Override
    protected ServiceParam doGenerateCodeParam(List<Method> methods) throws Exception {
        return super.doGenerateCodeParam(methods);
    }

    @Override
    public String getTemplateFile() {
        return "DownStreamService.ftl";
    }

    @Override
    public String getGenerateFileName() {
        return Config.NAME+"Servie2.java";
    }
}
