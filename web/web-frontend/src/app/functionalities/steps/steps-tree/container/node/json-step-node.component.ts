import {Component, OnInit, Input} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {StepTreeNodeModel} from "../../model/step-tree-node.model";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";
import {UrlService} from "../../../../../service/url.service";

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
                private urlService: UrlService,
                private treeService:JsonTreeService) {
        treeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
    }

    ngOnInit(): void {
        this.treeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as StepTreeNodeModel) == this.model;
            }
        );
        if(this.treeService.selectedNode != null && this.treeService.selectedNode == this.model) {
            this.isSelected = true;
        }
    }

    onStepSelected(item:JsonTreeNodeEventModel) {
        if(item.treeNode == this.model) {
            this.showViewer();
        }
    }
    private showViewer() {
        if (this.model.stepDef instanceof ComposedStepDef) {
            this.urlService.navigateToComposedStep(this.model.path)
        } else {
            this.urlService.navigateToBasicStep(this.model.path)
        }
    }

    showComposedStep() {
        if (this.model instanceof ComposedStepDef) {
            this.urlService.navigateToComposedStep(this.model.path);
        } else {
            this.urlService.navigateToSteps();
        }
    }
}
