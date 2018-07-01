import {Component, OnInit} from '@angular/core';
import {ResourceEditor} from "../resource-editor.abstract-class";
import {ActivatedRoute, Router} from "@angular/router";
import {ResourceService} from "../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../tree/resources-tree.service";
import {ResourceContext} from "../../../../model/resource/resource-context.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ResourceType} from "../../tree/model/type/resource-type.model";
import {JsonVerifyTreeService} from "./json-verify-tree/json-verify-tree.service";
import {ArrayJsonVerify} from "./json-verify-tree/model/array-json-verify.model";
import {JsonVerifyResourceType} from "../../tree/model/type/json-verify.resource-type.model";
import {SerializationUtil} from "./json-verify-tree/model/util/serialization.util";
import {EmptyJsonVerify} from "./json-verify-tree/model/empty-json-verify.model";
import {JsonSchemaExtractor} from "./json-schema/json-schema.extractor";
import {JsonTreeNodeSerializable} from "../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";
import {FormValidationModel} from "../../../../model/exception/form-validation.model";
import {FormUtil} from "../../../../utils/form.util";
import {UrlService} from "../../../../service/url.service";

@Component({
    moduleId: module.id,
    selector: 'json-verify',
    templateUrl: 'json-verify.component.html',
    styleUrls: [
        'json-verify.component.css',
        '../../../../generic/css/generic.css',
        '../../../../generic/css/forms.css'
    ]
})
export class JsonVerifyComponent extends ResourceEditor<ArrayJsonVerify> implements OnInit {

    name: string;
    rootResourceType: ResourceType = JsonVerifyResourceType.getInstanceForRoot();
    childrenResourceType: ResourceType = JsonVerifyResourceType.getInstanceForChildren();

    sampleJsonText: string;

    constructor(private router: Router,
                private route: ActivatedRoute,
                private urlService: UrlService,
                private resourceService: ResourceService,
                private resourcesTreeService: ResourcesTreeService,
                private jsonVerifyTreeService: JsonVerifyTreeService) {
        super()
    }

    ngOnInit() {
        if (this.isModalMode) {
            this.initInCreateMode();

        } else {
            this.route.data.subscribe(data => {
                this.resource = data['resource'];
                this.setEditMode(this.resource.isCreateNewResource());
                this.initModelFromResource();
            });
        }
    }

    onSampleJsonTextChange(json: string) {
        this.sampleJsonText = json;
        let jsonRootNode;
        try {
            jsonRootNode = JSON.parse(json);
        } catch (e) {
            //ignore exception, JSON is not valid
            return;
        }
        let verifyJsonRoot = new SerializationUtil().deserialize(jsonRootNode);

        let jsonSchema = new JsonSchemaExtractor().getJsonSchemaFromJson(
            verifyJsonRoot
        );

        this.jsonVerifyTreeService.setJsonSchema(jsonSchema);

    }

    initInCreateMode(): void {
        this.setEditMode(true);
        this.resource = ResourceContext.createInstance(
            Path.createInstanceOfEmptyPath(),
            new EmptyJsonVerify()
        );
        this.initModelFromResource();
    }

    initFromResourcePath(resourcePath: Path): void {
        this.setEditMode(false);
        this.resourceService.getResource(resourcePath, new SerializationUtil).subscribe(
            result => {
                Object.assign(this.resource, result);

                this.initModelFromResource();
            }
        )
    }

    private initModelFromResource() {
        this.name = this.resource.path.fileName;

        let schemaVefiry = this.resource.body as JsonTreeNodeSerializable;
        this.jsonVerifyTreeService.setJsonVerifyRootResource(schemaVefiry);
    }

    isEmptyModel(): boolean {
        return this.jsonVerifyTreeService.isEmptyModel();
    }

    shouldDisplayJsonSample(): boolean {
        return this.isEditMode() && (this.isEmptyModel() || this.sampleJsonText != null);
    }

    cancelAction(): void {
        if (this.resource.isCreateNewResource()) {
            this.urlService.navigateToResources();
        } else {
            this.resourceService.getResource(this.resource.path, new SerializationUtil()).subscribe(
                result => {
                    this.jsonVerifyTreeService.empty();
                    Object.assign(this.resource, result);
                    this.setEditMode(false);
                    this.initModelFromResource();
                }
            )
        }
    }

    deleteAction(): void {
        this.resourceService.deleteResource(this.resource.path).subscribe(result => {
            this.resourcesTreeService.initializeResources(this.rootResourceType, this.childrenResourceType);
            this.urlService.navigateToResources();
        });
    }


    isFormValid(): boolean {
        let isFormValid = super.isFormValid();
        if (!isFormValid) {
            return false;
        }

        return this.jsonVerifyTreeService.isModelValid();
    }

    saveAction(): void {
        this.jsonVerifyTreeService.removeEmptyNodes();

        this.resource.path = new Path(this.resource.path.directories, this.name, this.rootResourceType.fileExtension);
        if (this.jsonVerifyTreeService.rootNode.children.length != 0) {
            this.resource.body = this.jsonVerifyTreeService.rootNode.children[0].serialize();
        }

        this.resourceService.saveResource(this.resource, new SerializationUtil()).subscribe(
            result => {
                Object.assign(this.resource, result);
                this.initModelFromResource();

                if (this.isModalMode) {
                    this.createResourceEventEmitter.emit(result.path)
                } else {
                    this.setEditMode(false);
                    this.resourcesTreeService.initializeResources(this.rootResourceType, this.childrenResourceType);
                    this.router.navigate([
                        this.rootResourceType.resourceUrl,
                        {"path": result.path}]);
                }
            },
            (formValidationModel: FormValidationModel) => {
                FormUtil.setErrorsToForm(this.form, formValidationModel);
            }
        );
    }

    setEditMode(isEditMode: boolean): void {
        this.editMode = isEditMode;
        this.jsonVerifyTreeService.editMode = isEditMode;
    }
}
