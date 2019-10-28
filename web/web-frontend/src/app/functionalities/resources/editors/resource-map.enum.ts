import {Type} from "@angular/core";
import {HttpRequestComponent} from "./http/request/http-request.component";
import {ResourceComponent} from "./resource-component.interface";
import {Resource} from "../../../model/resource/resource.model";
import {HttpRequest} from "../../../model/resource/http/http-request.model";
import {ResourceType} from "../tree/model/type/resource-type.model";
import {HttpRequestResourceType} from "../tree/model/type/http-request.resource-type.model";
import {HttpMockServerComponent} from "./http/mock/server/http-mock-server.component";
import {HttpMockServer} from "./http/mock/server/model/http-mock-server.model";
import {HttpMockServerResourceType} from "../tree/model/type/http-mock-server.resource-type.model";
import {HttpMockComponent} from "./http/mock/stub/http-mock.component";
import {HttpMock} from "./http/mock/stub/model/http-mock.model";
import {HttpMockStubResourceType} from "../tree/model/type/http-mock-stub.resource-type.model";
import {HttpResponseVerifyComponent} from "./http/response_verify/http-response-verify.component";
import {HttpResponseVerify} from "./http/response_verify/model/http-response-verify.model";
import {HttpResponseVerifyResourceType} from "../tree/model/type/http-response-verify.resource-type.model";
import {RdbmsConnectionConfigComponent} from "./database/connection/rdbms-connection-config.component";
import {RdbmsConnectionConfig} from "../../../model/resource/rdbms/rdbms-connection-config.model";
import {RdbmsConnectionResourceType} from "../tree/model/type/rdbms-connection.resource-type.model";
import {RdbmsSqlComponent} from "./database/sql/rdbms-sql.component";
import {BasicResource} from "../../../model/resource/basic/basic-resource.model";
import {RdbmsSqlResourceType} from "../tree/model/type/rdbms-sql.resource-type.model";
import {RdbmsVerifyComponent} from "./database/verify/rdbms-verify.component";
import {SchemaVerify} from "./database/verify/rdbms-verify-tree/model/schema-verify.model";
import {RdbmsVerifyResourceType} from "../tree/model/type/rdbms-verify.resource-type.model";
import {BasicResourceComponent} from "./basic/basic-resource.component";
import {JsonVerifyResourceType} from "../tree/model/type/json-verify.resource-type.model";
import {BasicResourceType} from "../tree/model/type/basic.resource-type.model";
import {JsonVerifyResourceComponent} from "./json/json_verify/json-verify-resource.component";
import {JsonVerify} from "../../../generic/components/json-verify/model/json-verify.model";
import {JsonUtil} from "../../../utils/json.util";
import {JsonResourceComponent} from "./json/json_resource/json-resource.component";
import {JsonResourceType} from "../tree/model/type/json.resource-type.model";
import {TypeMeta} from "../../../model/text/parts/param-meta/type-meta.model";
import {StringTypeMeta} from "../../../model/text/parts/param-meta/string-type.meta";
import {NumberTypeMeta} from "../../../model/text/parts/param-meta/number-type.meta";
import {EnumTypeMeta} from "../../../model/text/parts/param-meta/enum-type.meta";
import {BooleanTypeMeta} from "../../../model/text/parts/param-meta/boolean-type.meta";
import {ObjectTypeMeta} from "../../../model/text/parts/param-meta/object-type.meta";
import {ObjectResourceComponent} from "./object/object-resource.component";
import {ObjectResourceType} from "../tree/model/type/object.resource-type.model";
import {ObjectResourceModel} from "./object/object-resource.model";
import {ListTypeMeta} from "../../../model/text/parts/param-meta/list-type.meta";
import {MapTypeMeta} from "../../../model/text/parts/param-meta/map-type.meta";

