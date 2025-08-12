import generator.*;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/14
 **/
public class FreemakerGenerator {
    public static void main(String[] args) throws Exception {
        UpStreamServiceCodeGenerator upStreamServiceCodeGenerator = new UpStreamServiceCodeGenerator();
        UpStreamServiceImplCodeGenerator upStreamServiceImplCodeGenerator = new UpStreamServiceImplCodeGenerator();
        DownStreamModelProtoFileCodeGenerator downStreamModelProtoFileCodeGenerator = new DownStreamModelProtoFileCodeGenerator();
        UpStreamRemoteServiceCodeGenerator upStreamRemoteServiceCodeGenerator = new UpStreamRemoteServiceCodeGenerator();
        UpStreamRemoteServiceImplCodeGenerator upStreamRemoteServiceImplCodeGenerator = new UpStreamRemoteServiceImplCodeGenerator();
        UpStreamConvertCodeGenerator upStreamConvertCodeGenerator = new UpStreamConvertCodeGenerator();
        DownStreamServiceProtoCodeGenerator downStreamServiceProtoCodeGenerator = new DownStreamServiceProtoCodeGenerator();
        DownStreamServiceCodeGenerator downStreamServiceCodeGenerator = new DownStreamServiceCodeGenerator();

        List<CodeGenerator> codeGenerators = Lists.newArrayList(
                upStreamServiceCodeGenerator,
                upStreamServiceImplCodeGenerator,
                downStreamModelProtoFileCodeGenerator,
                upStreamRemoteServiceCodeGenerator,
                upStreamRemoteServiceImplCodeGenerator,
                upStreamConvertCodeGenerator,
                downStreamServiceProtoCodeGenerator,
                downStreamServiceCodeGenerator
        );
        for (CodeGenerator codeGenerator : codeGenerators) {
            codeGenerator.generateCode();
        }

    }
}