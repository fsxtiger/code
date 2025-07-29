package bo;

import lombok.Data;

import java.util.List;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/22
 **/
@Data
public class ServiceParam {
    private String serviceName;
    private String name;
    private List<String> imports;
    private List<MethodInfo> methods;
}