export class ResourceMapEnum {
    public static TEXT: ResourceMapEnum = new ResourceMapEnum(
        new StringTypeMeta("java.lang.String"),
        "TEXT",
        "Text",
        null,
        BasicResourceComponent,
        () => {return new BasicResource()},
        null,
        () => {return new BasicResourceType()},
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static NUMBER: ResourceMapEnum = new ResourceMapEnum(
        new NumberTypeMeta(),
        "NUMBER",
        "Number",
        null,
        BasicResourceComponent,
        () => {return new BasicResource()},
        null,
        () => {return new BasicResourceType()},
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static ENUM: ResourceMapEnum = new ResourceMapEnum(
        new EnumTypeMeta(),
        "ENUM",
        "Enum",
        null,
        BasicResourceComponent,
        () => {return new BasicResource()},
        null,
        () => {return new BasicResourceType()},
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static BOOLEAN: ResourceMapEnum = new ResourceMapEnum(
        new BooleanTypeMeta(),
        "BOOLEAN",
        "Boolean",
        null,
        BasicResourceComponent,
        () => {return new BasicResource()},
        null,
        () => {return new BasicResourceType()},
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static RDBMS_CONNECTION: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("database.relational.connection_manager.model.RdbmsConnection"),
        "database.relational.connection_manager.model.RdbmsConnection",
        "RdbmsConnection",
        "rdbms.connection.yaml",
        RdbmsConnectionConfigComponent,
        () => {return new RdbmsConnectionConfig()},
        () => {return RdbmsConnectionResourceType.getInstanceForRoot()},
        () => {return RdbmsConnectionResourceType.getInstanceForChildren()},
        (input:string) => {return new RdbmsConnectionConfig().deserialize(JsonUtil.parseJson(input))}
    );
    public static RDBMS_SQL: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("database.relational.model.RdbmsSql"),
        "database.relational.model.RdbmsSql",
        "RdbmsSql",
        "sql",
        RdbmsSqlComponent,
        () => {return new BasicResource()},
        () => {return RdbmsSqlResourceType.getInstanceForRoot()},
        () => {return RdbmsSqlResourceType.getInstanceForChildren()},
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static RDBMS_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("database.relational.model.RdbmsVerify"),
        "database.relational.model.RdbmsVerify",
        "RdbmsVerify",
        "rdbms.verify.json",
        RdbmsVerifyComponent,
        () => {return new SchemaVerify(null)},
        () => {return RdbmsVerifyResourceType.getInstanceForRoot()},
        () => {return RdbmsVerifyResourceType.getInstanceForChildren()},
        (input:string) => {return new SchemaVerify(null).deserialize(JsonUtil.parseJson(input))}
    );
    public static HTTP_REQUEST: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("com.testerum.model.resources.http.request.HttpRequest"),
        "com.testerum.model.resources.http.request.HttpRequest",
        "HttpRequest",
        "http.request.yaml",
        HttpRequestComponent,
        () => {return new HttpRequest()},
        () => {return HttpRequestResourceType.getInstanceForRoot()},
        () => {return HttpRequestResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpRequest().deserialize(JsonUtil.parseJson(input))}
    );
    public static HTTP_RESPONSE_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("http.response.verify.model.HttpResponseVerify"),
        "http.response.verify.model.HttpResponseVerify",
        "HttpResponseVerify",
        "http.response.verify.yaml",
        HttpResponseVerifyComponent,
        () => {return new HttpResponseVerify()},
        () => {return HttpResponseVerifyResourceType.getInstanceForRoot()},
        () => {return HttpResponseVerifyResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpResponseVerify().deserialize(JsonUtil.parseJson(input))}
    );
    public static HTTP_MOCK_SERVER_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("com.testerum.model.resources.http.mock.server.HttpMockServer"),
        "com.testerum.model.resources.http.mock.server.HttpMockServer",
        "HttpMockServer",
        "http.mock.server.yaml",
        HttpMockServerComponent,
        () => {return new HttpMockServer()},
        () => {return HttpMockServerResourceType.getInstanceForRoot()},
        () => {return HttpMockServerResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpMockServer().deserialize(JsonUtil.parseJson(input))}
    );
    public static HTTP_MOCK_STUB_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("com.testerum.model.resources.http.mock.stub.HttpMock"),
        "com.testerum.model.resources.http.mock.stub.HttpMock",
        "HttpMock",
        "http.stub.yaml",
        HttpMockComponent,
        () => {return new HttpMock()},
        () => {return HttpMockStubResourceType.getInstanceForRoot()},
        () => {return HttpMockStubResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpMock().deserialize(JsonUtil.parseJson(input))}
    );
    public static JSON_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("net.qutester.model.resources.json.verify.JsonVerify"),
        "net.qutester.model.resources.json.verify.JsonVerify",
        "JsonVerify",
        "verify.json",
        JsonVerifyResourceComponent,
        () => {return new JsonVerify()},
        () => {return JsonVerifyResourceType.getInstanceForRoot()},
        () => {return JsonVerifyResourceType.getInstanceForChildren()},
        (input:string) => {return new JsonVerify().deserialize(input)}
    );
    public static JSON: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta("json.model.JsonResource"),
        "json.model.JsonResource",
        "JSON",
        "json",
        JsonResourceComponent,
        () => {return new BasicResource()},
        () => {return JsonResourceType.getInstanceForRoot()},
        () => {return JsonResourceType.getInstanceForChildren()},
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static LIST: ResourceMapEnum = new ResourceMapEnum(
        new ListTypeMeta(),
        "LIST",
        "List",
        null,
        ObjectResourceComponent,
        () => {return new ObjectResourceModel()},
        () => {return ObjectResourceType.getInstanceForRoot()},
        () => {return ObjectResourceType.getInstanceForChildren()},
        (input:string) => {return new ObjectResourceModel().deserialize(input)}
    );
    public static MAP: ResourceMapEnum = new ResourceMapEnum(
        new MapTypeMeta(),
        "MAP",
        "Map",
        null,
        ObjectResourceComponent,
        () => {return new ObjectResourceModel()},
        () => {return ObjectResourceType.getInstanceForRoot()},
        () => {return ObjectResourceType.getInstanceForChildren()},
        (input:string) => {return new ObjectResourceModel().deserialize(input)}
    );
    public static OBJECT: ResourceMapEnum = new ResourceMapEnum(
        new ObjectTypeMeta(),
        "OBJECT",
        "Custom",
        null,
        ObjectResourceComponent,
        () => {return new ObjectResourceModel()},
        () => {return ObjectResourceType.getInstanceForRoot()},
        () => {return ObjectResourceType.getInstanceForChildren()},
        (input:string) => {return new ObjectResourceModel().deserialize(input)}
    );
    public static ALL_PARAM_TYPES: Array<ResourceMapEnum> = [
        ResourceMapEnum.TEXT,
        ResourceMapEnum.NUMBER,
        ResourceMapEnum.ENUM,
        ResourceMapEnum.BOOLEAN,
        ResourceMapEnum.RDBMS_CONNECTION,
        ResourceMapEnum.RDBMS_SQL,
        ResourceMapEnum.RDBMS_VERIFY,
        ResourceMapEnum.HTTP_REQUEST,
        ResourceMapEnum.HTTP_RESPONSE_VERIFY,
        ResourceMapEnum.HTTP_MOCK_SERVER_VERIFY,
        ResourceMapEnum.HTTP_MOCK_STUB_VERIFY,
        ResourceMapEnum.JSON_VERIFY,
        ResourceMapEnum.JSON,
        ResourceMapEnum.LIST,
        ResourceMapEnum.MAP,
        ResourceMapEnum.OBJECT,
    ];

    static getResourceMapEnumByTypeMeta(serverType: TypeMeta): ResourceMapEnum {
        for (let paramType of ResourceMapEnum.ALL_PARAM_TYPES) {
            if (paramType.serverType.constructor == serverType.constructor) {
                if (serverType instanceof ObjectTypeMeta) {
                    if (serverType.javaType == paramType.serverType.javaType || // because http or other known resources are OBJECT TYPE
                        paramType.serverType.javaType == null) { //this case is to match the OBJECT TYPE RESOURCE
                        return paramType;
                    }
                } else {
                    return paramType;
                }
            }
        }
        return ResourceMapEnum.TEXT;
    }

    static getResourceMapEnumByUiType(uiType: string): ResourceMapEnum {
        for (let paramType of ResourceMapEnum.ALL_PARAM_TYPES) {
            if (paramType.uiType == uiType) {
                return paramType;
            }
        }
        return this.OBJECT;
    }

    static getResourceMapEnumByFileExtension(fileExtension: string): ResourceMapEnum {
        for (let paramType of ResourceMapEnum.ALL_PARAM_TYPES) {
            if (paramType.fileExtension == fileExtension) {
                return paramType;
            }
        }
        return null;
    }

    static getUiNameByUiType(uiType: string): string {
        let paramType = ResourceMapEnum.getResourceMapEnumByUiType(uiType);
        if(paramType == null) {
            return uiType;
        }

        return paramType.uiName;
    }

    public static deserializeInputForUiType(input: Object, uiType: string): Resource<any> {
        for (const argParamType of ResourceMapEnum.ALL_PARAM_TYPES) {
            if (argParamType.uiType === uiType) {
                return argParamType.contentTypeDeserializeFunction(input)
            }
        }

        return this.OBJECT.contentTypeDeserializeFunction(input)
    }

    public readonly serverType: TypeMeta;
    public readonly uiType: string;
    public readonly uiName: string;
    public readonly fileExtension: string;
    public readonly newInstanceFunction: () => Resource<any>;
    public readonly resourceTypeInstanceForRootFunction: () => ResourceType;
    public readonly resourceTypeInstanceForChildrenFunction: () => ResourceType;
    public readonly contentTypeDeserializeFunction: Function;


    private constructor(serverType: TypeMeta,
                        uiType: string,
                        uiName: string,
                        fileExtension: string,
                        public resourceComponent: Type<ResourceComponent<any>>,
                        newInstanceFunction: () => Resource<any>,
                        resourceTypeInstanceForRootFunction: () => ResourceType,
                        resourceTypeInstanceForChildrenFunction: () => ResourceType,
                        contentTypeDeserializeFunction: Function
                        ) {
        this.serverType = serverType;
        this.uiType = uiType;
        this.uiName = uiName;
        this.fileExtension = fileExtension;
        this.newInstanceFunction = newInstanceFunction;
        this.resourceTypeInstanceForRootFunction = resourceTypeInstanceForRootFunction;
        this.resourceTypeInstanceForChildrenFunction = resourceTypeInstanceForChildrenFunction;
        this.contentTypeDeserializeFunction = contentTypeDeserializeFunction;
    }

    getNewInstance(): Resource<any> {
        return this.newInstanceFunction();
    }

    getResourceTypeInstanceForRoot(): ResourceType {
        return this.resourceTypeInstanceForRootFunction();
    }

    getResourceTypeInstanceForChildren(): ResourceType {
        return this.resourceTypeInstanceForChildrenFunction();
    }
}
