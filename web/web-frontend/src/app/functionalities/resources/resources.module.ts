import {NgModule} from '@angular/core';
import {ResourcesRoutingModule} from "./resources-routing.module";
import {ResourcesComponent} from "./resources.component";
import {AngularSplitModule} from "angular-split-ng6";
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
import {JsonVerifyResourceComponent} from "./editors/json_verify/json-verify-resource.component";
import {HttpRequestService} from "./editors/http/request/http-request.service";
import {HttpRequestComponent} from "./editors/http/request/http-request.component";
import {HttpHeaderComponent} from "./editors/http/request/header/http-header.component";
import {HttpBodyComponent} from "./editors/http/request/body/http-body.component";
import {BsDropdownModule} from "ngx-bootstrap";
import {HttpBodyTypeFormComponent} from "./editors/http/request/body-type-form/http-body-type-form.component";
import {HttpParamsComponent} from "./editors/http/request/params/http-params.component";
import {AutoCompleteModule, DropdownModule, ToggleButtonModule, ToolbarModule} from "primeng/primeng";
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
import {SchemaChooserModalService} from "./editors/database/connection/schema_chooser_modal/schema-chooser-modal.service";

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
        ToggleButtonModule,
        ToolbarModule,
        DropdownModule,

        GenericModule,
    ],
    entryComponents: [
        RdbmsVerifySchemaNodeComponent,
        RdbmsVerifyTableNodeComponent,
        RdbmsVerifyTableRowNodeComponent,
        RdbmsVerifyFieldNodeComponent,

        HttpMockServerComponent,
        HttpMockComponent,
        HttpResponseVerifyComponent,
        RdbmsConnectionConfigComponent,
        RdbmsSqlComponent,
        RdbmsVerifyComponent,
        BasicResourceComponent,
        JsonVerifyResourceComponent,

        SchemaChooserModalComponent,
    ],
    exports: [
        RdbmsConnectionConfigComponent,
        RdbmsSqlComponent,
        RdbmsVerifyComponent,

        BasicResourceComponent,
        JsonVerifyResourceComponent,
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

        JsonVerifyResourceComponent,

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
        SchemaChooserModalService,
        HttpRequestService,
        HttpResponseVerifyService,
        HttpMockService,

        HttpMockRequestScenarioService,

        ResourceResolver,
    ],
})
export class ResourcesModule {
}
