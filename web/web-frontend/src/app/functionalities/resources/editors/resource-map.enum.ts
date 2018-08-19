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
import {JsonVerifyComponent} from "./json_verify/json-verify.component";
import {ArrayJsonVerify} from "./json_verify/json-verify-tree/model/array-json-verify.model";
import {JsonVerifyResourceType} from "../tree/model/type/json-verify.resource-type.model";
import {BasicResourceType} from "../tree/model/type/basic.resource-type.model";
import {JsonVerify} from "./json_verify/model/json-verify.model";

export class ResourceMapEnum {
    public static TEXT: ResourceMapEnum = new ResourceMapEnum(
        "java.lang.String",
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
        "java.lang.Double",
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
        "ENUM",
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
        "java.lang.Boolean",
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
        "database.relational.connection_manager.model.RdbmsClient",
        "database.relational.connection_manager.model.RdbmsClient",
        "RdbmsClient",
        "rdbms.connection.yaml",
        RdbmsConnectionConfigComponent,
        () => {return new RdbmsConnectionConfig()},
        () => {return RdbmsConnectionResourceType.getInstanceForRoot()},
        () => {return RdbmsConnectionResourceType.getInstanceForChildren()},
        (input:string) => {return new RdbmsConnectionConfig().deserialize(JSON.parse(input))}
    );
    public static RDBMS_SQL: ResourceMapEnum = new ResourceMapEnum(
        "database.relational.model.RdbmsSql",
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
        "database.relational.model.RdbmsVerify",
        "database.relational.model.RdbmsVerify",
        "RdbmsVerify",
        "rdbms.verify.json",
        RdbmsVerifyComponent,
        () => {return new SchemaVerify()},
        () => {return RdbmsVerifyResourceType.getInstanceForRoot()},
        () => {return RdbmsVerifyResourceType.getInstanceForChildren()},
        (input:string) => {return new SchemaVerify().deserialize(JSON.parse(input))}
    );
    public static HTTP_REQUEST: ResourceMapEnum = new ResourceMapEnum(
        "com.testerum.model.resources.http.request.HttpRequest",
        "com.testerum.model.resources.http.request.HttpRequest",
        "HttpRequest",
        "http.request.yaml",
        HttpRequestComponent,
        () => {return new HttpRequest()},
        () => {return HttpRequestResourceType.getInstanceForRoot()},
        () => {return HttpRequestResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpRequest().deserialize(JSON.parse(input))}
    );
    public static HTTP_RESPONSE_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        "http.response.verify.model.HttpResponseVerify",
        "http.response.verify.model.HttpResponseVerify",
        "HttpResponseVerify",
        "http.response.verify.yaml",
        HttpResponseVerifyComponent,
        () => {return new HttpResponseVerify()},
        () => {return HttpResponseVerifyResourceType.getInstanceForRoot()},
        () => {return HttpResponseVerifyResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpResponseVerify().deserialize(JSON.parse(input))}
    );
    public static HTTP_MOCK_SERVER_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        "com.testerum.model.resources.http.mock.server.HttpMockServer",
        "com.testerum.model.resources.http.mock.server.HttpMockServer",
        "HttpMockServer",
        "http.mock.server.yaml",
        HttpMockServerComponent,
        () => {return new HttpMockServer()},
        () => {return HttpMockServerResourceType.getInstanceForRoot()},
        () => {return HttpMockServerResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpMockServer().deserialize(JSON.parse(input))}
    );
    public static HTTP_MOCK_STUB_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        "com.testerum.model.resources.http.mock.stub.HttpMock",
        "com.testerum.model.resources.http.mock.stub.HttpMock",
        "HttpStub",
        "http.stub.yaml",
        HttpMockComponent,
        () => {return new HttpMock()},
        () => {return HttpMockStubResourceType.getInstanceForRoot()},
        () => {return HttpMockStubResourceType.getInstanceForChildren()},
        (input:string) => {return new HttpMock().deserialize(JSON.parse(input))}
    );
    public static JSON_VERIFY: ResourceMapEnum = new ResourceMapEnum(
        "net.qutester.model.resources.json.verify.JsonVerify",
        "net.qutester.model.resources.json.verify.JsonVerify",
        "JsonVerify",
        "verify.json",
        JsonVerifyComponent,
        () => {return new JsonVerify()},
        () => {return JsonVerifyResourceType.getInstanceForRoot()},
        () => {return JsonVerifyResourceType.getInstanceForChildren()},
        (input:string) => {return new JsonVerify().deserialize(input)}
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
    ];

    static getResourceMapEnumByServerType(serverType: string): ResourceMapEnum {
        for (let paramType of ResourceMapEnum.ALL_PARAM_TYPES) {
            if (paramType.serverType == serverType) {
                return paramType;
            }
        }
        return null;
    }

    static getResourceMapEnumByUiType(uiType: string): ResourceMapEnum {
        for (let paramType of ResourceMapEnum.ALL_PARAM_TYPES) {
            if (paramType.uiType == uiType) {
                return paramType;
            }
        }
        return null;
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

        throw new Error("Unknown data SERVER_TYPE [" + uiType + "]");
    }

    public readonly serverType: string;
    public readonly uiType: string;
    public readonly uiName: string;
    public readonly fileExtension: string;
    public readonly newInstanceFunction: () => Resource<any>;
    public readonly resourceTypeInstanceForRootFunction: () => ResourceType;
    public readonly resourceTypeInstanceForChildrenFunction: () => ResourceType;
    public readonly contentTypeDeserializeFunction: Function;


    private constructor(serverType: string,
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
