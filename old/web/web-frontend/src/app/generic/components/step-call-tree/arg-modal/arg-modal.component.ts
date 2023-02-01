import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
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
import {Subject} from "rxjs";
import {ArgModalEnum} from "./enum/arg-modal.enum";
import {ObjectResourceModel} from "../../../../functionalities/resources/editors/object/object-resource.model";
import {ResourceContextActions} from "../../../../functionalities/resources/editors/infrastructure/model/resource-context-actions.model";

@Component({
    moduleId: module.id,
    selector: 'arg-modal',
    templateUrl: 'arg-modal.component.html',
    styleUrls: [
        'arg-modal.component.scss'
    ],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class ArgModalComponent {

    arg:Arg;
    stepParameter: ParamStepPatternPart;

    modalComponentRef: ComponentRef<ArgModalComponent>;
    modalSubject:Subject<ArgModalEnum>;

    modalTitle;

    argPath: Path;
    isEditMode:boolean = true;
    isSharedResource = false;
    sharedResourcePathAsString: string;
    resourceMapping: ResourceMapEnum;

    resourceComponentRef: ComponentRef<ResourceComponent<any>>;
    @ViewChild("resourceModal", { static: true }) resourceModal: ModalDirective;
    @ViewChild("isSharedResourceInput", { static: true }) isSharedResourceInput: ElementRef;
    @ViewChild('modalBody', { read: ViewContainerRef, static: true }) modalBodyElement:ViewContainerRef;
    @ViewChild(NewSharedResourcePathModalComponent, { static: true }) newSharedResourcePathModal: NewSharedResourcePathModalComponent;
    @ViewChild(SelectSharedResourceModalComponent, { static: true }) selectSharedResourceModal: SelectSharedResourceModalComponent;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private resourceService: ResourceService,
                private cd: ChangeDetectorRef) {
    }

    ngAfterViewInit(): void {
        this.resourceMapping = ResourceMapEnum.getResourceMapEnumByUiType(this.arg.uiType);
        this.argPath = this.arg.path;
        this.modalTitle = this.resourceMapping.uiName;

        this.initializeIsSharedResource();

        let argName = this.getArgName();

        this.setResourceComponentInBody(this.arg.content, argName);

        this.resourceModal.show();
        this.resourceModal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    getArgName() {
        let argName = this.arg.name;
        if (!argName && this.stepParameter) {
            argName = this.stepParameter.name
        }
        if (!argName && this.argPath) {
            argName = this.argPath.fileName;
        }
        return argName;
    }

    private initializeIsSharedResource() {
        this.setIsSharedResourceValue (this.argPath != null);
        this.sharedResourcePathAsString = this.argPath ? "/"+this.argPath.toString() : "";
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
        this.resourceComponentRef.instance.contextActions = this.getContextActions();
        if (this.resourceComponentRef.instance.refresh) {
            this.resourceComponentRef.instance.refresh()
        }
    }

    toggleSharedResource() {
        if (this.isSharedResource) {
            this.argPath = null;
            this.setIsSharedResourceValue(false);
        } else {
            this.newSharedResourcePathModal.resourceMapping = this.resourceMapping;
            this.newSharedResourcePathModal.show().subscribe(
                (selectedPath: Path) => {
                    this.argPath = selectedPath;
                    this.initializeIsSharedResource();

                    if (this.argPath) {
                        this.resourceComponentRef.instance.name = this.argPath.fileName;
                    }
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
                if (selectedPath != null && selectedPath.isFile()) {
                    this.resourceService.getResource(selectedPath, this.arg.content.createInstance()).subscribe(
                        (resource: ResourceContext<any>) => {
                            this.setResourceComponentInBody(resource.body, selectedPath.fileName);
                            this.argPath = selectedPath;
                            this.initializeIsSharedResource();

                            if (this.resourceComponentRef.instance.refresh) {
                                this.resourceComponentRef.instance.refresh()
                            }
                            this.refresh();
                        }
                    )
                }
            }
        );
    }

    update() {
        this.resourceComponentRef.instance.onBeforeSave();

        let argName = null;

        if ((this.resourceComponentRef.instance.model instanceof BasicResource) &&
            this.resourceComponentRef.instance.name != this.arg.name) { //do not save the name for the basic resources
            argName = this.resourceComponentRef.instance.name;
        }

        if (!(this.resourceComponentRef.instance.model instanceof BasicResource)) { //do not save the name for the basic resources
            argName = this.resourceComponentRef.instance.name;
        }


        this.arg.name = argName;
        this.arg.path = this.argPath;
        this.arg.oldPath = this.argPath;

        let resource = this.resourceComponentRef.instance.model;
        if(ObjectUtil.hasAMethodCalled(resource, "clone")) {
            let serializedResource = resource.serialize();
            let resourceAsJson = resource instanceof BasicResource || resource instanceof ObjectResourceModel ? serializedResource : JSON.parse(serializedResource);
            this.arg.content.reset();
            this.arg.content.deserialize(resourceAsJson)
        } else {
            this.arg.content = resource
        }

        this.resourceComponentRef.instance.refresh();

        this.modalSubject.next(ArgModalEnum.OK);
        this.resourceModal.hide();
    }

    cancel() {
        this.modalSubject.next(ArgModalEnum.CANCEL);
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

    private getContextActions(): ResourceContextActions {
        let that = this;
        return new class implements ResourceContextActions {
            cancel() {
                that.cancel();
            }

            save() {
                that.update();
            }
        };
    }
}
