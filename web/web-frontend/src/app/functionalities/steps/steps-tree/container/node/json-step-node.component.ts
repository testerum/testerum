import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {StepTreeNodeModel} from "../../model/step-tree-node.model";
import {UrlService} from "../../../../../service/url.service";
import {StepsTreeService} from "../../steps-tree.service";

@Component({
    moduleId: module.id,
    selector: 'json-step-node',
    templateUrl: 'json-step-node.component.html',
    styleUrls:['json-step-node.component.scss']
})
export class JsonStepNodeComponent {

    @Input() model:StepTreeNodeModel;

    hasMouseOver: boolean = false;

    constructor(private router: Router,
                private urlService: UrlService,
                private stepsTreeService: StepsTreeService) {
    }

    setSelected() {
        if (this.model.isComposedStep) {
            this.urlService.navigateToComposedStep(this.model.path)
        } else {
            this.urlService.navigateToBasicStep(this.model.path)
        }
    }

    onCutStepDef() {
        this.setSelected();
        this.stepsTreeService.setPathToCut(this.model.path, this.model.isComposedStep);
    }

    onCopyStepDef() {
        this.setSelected();
        this.stepsTreeService.setPathToCopy(this.model.path, this.model.isComposedStep);
    }
}
