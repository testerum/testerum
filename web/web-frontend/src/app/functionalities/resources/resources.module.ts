import {NgModule} from '@angular/core';
import {ResourcesRoutingModule} from "./resources-routing.module";
import {ResourcesComponent} from "./resources.component";
import {AngularSplitModule} from "angular-split";
import {DndModule} from "ng2-dnd";
import {ModalModule, TabsModule} from "ngx-bootstrap";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {GenericModule} from "../../generic/generic.module";
import {ResourcesTreeService} from "./tree/resources-tree.service";
import {ResourcesContainerComponent} from "./tree/container/resources-container.component";
import {RdbmsConnectionConfigComponent} from "./editors/database/connection/rdbms-connection-config.component";
import {ResourceNodeComponent} from "./tree/container/node/resource-node.component";
import {SchemaChooserModalComponent} from "./editors/database/connection/schema_chooser_modal/schema-chooser-modal.component";
import {RdbmsSqlComponent} from "./editors/database/sql/rdbms-sql.component";
import {RdbmsSqlEditorComponent} from "./editors/database/sql/editor/rdbms-sql-editor.component";
import {AceEditorModule} from "ng2-ace-editor";
import {RdbmsVerifyComponent} from "./editors/database/verify/rdbms-verify.component";
import {RdbmsVerifyTreeComponent} from "./editors/database/verify/rdbms-verify-tree/rdbms-verify-tree.component";
import {RdbmsVerifySchemaNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/rdbms-verify-schema-node.component";
import {RdbmsVerifyTableNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/table-node/rdbms-verify-table-node.component";
import {RdbmsVerifyTableRowNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/table-node/table-row/rdbms-verify-table-row-node.component";
import {RdbmsVerifyFieldNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/table-node/table-row/field/rdbms-verify-field-node.component";
import {RdbmsVerifyTreeService} from "./editors/database/verify/rdbms-verify-tree/rdbms-verify-tree.service";
import {JsonVerifyRootComponent} from "./editors/json_verify/json-verify-root.component";
import {JsonVerifyComponent} from "./editors/json_verify/json-verify.component";
import {JsonVerifyTreeComponent} from "./editors/json_verify/json-verify-tree/json-verify-tree.component";
import {JsonArrayVerifyNodeComponent} from "./editors/json_verify/json-verify-tree/node-component/array-node/json-array-verify-node.component";
import {JsonObjectVerifyNodeComponent} from "./editors/json_verify/json-verify-tree/node-component/object-node/json-object-verify-node.component";
import {JsonVerifyResolver} from "./editors/json_verify/json-verify.resolver";
import {JsonVerifyTreeService} from "./editors/json_verify/json-verify-tree/json-verify-tree.service";
import {JsonEditorComponent} from "./editors/json_verify/editor/json-editor.component";
import {JsonFieldVerifyNodeComponent} from "./editors/json_verify/json-verify-tree/node-component/field-node/json-field-verify-node.component";
import {JsonPrimitiveVerifyNodeComponent} from "./editors/json_verify/json-verify-tree/node-component/primitive-node/json-primitive-verify-node.component";
import {JsonEmptyVerifyNodeComponent} from "./editors/json_verify/json-verify-tree/node-component/emtpy-node/json-empty-verify-node.component";
import {HttpRequestService} from "./editors/http/request/http-request.service";
import {HttpRequestComponent} from "./editors/http/request/http-request.component";
import {HttpHeaderComponent} from "./editors/http/request/header/http-header.component";
import {HttpBodyComponent} from "./editors/http/request/body/http-body.component";
import {BsDropdownModule} from "ngx-bootstrap";
import {HttpBodyTypeFormComponent} from "./editors/http/request/body-type-form/http-body-type-form.component";
import {HttpParamsComponent} from "./editors/http/request/params/http-params.component";
import {AutoCompleteModule} from "primeng/primeng";
import {HttpResponseComponent} from "./editors/http/request/response/http-request-response.component";
import {HttpResponseVerifyComponent} from "./editors/http/response_verify/http-response-verify.component";
import {HttpResponseVerifyService} from "./editors/http/response_verify/http-response-verify.service";
import {HttpResponseVerifyHeaderComponent} from "./editors/http/response_verify/header/http-response-verify-header.component";
import {HttpMockComponent} from "./editors/http/mock/stub/http-mock.component";
import {HttpMockService} from "./editors/http/mock/stub/http-mock.service";
import {HttpMockRequestHeaderComponent} from "./editors/http/mock/stub/request/request-headers/http-mock-request-header.component";
import {HttpMockRequestParamsComponent} from "./editors/http/mock/stub/request/request-params/http-mock-request-params.component";
import {HttpMockRequestBodyComponent} from "./editors/http/mock/stub/request/request-body/http-mock-request-body.component";
import {HttpResponseVerifyBodyComponent} from "./editors/http/response_verify/body/http-response-verify-body.component";
import {HttpMockResponseComponent} from "./editors/http/mock/stub/response/http-mock-response.component";
import {HttpMockResponseHeadersComponent} from "./editors/http/mock/stub/response/mock/headers/http-mock-response-headers.component";
import {HttpMockResponseBodyComponent} from "./editors/http/mock/stub/response/mock/body/http-mock-response-body.component";
import {HttpMockRequestComponent} from "./editors/http/mock/stub/request/http-mock-request.component";
import {HttpMockFaultResponseComponent} from "./editors/http/mock/stub/response/fault/http-mock-fault-response.component";
import {HttpMockProxyResponseComponent} from "./editors/http/mock/stub/response/proxy/http-mock-proxy-response.component";
import {HttpMockRequestScenarioService} from "./editors/http/mock/stub/request/request-scenario/http-mock-request-scenario.service";
import {HttpMockRequestScenarioComponent} from "./editors/http/mock/stub/request/request-scenario/http-mock-request-scenario.component";
import {HttpMockServerComponent} from "./editors/http/mock/server/http-mock-server.component";
import {StandAlownResourcePanelComponent} from "./editors/infrastructure/form-panel-container/stand-alown-resource-panel.component";
import {ResourceResolver} from "./editors/resource.resolver";
import {BasicResourceComponent} from "./editors/basic/basic-resource.component";
import {ResourcesTreeComponent} from "./tree/resources-tree.component";

@NgModule({
    imports: [
        ResourcesRoutingModule,

        BrowserModule,
        FormsModule,
        ModalModule.forRoot(),
        TabsModule.forRoot(),
        BsDropdownModule.forRoot(),
        DndModule.forRoot(),
        AngularSplitModule,
        AceEditorModule,
        AutoCompleteModule,

        GenericModule,

    ],
    entryComponents: [
        RdbmsVerifySchemaNodeComponent,
        RdbmsVerifyTableNodeComponent,
        RdbmsVerifyTableRowNodeComponent,
        RdbmsVerifyFieldNodeComponent,

        JsonArrayVerifyNodeComponent,
        JsonObjectVerifyNodeComponent,
        JsonFieldVerifyNodeComponent,
        JsonPrimitiveVerifyNodeComponent,
        JsonEmptyVerifyNodeComponent,

        HttpMockServerComponent,
        HttpMockComponent,
        HttpResponseVerifyComponent,
        RdbmsConnectionConfigComponent,
        RdbmsSqlComponent,
        RdbmsVerifyComponent,
        BasicResourceComponent,
        JsonVerifyComponent,

    ],
    exports: [
        RdbmsConnectionConfigComponent,
        RdbmsSqlComponent,
        RdbmsVerifyComponent,

        BasicResourceComponent,
        JsonVerifyComponent,
        HttpRequestComponent,
        HttpResponseVerifyComponent,
        HttpMockComponent,
        HttpMockServerComponent,

    ],
    declarations: [
        ResourcesTreeComponent,
        ResourcesComponent,
        ResourcesContainerComponent,
        ResourceNodeComponent,

        StandAlownResourcePanelComponent,

        RdbmsConnectionConfigComponent,
        SchemaChooserModalComponent,

        RdbmsSqlComponent,
        RdbmsSqlEditorComponent,

        RdbmsVerifyComponent,
        RdbmsVerifyTreeComponent,
        RdbmsVerifySchemaNodeComponent,
        RdbmsVerifyTableNodeComponent,
        RdbmsVerifyTableRowNodeComponent,
        RdbmsVerifyFieldNodeComponent,

        JsonVerifyComponent,
        JsonVerifyRootComponent,
        JsonVerifyTreeComponent,
        JsonEditorComponent,
        JsonArrayVerifyNodeComponent,
        JsonObjectVerifyNodeComponent,
        JsonFieldVerifyNodeComponent,
        JsonPrimitiveVerifyNodeComponent,
        JsonEmptyVerifyNodeComponent,

        HttpRequestComponent,
        HttpHeaderComponent,
        HttpBodyComponent,
        HttpBodyTypeFormComponent,
        HttpParamsComponent,
        HttpResponseComponent,

        HttpResponseVerifyComponent,
        HttpResponseVerifyHeaderComponent,
        HttpResponseVerifyBodyComponent,

        HttpMockComponent,
        HttpMockRequestComponent,
        HttpMockRequestParamsComponent,
        HttpMockRequestHeaderComponent,
        HttpMockRequestBodyComponent,
        HttpMockRequestScenarioComponent,
        HttpMockResponseComponent,
        HttpMockResponseHeadersComponent,
        HttpMockResponseBodyComponent,
        HttpMockFaultResponseComponent,
        HttpMockProxyResponseComponent,

        HttpMockServerComponent,

        BasicResourceComponent,
    ],
    providers: [
        ResourcesTreeService,
        RdbmsVerifyTreeService,
        JsonVerifyTreeService,
        HttpRequestService,
        HttpResponseVerifyService,
        HttpMockService,

        JsonVerifyResolver,//TODO Ionut: after INLINE check if is still need it
        HttpMockRequestScenarioService,

        ResourceResolver,
    ],
})
export class ResourcesModule {
}
