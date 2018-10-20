import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {StepTreeNodeModel} from "../../model/step-tree-node.model";
import {UrlService} from "../../../../../service/url.service";
import {Subscription} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'json-step-node',
    templateUrl: 'json-step-node.component.html',
    styleUrls:['json-step-node.component.scss']
})
export class JsonStepNodeComponent {

    @Input() model:StepTreeNodeModel;

    constructor(private router: Router,
                private urlService: UrlService) {
    }

    setSelected() {
        if (this.model.isComposedStep) {
            this.urlService.navigateToComposedStep(this.model.path)
        } else {
            this.urlService.navigateToBasicStep(this.model.path)
        }
    }
}
