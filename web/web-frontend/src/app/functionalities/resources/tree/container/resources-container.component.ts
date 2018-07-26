import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {ResourcesTreeContainer} from "../model/resources-tree-container.model";
import {ResourcesTreeService} from "../resources-tree.service";
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../../../generic/components/json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ResourceType} from "../model/type/resource-type.model";
import {ResourceService} from "../../../../service/resources/resource.service";
import {RenamePath} from "../../../../model/infrastructure/path/rename-path.model";
import {ResourcesTreeNode} from "../model/resources-tree-node.model";
import {CopyPath} from "../../../../model/infrastructure/path/copy-path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {ArrayUtil} from "../../../../utils/array.util";
import {JsonTreePathContainer} from "../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

@Component({
    moduleId: module.id,
    selector: 'resources-container',
    templateUrl: 'resources-container.component.html',
    styleUrls: [
        'resources-container.component.scss',
        '../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../generic/css/tree.scss'
    ]
})
export class ResourcesContainerComponent {

    @Input() model: ResourcesTreeContainer;
    hasMouseOver: boolean = false;

    constructor(private router: Router,
                private jsonTreeService: JsonTreeService,
                private resourcesTreeService: ResourcesTreeService,
                private resourceService: ResourceService) {
    }

    allowDrop():any {
        return (dragData: ResourcesTreeNode) => {
            return !JsonTreePathUtil.isChildOf(dragData, this.model) &&
                    !JsonTreePathUtil.isDirectChildOf(this.model, dragData) &&
                    this.model !== dragData}
    }

    showCreateSubResource() {
        let resourceType = this.model.resourceType;

        this.router.navigate(
            [
                "/automated/resources/create",
                {
                    "path":this.model.path,
                    "resourceExt":this.model.resourceType.fileExtension,
                }
            ]
        );
    }

    showCreateDirectoryModal(): void {
        let childrenContainersName = this.getChildrenContainersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {
                let pathDirectories: Array<string> = ArrayUtil.copyArray(this.model.path.directories);
                pathDirectories.push(createEvent.newName);

                let parentResourceType = this.model.resourceType;

                let resourceType: ResourceType = parentResourceType.getResourceTypeForChildren();

                this.model.children.push(new ResourcesTreeContainer(
                    this.model,
                    resourceType,
                    new Path(pathDirectories, null, null)
                ));
                this.model.sort();
            }
        )
    }

    private getChildrenContainersName() {
        let childrenContainersName: Array<string> = [];
        for (const child of this.model.children) {
            if (child.isContainer()) {
                childrenContainersName.push(child.name)
            }
        }
        return childrenContainersName;
    }

    showEditDirectoryNameModal(): void {
        let siblingNames = this.getSiblingNames();

        this.jsonTreeService.triggerUpdateContainerAction(this.model.name, siblingNames).subscribe(
            (updateEvent: JsonTreeContainerEditorEvent) => {

                this.resourceService.renameDirectory(
                    new RenamePath(this.model.path, updateEvent.newName)
                ).subscribe(
                    (responsePath: Path) => {
                        this.model.name = updateEvent.newName;
                        this.resourcesTreeService.sort()
                    }
                )
            }
        )
    }

    private getSiblingNames() {
        let siblingNames: Array<string> = [];
        let siblingContainers = this.model.parentContainer.getChildren();
        for (const child of siblingContainers) {
            if (child.isContainer()) {
                siblingNames.push((child as JsonTreePathContainer).name)
            }
        }
        return siblingNames;
    }

    deleteDirectory(): void {
        this.jsonTreeService.triggerDeleteContainerAction(this.model.name).subscribe(
            (deleteEvent: JsonTreeContainerEditorEvent) => {

                this.resourceService.deleteDirectory(
                    this.model.path
                ).subscribe(
                    it => {
                        this.resourceService.showResourcesScreen();
                        this.resourcesTreeService.initializeResourceTreeFromServer(null);
                    }
                )
            }
        )
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    copyResource(event: any) {
        let resourceToCopyTreeNode: ResourcesTreeNode = event.dragData;
        let pathToCopy = resourceToCopyTreeNode.path;
        let destinationPath = this.model.path;
        this.jsonTreeService.triggerCopyAction(pathToCopy, destinationPath).subscribe(
            (copyEvent: JsonTreeContainerEditorEvent) => {

                let copyPath = new CopyPath(pathToCopy, destinationPath);
                this.resourceService.moveDirectoryOrFile (
                    copyPath
                ).subscribe(
                    it => {
                        this.resourcesTreeService.copy(pathToCopy, destinationPath);
                    }
                )
            }
        )
    }
}
