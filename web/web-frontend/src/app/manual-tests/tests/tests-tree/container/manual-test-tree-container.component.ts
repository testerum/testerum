import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../../../generic/components/json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {RenamePath} from "../../../../model/infrastructure/path/rename-path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {CopyPath} from "../../../../model/infrastructure/path/copy-path.model";
import {ManualTestTreeContainerModel} from "../model/manual-test-tree-container.model";
import {ManualTestsTreeService} from "../manual-tests-tree.service";
import {ManualTestTreeNodeModel} from "../model/manual-test-tree-node.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ManualTestsService} from "../../service/manual-tests.service";

@Component({
    moduleId: module.id,
    selector: 'feature-container',
    templateUrl: 'manual-test-tree-container.component.html',
    styleUrls: [
        'manual-test-tree-container.component.css',
        '../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../generic/css/tree.css'
    ]
})
export class ManualTestTreeContainerComponent {

    @Input() model: ManualTestTreeContainerModel;
    hasMouseOver: boolean = false;

    constructor(private router: Router,
                private jsonTreeService: JsonTreeService,
                private testsTreeService: ManualTestsTreeService,
                private manualTestsService: ManualTestsService) {
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    allowDrop():any {
        return (dragData: ManualTestTreeNodeModel) => {
            let isNotChildOfContainer = !JsonTreePathUtil.isChildOf(dragData, this.model) &&
                !JsonTreePathUtil.isDirectChildOf(this.model, dragData) &&
                this.model !== dragData;
            return isNotChildOfContainer;
        }
    }

    showCreateTest() {
        this.router.navigate(["/manual/tests/create", { path : this.model.path.toString()}]);
    }

    showCreateDirectoryModal(): void {
        let childrenContainersName = this.getChildrenContanersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {
                let pathDirectories: Array<string> = ArrayUtil.copyArray(this.model.path.directories);
                pathDirectories.push(createEvent.newName);

                let newContainer = new ManualTestTreeContainerModel(
                    this.model,
                    createEvent.newName,
                    new Path(pathDirectories, null, null)
                );
                newContainer.editable = true;
                this.model.children.push(newContainer);
                this.model.sort();
            }
        )
    }

    private getChildrenContanersName() {
        let childrenContainersName: Array<string> = [];
        for (const child of this.model.children) {
            if (child.isContainer()) {
                childrenContainersName.push(child.name)
            }
        }
        return childrenContainersName;
    }

    showEditDirectoryNameModal(): void {
        let siblingNames: Array<string> = this.getSiblingNames();

        this.jsonTreeService.triggerUpdateContainerAction(this.model.name, siblingNames).subscribe(
            (updateEvent: JsonTreeContainerEditorEvent) => {

                this.manualTestsService.renameDirectory(
                    new RenamePath(this.model.path, updateEvent.newName)
                ).subscribe(
                    (responsePath: Path) => {
                        this.model.name = updateEvent.newName;
                        this.model.path = responsePath;
                        this.testsTreeService.sort()
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
                siblingNames.push(child.name)
            }
        }
        return siblingNames;
    }

    deleteDirectory(): void {
        this.jsonTreeService.triggerDeleteContainerAction(this.model.name).subscribe(
            (deleteEvent: JsonTreeContainerEditorEvent) => {

                this.manualTestsService.deleteDirectory(
                    this.model.path
                ).subscribe(
                    it => {
                        this.manualTestsService.showTestsScreen();
                        this.testsTreeService.initializeTestsTreeFromServer();
                    }
                )
            }
        )
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    copyResource(event: any) {
        let stepToCopyTreeNode: ManualTestTreeNodeModel = event.dragData;
        let pathToCopy = stepToCopyTreeNode.path;
        let destinationPath = this.model.path;
        this.jsonTreeService.triggerCopyAction(pathToCopy, destinationPath).subscribe(
            (copyEvent: JsonTreeContainerEditorEvent) => {

                let copyPath = new CopyPath(pathToCopy, destinationPath);
                this.manualTestsService.moveDirectoryOrFile (
                    copyPath
                ).subscribe(
                    it => {
                        this.testsTreeService.copy(pathToCopy, destinationPath);
                    }
                )
            }
        )
    }
}
