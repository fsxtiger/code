syntax = "proto3";

import "google/protobuf/empty.proto";
import "${name}Model.proto";

service ${name}Service {
<#list methods as method>
    rpc ${method.name}(${method.param}) returns (${method.returnValue});
</#list>
}