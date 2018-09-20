import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    OnDestroy,
    OnInit,
    Type,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {AreYouSureModalEnum} from "../../../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {ResourceMapEnum} from "../../resource-map.enum";
import {ActivatedRoute} from "@angular/router";
import {ResourceContext} from "../../../../../model/resource/resource-context.model";
import {ResourceComponent} from "../../resource-component.interface";
import {Subscription} from "rxjs";
import {ResourceService} from "../../../../../service/resources/resource.service";
import {FormUtil} from "../../../../../utils/form.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ResourcesTreeService} from "../../../tree/resources-tree.service";
import {UrlService} from "../../../../../service/url.service";
import {AreYouSureModalService} from "../../../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";

@Component({
    moduleId: module.id,
    selector: 'stand-alown-resource-panel',
    templateUrl: 'stand-alown-resource-panel.component.html',
    styleUrls: ['stand-alown-resource-panel.component.scss']
})

export class StandAlownResourcePanelComponent implements OnInit, OnDestroy {

    @ViewChild('panelBody', {read: ViewContainerRef}) bodyElement:ViewContainerRef;

    resource: ResourceContext<any>;
    resourceFileExtension:string;
    isEditMode: boolean;

    resourceComponentRef: ComponentRef<ResourceComponent<any>>;
    routeSubscription: Subscription;
    routeParamSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private componentFactoryResolver: ComponentFactoryResolver,
                private resourceService: ResourceService,
                private resourcesTreeService: ResourcesTreeService,
                private areYouSureModalService: AreYouSureModalService,) {
    }

    ngOnInit(): void {
        this.routeSubscription= this.route.data.subscribe(data => {
            this.resource = data['resource'];

            if (this.resource.isCreateNewResource()) {
                this.routeParamSubscription = this.route.params.subscribe(
                    params => {
                        this.resourceFileExtension = params["resourceExt"];
                        this.initialize();
                    }
                );
            } else {
                this.resourceFileExtension = this.resource.path.fileExtension;
                this.initialize();
            }
        });
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.routeParamSubscription) this.routeParamSubscription.unsubscribe();
    }

    private initialize() {
        this.isEditMode = this.resource.isCreateNewResource();

        let paramRendererContainer: Type<any> = this.getResourceRenderer();
        let resourceModel = this.resource.body.clone();

        const factory:ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(paramRendererContainer);
        this.bodyElement.clear();
        this.resourceComponentRef = this.bodyElement.createComponent(factory);

        this.resourceComponentRef.instance.model = resourceModel;
        this.resourceComponentRef.instance.name = this.resource.path.fileName;
        this.resourceComponentRef.instance.condensedViewMode = false;
        this.resourceComponentRef.instance.editMode  = this.isEditMode;
    }

    private getResourceRenderer():Type<any>  {
        let paramTypeEnumByType = ResourceMapEnum.getResourceMapEnumByFileExtension(this.resourceFileExtension);
        if(paramTypeEnumByType) {
            return paramTypeEnumByType.resourceComponent;
        }

        throw new Error("Unmapped resource renderer for file extension [" + this.resourceFileExtension + "] ");
    }

    setEditMode(): void {
        this.isEditMode = true;
        this.resourceComponentRef.instance.editMode = true;
        this.resourceComponentRef.instance.refresh();
    }

    deleteAction(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Delete Resource",
            "Are you sure you want to delete this resource?")
            .subscribe((action: AreYouSureModalEnum) => {
                if (action == AreYouSureModalEnum.OK) {
                    this.resourceService.deleteResource(this.resource.path).subscribe(result => {
                        this.refreshResourceTree();
                        this.urlService.navigateToResources();
                    });
                }
            });
    }

    isEditedResourceValid(): boolean {
        return this.resourceComponentRef.instance.isFormValid()
    }

    cancelAction() {
        if (this.resource.isCreateNewResource()) {
            this.urlService.navigateToResources();
        } else {
            this.initialize();
        }
    }

    saveAction() {
        this.resourceComponentRef.instance.onBeforeSave();

        this.resource.path = new Path(this.resource.path.directories, this.resourceComponentRef.instance.name, this.resourceFileExtension);
        this.resource.body = this.resourceComponentRef.instance.model.serialize();

        let newInstanceResource = ResourceMapEnum.getResourceMapEnumByFileExtension(this.resourceFileExtension).getNewInstance();
        this.resourceService.saveResource(this.resource, newInstanceResource).subscribe(
            result => {
                this.resource = result;

                this.urlService.navigateToResource(this.resource.path);
                this.initialize();
                this.refreshResourceTree();
            },
            (error: any) => {
                let form = this.resourceComponentRef.instance.getForm();
                if (!form) {
                    throw new Error("A form element should be define");
                }

                FormUtil.setErrorsToForm(form, error);
            }
        );
    }

    private refreshResourceTree() {
        let resourceType = ResourceMapEnum.getResourceMapEnumByFileExtension(this.resourceFileExtension);
        this.resourcesTreeService.initializeResources(
            resourceType.getResourceTypeInstanceForRoot(),
            resourceType.getResourceTypeInstanceForChildren()
        );
    }

    getResourceName(): string {
        let resourceType = ResourceMapEnum.getResourceMapEnumByFileExtension(this.resourceFileExtension);
        return resourceType.uiName
    }

    getPathForTitle(): string {
        let pathForTitle = "";
        if (this.resource.oldPath) {
            pathForTitle = new Path(this.resource.oldPath.directories, null, null).toString();
        }
        return pathForTitle;
    }

}
