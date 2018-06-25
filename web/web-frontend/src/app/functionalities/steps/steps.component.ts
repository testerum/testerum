import {Component, OnInit, Input} from '@angular/core';
import {StepsService} from "../../service/steps.service";
import {StepsTreeService} from "./steps-tree/steps-tree.service";
import {ActivatedRouteSnapshot, NavigationEnd, Params, Router} from "@angular/router";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {StepTreeContainerModel} from "./steps-tree/model/step-tree-container.model";
import {StepTreeNodeModel} from "./steps-tree/model/step-tree-node.model";
import {JsonStepNodeComponent} from "./steps-tree/container/node/json-step-node.component";
import {JsonStepContainerComponent} from "./steps-tree/container/json-step-container.component";

@Component({
    moduleId: module.id,
    selector: 'steps',
    templateUrl: 'steps.component.html',
    styleUrls: ["../../generic/css/main-container.css"]

})
export class StepsComponent implements OnInit {

    @Input() editable:boolean = true;

    private isStepSelected: boolean = true;

     jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepTreeContainerModel, JsonStepContainerComponent)
        .addPair(StepTreeNodeModel, JsonStepNodeComponent);

    stepsService:StepsService;
    stepsTreeService:StepsTreeService;

    constructor(stepsService:StepsService,
                stepsTreeService:StepsTreeService,
                private router: Router) {
        this.stepsService = stepsService;
        this.stepsTreeService = stepsTreeService;
    }

    ngOnInit() {
        this.stepsService.getDefaultSteps().subscribe(
            steps => this.stepsTreeService.setBasicStepsModel(steps)
        );

        this.stepsService.getComposedStepDefs().subscribe(
            stepsPackage => this.stepsTreeService.setComposedStepsModel(stepsPackage)
        );


        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute
            })
            .subscribe((activatedRoute: ActivatedRouteSnapshot) => {
                if(activatedRoute.params['path']) {
                    this.isStepSelected = true;
                    return
                }

                if(activatedRoute.params["action"]) {
                    this.isStepSelected = true;
                    return
                }

                this.isStepSelected = false;
            });
    }
}
