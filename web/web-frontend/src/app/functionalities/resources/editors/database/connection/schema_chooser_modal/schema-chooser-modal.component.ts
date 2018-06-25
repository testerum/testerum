import {Component, ViewChild} from '@angular/core';
import {SchemaChooserModalListener} from "./schema-chooser-modal.listener";
import {ModalDirective} from "ngx-bootstrap";

@Component({
    moduleId: module.id,
    selector: 'schema-chooser-modal',
    templateUrl: 'schema-chooser-modal.component.html',
    styleUrls: ["schema-chooser-modal.component.css"]
})
export class SchemaChooserModalComponent {

    @ViewChild("schemaChooserModalComponent") schemaChooserModalComponent:ModalDirective;

    private listener: SchemaChooserModalListener;
    private schemas:Array<string> = [];
    private selectedSchema:string;

    public show(schemas:Array<string>, listener: SchemaChooserModalListener) {
        this.schemas = schemas;
        this.listener = listener;

        this.schemaChooserModalComponent.show();
    }

    selectSchema(schema:string) {
        this.selectedSchema = schema;
    }

    ok() {
        this.listener.schemaChooserEventListener(this.selectedSchema);
        this.schemaChooserModalComponent.hide();
    }

    cancel() {
        this.schemaChooserModalComponent.hide();
    }
}
