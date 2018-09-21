import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {SchemaChooserModalListener} from "./schema-chooser-modal.listener";
import {SchemaChooserModalComponent} from "./schema-chooser-modal.component";
import {AppComponent} from "../../../../../../app.component";

@Injectable()
export class SchemaChooserModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showSchemaChooserModal(schemas:Array<string>, listener: SchemaChooserModalListener): void {
        const factory = this.componentFactoryResolver.resolveComponentFactory(SchemaChooserModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: SchemaChooserModalComponent = modalComponentRef.instance;

        modalInstance.schemas = schemas;
        modalInstance.listener = listener;

        modalInstance.modalComponentRef = modalComponentRef;
    }
}
