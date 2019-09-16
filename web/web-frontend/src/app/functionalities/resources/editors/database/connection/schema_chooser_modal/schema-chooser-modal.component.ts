import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {SchemaChooserModalListener} from "./schema-chooser-modal.listener";
import {ModalDirective} from "ngx-bootstrap";

@Component({
    moduleId: module.id,
    selector: 'schema-chooser-modal',
    templateUrl: 'schema-chooser-modal.component.html',
    styleUrls: ["schema-chooser-modal.component.scss"]
})
export class SchemaChooserModalComponent implements AfterViewInit {

    @ViewChild("schemaChooserModalComponent", { static: false }) modal: ModalDirective;

    listener: SchemaChooserModalListener;
    schemas:Array<string> = [];
    selectedSchema:string;

    modalComponentRef: ComponentRef<SchemaChooserModalComponent>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
        })
    }

    selectSchema(schema:string) {
        this.selectedSchema = schema;
    }

    ok() {
        this.listener.schemaChooserEventListener(this.selectedSchema);
        this.modal.hide();
    }

    cancel() {
        this.modal.hide();
    }
}
