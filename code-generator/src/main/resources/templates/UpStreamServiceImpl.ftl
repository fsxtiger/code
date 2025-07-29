package cn.golingo.glep.education.provider.service.impl;
<#assign uncapitalize_name = name?substring(0, 1)?lower_case + name?substring(1)>
<#list imports as import>
import ${import};
</#list>

/**
 * @author shuoxuan.fang
 * @date ${.now?string("yyyy/MM/dd")}
 */
@Service
public class ${name}ServiceImpl implements ${name}Service {
    @Resource
    private Remote${serviceName}Service remote${serviceName}Service;
    @Resource
    private ${name}Convert ${uncapitalize_name}Convert

    <#list methods as method>
    @Override
    public ${method.returnValue} ${method.name}(${method.param}) {

    }
    </#list>
}