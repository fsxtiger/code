package cn.golingo.glep.education.provider.service;

<#list imports as import>
import ${import};
</#list>

/**
 * @author shuoxuan.fang
 * @date ${.now?string("yyyy/MM/dd")}
 */
public interface ${name}Service {
<#list methods as method>
    ${method.returnValue} ${method.name}(${method.param});

</#list>
}