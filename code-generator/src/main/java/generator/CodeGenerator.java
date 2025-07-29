package generator;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/22
 **/
public interface CodeGenerator {
    String getTemplateFile();

    String getGenerateFileName();

    void generateCode() throws Exception;
}
