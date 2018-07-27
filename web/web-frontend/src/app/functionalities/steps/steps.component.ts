import {Component, Input, OnInit} from '@angular/core';
import {StepsTreeService} from "./steps-tree/steps-tree.service";
import {ActivatedRoute} from "@angular/router";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {StepTreeContainerModel} from "./steps-tree/model/step-tree-container.model";
import {StepTreeNodeModel} from "./steps-tree/model/step-tree-node.model";
import {JsonStepNodeComponent} from "./steps-tree/container/node/json-step-node.component";
import {JsonStepContainerComponent} from "./steps-tree/container/json-step-container.component";
import {Path} from "../../model/infrastructure/path/path.model";

@Component({
    moduleId: module.id,
    selector: 'steps',
    templateUrl: 'steps.component.html',
    styleUrls: ["../../generic/css/main-container.scss"]

})
export class StepsComponent implements OnInit {

    @Input() editable:boolean = true;

     jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepTreeContainerModel, JsonStepContainerComponent)
        .addPair(StepTreeNodeModel, JsonStepNodeComponent);

    stepsTreeService:StepsTreeService;

    constructor(stepsTreeService:StepsTreeService,
                private activatedRoute: ActivatedRoute,) {
        this.stepsTreeService = stepsTreeService;
    }

    ngOnInit() {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path'] : null;
        let path: Path = pathAsString != null ? Path.createInstance(pathAsString) : null;

        this.stepsTreeService.initializeStepsTreeFromServer(path);
    }
}
