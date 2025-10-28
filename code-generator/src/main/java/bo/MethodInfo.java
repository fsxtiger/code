package bo;

import lombok.Data;

import java.util.Objects;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/22
 **/
@Data
public class MethodInfo {
    private String returnValue;
    private String name;
    private String param;
    private String paramModel;
    private String paramDTO;

    public MethodInfo() {
    }

    public MethodInfo(String returnValue, String name, String param) {
        this.returnValue = returnValue;
        this.name = name;
        this.param = param;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(param, that.param);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, param);
    }
}