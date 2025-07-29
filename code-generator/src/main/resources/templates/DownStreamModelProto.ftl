syntax = "proto3";
import "google/protobuf/wrappers.proto";

package cn.golingo.glep.${serviceName}.grpc.client.${name}.model;
option java_multiple_files = true;

<#list messages as message>
message ${message.name} {
<#list message.fields as field>
    <#if field.repeated>
    repeated ${field.type} ${field.name}=${field_index + 1};
    <#else>
    ${field.type} ${field.name}=${field_index + 1};
    </#if>
</#list>
}

</#list>