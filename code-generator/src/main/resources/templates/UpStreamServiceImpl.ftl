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
    private ${name}Convert ${uncapitalize_name}Convert;

    <#list methods as method>
    @Override
    public ${method.returnValue} ${method.name}(${method.param}) {
    <#if method.param?? && method.param?has_content>
        <#assign paramArray = method.param?split(" ")>
            <#assign secondParam = "" />
            <#if paramArray?size gt 1>
                <#assign secondParam = paramArray[1]>
            </#if>
        ${method.paramModel} ${method.paramModel?uncap_first} = ${uncapitalize_name}Convert.paramToModel(${secondParam});
    </#if>
    <#if method.returnValue != 'void'>
        ${method.paramDTO} ${method.paramDTO?uncap_first} = remote${serviceName}Service.${method.name}(${method.paramModel?uncap_first});
        return ${uncapitalize_name}Convert.dtoToVO(${method.paramDTO?uncap_first});
    <#else>
        remote${serviceName}Service.${method.name}(${method.paramModel?uncap_first});
    </#if>
    }
    </#list>
}