import {Component, Input} from '@angular/core';
import {StepTreeContainerModel} from "../../../../functionalities/steps/steps-tree/model/step-tree-container.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'step-chooser-container',
    templateUrl: 'step-chooser-container.component.html',
    styleUrls: [
        'step-chooser-container.component.scss',
        '../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../generic/css/tree.scss'
    ]
})
export class StepChooserContainerComponent {

    @Input() model: StepTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
