package cn.golingo.glep.education.provider.remote.impl;
<#assign uncapitalize_name = name?substring(0, 1)?lower_case + name?substring(1)>

<#list imports as import>
import ${import};
</#list>

/**
 * @author shuoxuan.fang
 * @date ${.now?string("yyyy/MM/dd")}
 */

@Service
public class Remote${serviceName}GrpcServiceImpl implements Remote${serviceName}GrpcService {
    private ${name}ServiceGrpc.${name}ServiceBlockingStub ${uncapitalize_name}ServiceBlockingStub;

    @PostConstruct
    public void init() {
        ${uncapitalize_name}ServiceBlockingStub = ${name}ServiceGrpc.newBlockingStub(managedChannel);
    }

<#list methods as method>
    @Override
    public ${method.returnValue} ${method.name}(${method.param}) {
    <#assign paramArray = method.param?split(" ")>
    <#assign secondParam = "" />
    <#if paramArray?size gt 1>
        <#assign secondParam = paramArray[1]>
    <#else>
        <#assign secondParam = "Empty.getDefaultInstance()">
    </#if>
    <#if method.returnValue == 'void'>
        ${uncapitalize_name}ServiceBlockingStub.${method.name}(${secondParam});
    <#else>
        return ${uncapitalize_name}ServiceBlockingStub.${method.name}(${secondParam});
    </#if>
    }
</#list>
}