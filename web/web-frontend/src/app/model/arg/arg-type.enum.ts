import {HttpRequest} from "../resource/http/http-request.model";
import {BasicResource} from "../resource/basic/basic-resource.model";
import {HttpMock} from "../../functionalities/resources/editors/http/mock/stub/model/http-mock.model";
import {HttpMockServer} from "../../functionalities/resources/editors/http/mock/server/model/http-mock-server.model";
import {RdbmsConnectionConfig} from "../resource/rdbms/rdbms-connection-config.model";
import {SchemaVerify} from "../../functionalities/resources/editors/database/verify/rdbms-verify-tree/model/schema-verify.model";
import {Resource} from "../resource/resource.model";
import {HttpResponseVerify} from "../../functionalities/resources/editors/http/response_verify/model/http-response-verify.model";

export class ArgType {

    public static TEXT: ArgType = new ArgType(
        "TEXT",
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static NUMBER: ArgType = new ArgType(
        "NUMBER",
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static ENUM: ArgType = new ArgType(
        "ENUM",
        new BasicResource().deserialize
    );
    public static BOOLEAN: ArgType = new ArgType(
        "BOOLEAN",
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static RDBMS_CONNECTION: ArgType = new ArgType(
        "database.relational.connection_manager.model.RdbmsClient",
        (input:string) => {return new RdbmsConnectionConfig().deserialize(JSON.parse(input))}
    );
    public static RDBMS_SQL: ArgType = new ArgType(
        "database.relational.model.RdbmsSql",
        (input:string) => {return new BasicResource().deserialize(input)}
    );
    public static RDBMS_VERIFY: ArgType = new ArgType(
        "database.relational.model.RdbmsVerify",
        (input:string) => {return new SchemaVerify().deserialize(JSON.parse(input))}
    );
    public static HTTP_REQUEST: ArgType = new ArgType(
        "net.qutester.model.resources.http.request.HttpRequest",
        (input:string) => {return new HttpRequest().deserialize(JSON.parse(input))}
    );
    public static HTTP_RESPONSE_VERIFY: ArgType = new ArgType(
        "http.response.verify.model.HttpResponseVerify",
        (input:string) => {return new HttpResponseVerify().deserialize(JSON.parse(input))}
    );
    public static HTTP_HOCK_SERVER_VERIFY: ArgType = new ArgType(
        "net.qutester.model.resources.http.mock.server.HttpMockServer",
        (input:string) => {return new HttpMockServer().deserialize(JSON.parse(input))}
    );
    public static HTTP_HOCK_STUB_VERIFY: ArgType = new ArgType(
        "net.qutester.model.resources.http.mock.stub.HttpMock",
        (input:string) => {return new HttpMock().deserialize(JSON.parse(input))}
    );


    public static ALL_PARAM_TYPES: Array<ArgType> = [
        ArgType.TEXT,
        ArgType.NUMBER,
        ArgType.ENUM,
        ArgType.BOOLEAN,
        ArgType.RDBMS_CONNECTION,
        ArgType.RDBMS_SQL,
        ArgType.RDBMS_VERIFY,
        ArgType.HTTP_REQUEST,
        ArgType.HTTP_RESPONSE_VERIFY,
        ArgType.HTTP_HOCK_SERVER_VERIFY,
        ArgType.HTTP_HOCK_STUB_VERIFY,
    ];

    public readonly serverType: string;
    public readonly contentTypeDeserializeFunction: Function;

    constructor(serverType: string, contentTypeDeserializeFunction: Function) {
        this.serverType = serverType;
        this.contentTypeDeserializeFunction = contentTypeDeserializeFunction;
    }

    public static deserializeInputForServerType(input: Object, serverType: string): Resource<any> {
        for (const argParamType of ArgType.ALL_PARAM_TYPES) {
            if (argParamType.serverType === serverType) {
                return argParamType.contentTypeDeserializeFunction(input)
            }
        }

            throw new Error("Unknown data SERVER_TYPE [" + serverType + "]");
        }
}
