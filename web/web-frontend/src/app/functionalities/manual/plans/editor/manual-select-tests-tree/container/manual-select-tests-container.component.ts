import {Component, Input} from '@angular/core';
import {SelectTestsTreeContainerModel} from "../model/select-tests-tree-container.model";
import {SelectionStateEnum} from "../model/enum/selection-state.enum";
import {ManualSelectTestsTreeComponentService} from "../manual-select-tests-tree.component-service";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'manual-select-tests-container',
    templateUrl: 'manual-select-tests-container.component.html',
    styleUrls: [
        'manual-select-tests-container.component.scss'
    ]
})
export class ManualSelectTestsContainerComponent {

    @Input() model: SelectTestsTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    constructor(private manualSelectTestsTreeComponentService: ManualSelectTestsTreeComponentService) {
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.manualSelectTestsTreeComponentService.isEditMode;
    }

    selectOrNot() {
        if(!this.isEditMode()) {
            return;
        }

        this.model.selectedState = this.model.selectedState != SelectionStateEnum.SELECTED ? SelectionStateEnum.SELECTED : SelectionStateEnum.NOT_SELECTED ;
        this.selectOrNotChildren(this.model);
    }

    private selectOrNotChildren(container: SelectTestsTreeContainerModel) {
        container.selectedState = this.model.selectedState;
        for (let child of container.children) {
            if (child.isContainer()) {
                this.selectOrNotChildren(child as SelectTestsTreeContainerModel)
            } else {
                child.isSelected = this.model.selectedState == SelectionStateEnum.SELECTED;
            }
        }

        if (this.model.parentContainer instanceof SelectTestsTreeContainerModel) {
            this.model.parentContainer.calculateCheckState()
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }
}
