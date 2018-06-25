import {Component, OnInit, Input, ViewChild, ElementRef} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {ResourceService} from "../../../../service/resources/resource.service";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {StepTreeContainerModel} from "../../../../functionalities/steps/steps-tree/model/step-tree-container.model";
import {StepsTreeService} from "../../../../functionalities/steps/steps-tree/steps-tree.service";
import {StepTreeNodeModel} from "../../../../functionalities/steps/steps-tree/model/step-tree-node.model";

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

    constructor(private router: Router) {
    }


    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }
}
