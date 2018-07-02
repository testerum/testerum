import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    ElementRef,
    Type,
    ViewChild,
    ViewContainerRef
} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {Arg} from "../../../../model/arg/arg.model";
import {ResourceComponent} from "../../../../functionalities/resources/editors/resource-component.interface";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";
import {ObjectUtil} from "../../../../utils/object.util";
import {ResourceService} from "../../../../service/resources/resource.service";
import {NewSharedResourcePathModalComponent} from "../new-shared-resource-path-modal/new-shared-resource-path-modal.component";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Resource} from "../../../../model/resource/resource.model";
import {SelectSharedResourceModalComponent} from "../select-shared-resource-modal/select-shared-resource-modal.component";
import {ResourceContext} from "../../../../model/resource/resource-context.model";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {BasicResource} from "../../../../model/resource/basic/basic-resource.model";

@Component({
    moduleId: module.id,
    selector: 'arg-modal',
    templateUrl: 'arg-modal.component.html',
    styleUrls: [
        'arg-modal.component.css',
        '../../../../generic/css/generic.css'
    ],
})
export class ArgModalComponent {

    modalTitle = "HTTP Request";
    arg:Arg;
    stepParameter: ParamStepPatternPart;

    isEditMode:boolean = true;
    isSharedResource = false;
    sharedResourcePathAsString: string;
    resourceMapping: ResourceMapEnum;

    resourceComponentRef: ComponentRef<ResourceComponent<any>>;
    @ViewChild("resourceModal") resourceModal: ModalDirective;
    @ViewChild("isSharedResourceInput") isSharedResourceInput: ElementRef;
    @ViewChild('modalBody', {read: ViewContainerRef}) modalBodyElement:ViewContainerRef;
    @ViewChild(NewSharedResourcePathModalComponent) newSharedResourcePathModal: NewSharedResourcePathModalComponent;
    @ViewChild(SelectSharedResourceModalComponent) selectSharedResourceModal: SelectSharedResourceModalComponent;


    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private resourceService: ResourceService) {
    }

    show() {
        this.resourceMapping = ResourceMapEnum.getResourceMapEnumByServerType(this.arg.uiType);

        this.initializeIsSharedResource();

        let argName = this.getArgName();

        this.setResourceComponentInBody(this.arg.content, argName);

        this.resourceModal.show();
    }

    getArgName() {
        let argName = this.arg.name;
        if (!this.arg.name && this.arg.path) {
            argName = this.arg.path.fileName;
        }
        return argName;
    }

    private initializeIsSharedResource() {
        this.setIsSharedResourceValue (this.arg.path != null);
        this.sharedResourcePathAsString = this.arg.path ? "/"+this.arg.path.toString() : "";
    }

    private setIsSharedResourceValue(value: boolean) {
        this.isSharedResource = value;
        if(this.isSharedResourceInput) {
            this.isSharedResourceInput.nativeElement.checked = value;
        }
        if (this.resourceComponentRef) {
            this.resourceComponentRef.instance.isSharedResource = this.isSharedResource;
            if (this.resourceComponentRef.instance.refresh) {
                this.resourceComponentRef.instance.refresh()
            }
        }
    }

    private setResourceComponentInBody(resourceModelInBody: Resource<any>, name: string) {

        if (this.resourceComponentRef) {
            this.resourceComponentRef.destroy()
        }
        let paramRendererContainer: Type<any> = this.resourceMapping.resourceComponent;

        let resourceModel = resourceModelInBody.clone();

        const factory:ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(paramRendererContainer);
        this.resourceComponentRef = this.modalBodyElement.createComponent(factory);

        this.resourceComponentRef.instance.model = resourceModel;
        this.resourceComponentRef.instance.stepParameter = this.stepParameter;
        this.resourceComponentRef.instance.name = name;
        this.resourceComponentRef.instance.condensedViewMode = false;
        this.resourceComponentRef.instance.editMode = this.isEditMode;
        this.resourceComponentRef.instance.isSharedResource = this.isSharedResource;
        if (this.resourceComponentRef.instance.refresh) {
            this.resourceComponentRef.instance.refresh()
        }
    }

    toggleSharedResource() {
        if (this.isSharedResource) {
            this.arg.path = null;
            this.setIsSharedResourceValue(false);
        } else {
            this.newSharedResourcePathModal.resourceMapping = this.resourceMapping;
            this.newSharedResourcePathModal.show().subscribe(
                (selectedPath: Path) => {
                    this.arg.path = selectedPath;
                    this.initializeIsSharedResource();

                    this.resourceComponentRef.instance.name = this.arg.path.fileName;
                    if (this.resourceComponentRef.instance.refresh) {
                        this.resourceComponentRef.instance.refresh()
                    }
                }
            );
        }
    }

    selectSharedResource() {
        this.selectSharedResourceModal.resourceMapping = this.resourceMapping;
        this.selectSharedResourceModal.show().subscribe(
            (selectedPath: Path) => {
                if (selectedPath != null) {
                    this.resourceService.getResource(selectedPath, this.arg.content.createInstance()).subscribe(
                        (resource: ResourceContext<any>) => {
                            this.setResourceComponentInBody(resource.body, selectedPath.fileName);
                            this.arg.path = selectedPath;
                            this.initializeIsSharedResource()
                        }
                    )
                }
            }
        );
    }

    update() {
        this.arg.name = this.resourceComponentRef.instance.name;

        let resource = this.resourceComponentRef.instance.model;
        if(ObjectUtil.hasAMethodCalled(resource, "clone")) {
            let serializedResource = resource.serialize();
            let resourceAsJson = resource instanceof BasicResource ? serializedResource : JSON.parse(serializedResource);
            this.arg.content.reset();
            this.arg.content.deserialize(resourceAsJson)
        } else {
            this.arg.content = resource
        }
        this.resourceModal.hide();
    }

    cancel() {
        this.resourceModal.hide();
    }

    isValid(): boolean {
        if (this.resourceComponentRef) {
            return this.resourceComponentRef.instance.isFormValid();
        }

        return false;
    }

    canBeSharedResource(): boolean {
        if (!this.resourceMapping) {
            return false;
        }
        return this.resourceMapping.fileExtension != null;
    }
}
