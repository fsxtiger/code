package config;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author shuoxuan.fang
 * @Date 2025/4/14
 **/
public class Config {
    public static final String NAME = "Auth";
    public static final String SERVICE_PATH = "cn.golingo.glep.admin.provider.service.";
    public static final String SERVICE_FULL_PATH = SERVICE_PATH + Config.NAME + "Service";
    public static final String REMOTE_SERVICE_PATH = "cn.golingo.glep.admin.provider.remote.";
    public static final String DOWN_STREAM_SERVICE_NAME = "User";

    public static final String REMOTE_SERVICE_FULL_PATH = REMOTE_SERVICE_PATH + "Remote" + DOWN_STREAM_SERVICE_NAME + "Service";

    public static final String TEMPLATE_DIRS = "code-generator/src/main/resources/templates";
    public static final String CREATE_DIR = "code-generator/src/main/resources/templates/files/";

    public static final List<String> BEAN_ANNOTATIONS_IMPORTS = Lists.newArrayList(
            "javax.annotation.Resource", "org.springframework.stereotype.Service"
    );

    public static final List<String> MAP_STRUCT_IMPORTS = Lists.newArrayList(
            "org.mapstruct.*"
    );
    public static final String CLASS_NAME = "cn.golingo.glep.admin.controller.audit.AuditTalentController";

    public static final String FULL_GRPC_PATH = "cn.golingo.glep." + StringUtils.uncapitalize(Config.DOWN_STREAM_SERVICE_NAME) + ".grpc.client.service." + Config.NAME + "ServiceGrpc";
    public static final String FULL_CONVERT_PATH = "cn.golingo.glep.education.provider.convert." + Config.NAME + "Convert";
    public static final List<String> METHODS = Lists.newArrayList();

    public static final String IMPORT_GRPC_MODEL_PATH_PREFIX = "cn.golingo.glep."
            + StringUtils.uncapitalize(Config.DOWN_STREAM_SERVICE_NAME)
            + ".grpc.client."
            + StringUtils.uncapitalize(Config.NAME)
            +".model.";

    public static final String PARAM_TO_MODEL = "paramToModel";
    public static final String DTO_TO_VO = "dtoToVO";

    public static final String MODEL_TO_ENTITY = "modelToEntity";
    public static final String ENTITY_TO_DTO = "entityToDTO";

    public static final String GRPC_EMPTY = "google.protobuf.Empty";
}