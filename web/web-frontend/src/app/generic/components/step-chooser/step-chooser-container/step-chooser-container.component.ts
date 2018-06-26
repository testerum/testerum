import {Component, Input} from '@angular/core';
import {StepTreeContainerModel} from "../../../../functionalities/steps/steps-tree/model/step-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'step-chooser-container',
    templateUrl: 'step-chooser-container.component.html',
    styleUrls: [
        'step-chooser-container.component.css',
        '../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../generic/css/tree.css'
    ]
})
export class StepChooserContainerComponent {

    @Input() model: StepTreeContainerModel;

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
