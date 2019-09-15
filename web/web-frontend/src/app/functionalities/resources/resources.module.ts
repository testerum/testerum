import {NgModule} from '@angular/core';
import {ResourcesRoutingModule} from "./resources-routing.module";
import {ResourcesComponent} from "./resources.component";
import {DndModule} from "ng2-dnd";
import {BsDropdownModule, ModalModule, TabsModule} from "ngx-bootstrap";
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
import {RdbmsVerifyComponent} from "./editors/database/verify/rdbms-verify.component";
import {RdbmsVerifyTreeComponent} from "./editors/database/verify/rdbms-verify-tree/rdbms-verify-tree.component";
import {RdbmsVerifySchemaNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/rdbms-verify-schema-node.component";
import {RdbmsVerifyTableNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/table-node/rdbms-verify-table-node.component";
import {RdbmsVerifyTableRowNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/table-node/table-row/rdbms-verify-table-row-node.component";
import {RdbmsVerifyFieldNodeComponent} from "./editors/database/verify/rdbms-verify-tree/schema-node/table-node/table-row/field/rdbms-verify-field-node.component";
import {RdbmsVerifyTreeService} from "./editors/database/verify/rdbms-verify-tree/rdbms-verify-tree.service";
import {JsonVerifyResourceComponent} from "./editors/json/json_verify/json-verify-resource.component";
import {HttpRequestComponent} from "./editors/http/request/http-request.component";
import {HttpHeaderComponent} from "./editors/http/request/header/http-header.component";
import {HttpBodyComponent} from "./editors/http/request/body/http-body.component";
import {HttpBodyTypeFormComponent} from "./editors/http/request/body-type-form/http-body-type-form.component";
import {HttpParamsComponent} from "./editors/http/request/params/http-params.component";
import {
    AutoCompleteModule, CalendarModule,
    CheckboxModule,
    DropdownModule,
    ToggleButtonModule,
    ToolbarModule,
    TooltipModule
} from "primeng/primeng";
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
import {StandAloneResourcePanelComponent} from "./editors/infrastructure/form-panel-container/stand-alone-resource-panel.component";
import {ResourceResolver} from "./editors/resource.resolver";
import {BasicResourceComponent} from "./editors/basic/basic-resource.component";
import {ResourcesTreeComponent} from "./tree/resources-tree.component";
import {SchemaChooserModalService} from "./editors/database/connection/schema_chooser_modal/schema-chooser-modal.service";
import {JsonResourceComponent} from "./editors/json/json_resource/json-resource.component";
import {AngularSplitModule} from "angular-split";
import {ObjectResourceComponent} from "./editors/object/object-resource.component";
import {StringObjectTreeNodeComponent} from "./editors/object/nodes/string-node/string-object-tree-node.component";
import {ObjectObjectTreeNodeComponent} from "./editors/object/nodes/object-node/object-object-tree-node.component";
import {EnumObjectTreeNodeComponent} from "./editors/object/nodes/enum-node/enum-object-tree-node.component";
import {BooleanObjectTreeNodeComponent} from "./editors/object/nodes/boolean-node/boolean-object-tree-node.component";
import {DateObjectTreeNodeComponent} from "./editors/object/nodes/date-node/date-object-tree-node.component";

@NgModule({
    imports: [
        ResourcesRoutingModule,

        BrowserModule,
        FormsModule,
        ModalModule.forRoot(),
        TabsModule.forRoot(),
        BsDropdownModule.forRoot(),
        DndModule.forRoot(),
        AngularSplitModule.forRoot(),
        CheckboxModule,

        AutoCompleteModule,
        ToggleButtonModule,
        ToolbarModule,
        DropdownModule,
        TooltipModule,
        CalendarModule,

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
        JsonResourceComponent,

        SchemaChooserModalComponent,

        ObjectResourceComponent,
        BooleanObjectTreeNodeComponent,
        DateObjectTreeNodeComponent,
        EnumObjectTreeNodeComponent,
        StringObjectTreeNodeComponent,
        ObjectObjectTreeNodeComponent,
    ],
    exports: [
        RdbmsConnectionConfigComponent,
        RdbmsSqlComponent,
        RdbmsVerifyComponent,

        BasicResourceComponent,
        JsonVerifyResourceComponent,
        JsonResourceComponent,
        HttpRequestComponent,
        HttpResponseVerifyComponent,
        HttpMockComponent,
        HttpMockServerComponent,

        ObjectResourceComponent,

    ],
    declarations: [
        ResourcesTreeComponent,
        ResourcesComponent,
        ResourcesContainerComponent,
        ResourceNodeComponent,

        StandAloneResourcePanelComponent,

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
        JsonResourceComponent,

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

        ObjectResourceComponent,
        BooleanObjectTreeNodeComponent,
        DateObjectTreeNodeComponent,
        EnumObjectTreeNodeComponent,
        StringObjectTreeNodeComponent,
        ObjectObjectTreeNodeComponent,
    ],
    providers: [
        ResourcesTreeService,
        RdbmsVerifyTreeService,
        SchemaChooserModalService,
        HttpResponseVerifyService,
        HttpMockService,

        HttpMockRequestScenarioService,

        ResourceResolver,
    ],
})
export class ResourcesModule {
}
