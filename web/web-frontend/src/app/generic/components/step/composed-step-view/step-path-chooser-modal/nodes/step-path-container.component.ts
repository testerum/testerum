import {Component, Input, OnInit} from "@angular/core";
import {StepPathContainerModel} from "../model/step-path-container.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeService} from "../../../../json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../../../json-tree/container-editor/model/json-tree-container-editor.event";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {StepPathModalComponentService} from "../step-path-modal.component-service";

@Component({
    moduleId: module.id,
    selector: 'path-chooser-container',
    templateUrl: 'step-path-container.component.html',
    styleUrls: [
        'step-path-container.component.scss',
        '../../../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../../../generic/css/tree.scss'
    ]
})
export class StepPathContainerComponent implements OnInit {

    @Input() model: StepPathContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    hasMouseOver: boolean = false;

    isSelected: boolean;

    constructor(private jsonTreeService: JsonTreeService,
                private stepPathModalComponentService: StepPathModalComponentService) {
    }

    ngOnInit(): void {
        this.stepPathModalComponentService.selectedNodeEmitter.subscribe((item:StepPathContainerModel) => this.onStepSelected(item));
    }

    private onStepSelected(selectedModel:StepPathContainerModel) : void {
        this.isSelected = selectedModel == this.model;
    }

    showCreateDirectoryModal(): void {
        let childrenContainersName = this.getChildrenContainersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {
                let pathDirectories: Array<string> = ArrayUtil.copyArrayOfImmutableObjects(this.model.path.directories);
                pathDirectories.push(createEvent.newName);

                let newContainer = new StepPathContainerModel(
                    this.model,
                    new Path(pathDirectories, null, null),
                );
                this.model.children.push(newContainer);
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

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    selectNode() {
        this.stepPathModalComponentService.selectedNodeEmitter.next(this.model)
    }
}
