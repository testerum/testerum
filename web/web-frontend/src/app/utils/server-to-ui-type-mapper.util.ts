export class ServerToUiTypeMapperUtil {

    private static SERVER_TYPE_TO_UI_TYPE_MAP: Map<string, string> = new Map<string, string>([
        ['TEXT'               , 'TEXT'],
        ['char'               , 'TEXT'],
        ['java.lang.Character', 'TEXT'],
        ['java.lang.String'   , 'TEXT'],

        ['NUMBER'           , 'NUMBER'],
        ['byte'             , 'NUMBER'],
        ['short'            , 'NUMBER'],
        ['int'              , 'NUMBER'],
        ['long'             , 'NUMBER'],
        ['float'            , 'NUMBER'],
        ['double'           , 'NUMBER'],
        ['java.lang.Byte'   , 'NUMBER'],
        ['java.lang.Short'  , 'NUMBER'],
        ['java.lang.Integer', 'NUMBER'],
        ['java.lang.Long'   , 'NUMBER'],
        ['java.lang.Float'  , 'NUMBER'],
        ['java.lang.Double' , 'NUMBER'],

        ['ENUM', 'ENUM'],

        ['BOOLEAN'          , 'BOOLEAN'],
        ['boolean'          , 'BOOLEAN'],
        ['java.lang.Boolean', 'BOOLEAN'],

        ['database.relational.connection_manager.model.RdbmsConnection', 'database.relational.connection_manager.model.RdbmsConnection'],

        ['database.relational.model.RdbmsSql', 'database.relational.model.RdbmsSql'],

        ['database.relational.model.RdbmsVerify', 'database.relational.model.RdbmsVerify'],

        ['com.testerum.model.resources.http.request.HttpRequest', 'com.testerum.model.resources.http.request.HttpRequest'],

        ['http.response.verify.model.HttpResponseVerify', 'http.response.verify.model.HttpResponseVerify'],

        ['com.testerum.model.resources.http.mock.server.HttpMockServer', 'com.testerum.model.resources.http.mock.server.HttpMockServer'],

        ['com.testerum.model.resources.http.mock.stub.HttpMock', 'com.testerum.model.resources.http.mock.stub.HttpMock'],

        ['json.model.JsonResource', 'json.model.JsonResource'],
    ]);

    private constructor() {}

    public static mapServerToUi(serverType: string): string {
        let uiType = ServerToUiTypeMapperUtil.SERVER_TYPE_TO_UI_TYPE_MAP.get(serverType);
        if (uiType === undefined) {
            return 'TEXT';
        }

        return uiType;
    }

}
