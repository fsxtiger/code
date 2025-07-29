package bo;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/23
 **/
@Data
public class ProtoMessage {
    private String name;
    private List<MessageField> fields;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtoMessage that = (ProtoMessage) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
