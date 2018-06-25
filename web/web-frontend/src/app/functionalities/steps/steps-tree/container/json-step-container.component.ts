import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../../../generic/components/json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {RenamePath} from "../../../../model/infrastructure/path/rename-path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {StepTreeContainerModel} from "../model/step-tree-container.model";
import {StepsTreeService} from "../steps-tree.service";
import {StepTreeNodeModel} from "../model/step-tree-node.model";
import {BasicStepDef} from "../../../../model/basic-step-def.model";
import {StepsService} from "../../../../service/steps.service";
import {CopyPath} from "../../../../model/infrastructure/path/copy-path.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";

@Component({
    moduleId: module.id,
    selector: 'json-step-container',
    templateUrl: 'json-step-container.component.html',
    styleUrls: [
        'json-step-container.component.css',
        '../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../generic/css/tree.css'
    ]
})
export class JsonStepContainerComponent {

    @Input() model: StepTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    hasMouseOver: boolean = false;

    constructor(private router: Router,
                private jsonTreeService: JsonTreeService,
                private stepsTreeService: StepsTreeService,
                private stepsService: StepsService) {
    }

    allowDrop():any {
        return (dragData: StepTreeNodeModel) => {
            let isNotChildOfContainer = !JsonTreePathUtil.isChildOf(dragData, this.model) &&
                !JsonTreePathUtil.isDirectChildOf(this.model, dragData) &&
                this.model !== dragData;
            let dragDataIsBasicStep = dragData.stepDef instanceof BasicStepDef;
            let isComposedStepContainer = this.model.isComposedStepContainer;
            return isNotChildOfContainer && !dragDataIsBasicStep && isComposedStepContainer
        }
    }

    showCreateDirectory() {
        this.router.navigate(["/automated/steps/composed/create", { path : this.model.path.toString()}]);
    }

    showCreateDirectoryModal(): void {
        let childrenContainersName = this.getChildrenContanersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {
                let pathDirectories: Array<string> = ArrayUtil.copyArray(this.model.path.directories);
                pathDirectories.push(createEvent.newName);

                let newContainer = new StepTreeContainerModel(
                    this.model,
                    new Path(pathDirectories, null, null),
                    true
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

                this.stepsService.renameDirectory(
                    new RenamePath(this.model.path, updateEvent.newName)
                ).subscribe(
                    (responsePath: Path) => {
                        this.model.name = updateEvent.newName;
                        this.stepsTreeService.sort()
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

                this.stepsService.deleteDirectory(
                    this.model.path
                ).subscribe(
                    it => {
                        this.stepsService.showStepsScreen();
                        this.stepsTreeService.initializeStepsTreeFromServer();
                    }
                )
            }
        )
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    copyResource(event: any) {
        let stepToCopyTreeNode: StepTreeNodeModel = event.dragData;
        let pathToCopy = stepToCopyTreeNode.path;
        let destinationPath = this.model.path;
        this.jsonTreeService.triggerCopyAction(pathToCopy, destinationPath).subscribe(
            (copyEvent: JsonTreeContainerEditorEvent) => {

                let copyPath = new CopyPath(pathToCopy, destinationPath);
                this.stepsService.moveDirectoryOrFile (
                    copyPath
                ).subscribe(
                    it => {
                        this.stepsTreeService.copy(pathToCopy, destinationPath);
                    }
                )
            }
        )
    }
}
