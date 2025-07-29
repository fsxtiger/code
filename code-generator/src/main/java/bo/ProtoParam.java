package bo;

import lombok.Data;

import java.util.List;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/23
 **/
@Data
public class ProtoParam {
    private String name;
    private String serviceName;
    private List<ProtoMessage> messages;
}
