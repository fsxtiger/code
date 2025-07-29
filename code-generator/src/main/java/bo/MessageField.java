package bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/23
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageField {
    private String type;
    private String name;
    private boolean repeated = false;

    public MessageField(String type, String name) {
        this.type = type;
        this.name = name;
        this.repeated = Boolean.FALSE;
    }
}
