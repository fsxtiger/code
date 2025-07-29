package cn.golingo.glep.education.provider.convert;

<#list imports as import>
import ${import};
</#list>

/**
 * @author shuoxuan.fang
 * @date ${.now?string("yyyy/MM/dd")}
 */

@Mapper(
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
public interface ${name}Convert {
<#list methods as method>
    ${method.returnValue} ${method.name}(${method.param});

</#list>
}