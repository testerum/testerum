import {Component, Input, OnInit} from '@angular/core';
import {JsonTreeService} from "../../json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../json-tree/model/json-tree-node-state.model";
import {PathChooserContainerModel} from "../model/path-chooser-container.model";
import {MapPathChooserUtil} from "../util/map-path-chooser.util";
import {PathChooserNodeModel} from "../model/path-chooser-node.model";
import {PathChooserService} from "../path-chooser.service";
import {JsonTreeNodeEventModel} from "../../json-tree/event/selected-json-tree-node-event.model";

@Component({
    moduleId: module.id,
    selector: 'path-chooser-container',
    templateUrl: 'path-chooser-container.component.html',
    styleUrls: [
        'path-chooser-container.component.css',
        '../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../generic/css/tree.css'
    ]
})
export class PathChooserContainerComponent implements OnInit {

    @Input() model: PathChooserContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    isSelected: boolean;

    constructor(private jsonTreeService: JsonTreeService,
                private pathChooserService: PathChooserService) {
    }

    ngOnInit(): void {
        this.jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
    }

    onStepSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) : void {
        if(selectedJsonTreeNodeEventModel.treeNode == this.model) {
            this.isSelected = true;
        } else {
            this.isSelected = false;
        }
    }

    showCreateDirectoryModal(): void {
        let childrenContainersName = this.getChildrenContainersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {
                let pathDirectories: Array<string> = ArrayUtil.copyArrayOfImmutableObjects(this.model.path.directories);
                pathDirectories.push(createEvent.newName);

                let newContainer = new PathChooserContainerModel(
                    this.model,
                    new Path(pathDirectories, null, null),
                    this.model.allowDirSelection
                );
                newContainer.isNew = true;
                this.model.addChild(newContainer);
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
        let siblingNames: Array<string> = this.getSiblingNames();

        this.jsonTreeService.triggerUpdateContainerAction(this.model.name, siblingNames).subscribe(
            (updateEvent: JsonTreeContainerEditorEvent) => {
                this.model.name = updateEvent.newName;
                MapPathChooserUtil.sortChildren(this.model.parentContainer);
            }
        )
    }

    private getSiblingNames() {
        let siblingNames: Array<string> = [];
        let siblingContainers = this.model.parentContainer.getChildren();
        for (const child of siblingContainers) {
            if (child.isContainer()) {
                siblingNames.push((child as PathChooserNodeModel).name)
            }
        }
        return siblingNames;
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    selectNode() {
        this.pathChooserService.selectPathSubject.next(this.model.path)
    }
}
