import {Component, OnInit, Input} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {StepTreeNodeModel} from "../../model/step-tree-node.model";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";

@Component({
    moduleId: module.id,
    selector: 'json-step-node',
    templateUrl: 'json-step-node.component.html',
    styleUrls:['json-step-node.component.css']
})
export class JsonStepNodeComponent implements OnInit {

    @Input() model:StepTreeNodeModel;
    private isSelected:boolean = false;

    constructor(private router: Router,
                private treeService:JsonTreeService) {
        treeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
    }

    ngOnInit(): void {

        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                let selectedPath = params['id'];

                if(selectedPath == this.model.path.toString()){
                    this.treeService.setSelectedNode(this.model);
                }
            });

        this.treeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as StepTreeNodeModel) == this.model;
            }
        )
    }

    onStepSelected(item:JsonTreeNodeEventModel) {
        if(item.treeNode == this.model) {
            this.showViewer();
        }
    }
    private showViewer() {
        if (this.model.stepDef instanceof ComposedStepDef) {
            this.router.navigate(['/automated/steps/composed', {path : this.model.path.toString()}]);
        } else {
            this.router.navigate(["/automated/steps/basic", {path : this.model.path.toString()}]);
        }
    }

    showComposedStep() {
        if (this.model instanceof ComposedStepDef) {
            this.router.navigate(['/automated/steps/composed', {path : this.model.path.toString()} ]);
        } else {
            this.router.navigate(["/automated/steps"]);
        }
    }
}
