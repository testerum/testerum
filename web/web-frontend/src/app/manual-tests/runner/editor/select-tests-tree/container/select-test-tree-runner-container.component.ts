import {Component, Input} from '@angular/core';
import {SelectTestTreeRunnerContainerModel} from "../model/select-test-tree-runner-container.model";
import {ObjectUtil} from "../../../../../utils/object.util";
import {SelectionStateEnum} from "../model/enum/selection-state.enum";
import {SelectTestsTreeRunnerService} from "../select-tests-tree-runner.service";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'feature-container',
    templateUrl: 'select-test-tree-runner-container.component.html',
    styleUrls: [
        'select-test-tree-runner-container.component.scss',
        '../../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../../generic/css/tree.scss'
    ]
})
export class SelectTestTreeRunnerContainerComponent {

    @Input() model: SelectTestTreeRunnerContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    constructor(private selectTestsTreeRunnerService: SelectTestsTreeRunnerService) {
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.selectTestsTreeRunnerService.isEditMode;
    }

    selectOrNot() {
        if(!this.isEditMode()) {
            return;
        }

        this.model.selectedState = this.model.selectedState != SelectionStateEnum.SELECTED? SelectionStateEnum.SELECTED: SelectionStateEnum.NOT_SELECTED ;
        this.selectOrNotChildren(this.model);
    }

    private selectOrNotChildren(container: SelectTestTreeRunnerContainerModel) {
        container.selectedState = this.model.selectedState;
        for (let child of container.children) {
            if (ObjectUtil.hasAMethodCalled(child, "hasChildren")) {
                this.selectOrNotChildren(child as SelectTestTreeRunnerContainerModel)
            } else {
                child.isSelected = this.model.selectedState == SelectionStateEnum.SELECTED;
            }
        }

        let parentNodes: Array<SelectTestTreeRunnerContainerModel> = this.selectTestsTreeRunnerService.getParentNodesOf(
            this.model
        ) as Array<SelectTestTreeRunnerContainerModel>;
        for (const parentNode of parentNodes) {
            parentNode.calculateCheckState()
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }
}
