package cn.golingo.glep.education.provider.remote;

<#list imports as import>
import ${import};
</#list>

/**
 * @author shuoxuan.fang
 * @date ${.now?string("yyyy/MM/dd")}
 */
 public interface Remote${serviceName}GrpcService {
 <#list methods as method>
     ${method.returnValue} ${method.name}(${method.param});

 </#list>
 }