<#assign uncapitalize_serviceName = serviceName?substring(0, 1)?lower_case + serviceName?substring(1)>
package cn.golingo.glep.${uncapitalize_serviceName}.service;

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
