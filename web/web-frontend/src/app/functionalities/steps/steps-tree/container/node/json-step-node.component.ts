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
export class JsonStepNodeComponent implements OnInit, OnDestroy {

    @Input() model:StepTreeNodeModel;
    isSelected:boolean = false;

    selectedNodeSubscription: Subscription;

    constructor(private router: Router,
                private urlService: UrlService,
                private jsonTreeService:JsonTreeService) {
        this.selectedNodeSubscription = jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onNodeSelected(item));
    }

    ngOnInit(): void {
        this.isSelected = this.jsonTreeService.isSelectedNodeEqualsWithTreeNode(this.model);
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
    }

    onNodeSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) {
        this.isSelected = selectedJsonTreeNodeEventModel.treeNode == this.model;

        if(this.isSelected) {
            this.showViewer();
        }
    }

    showViewer() {
        if (this.model.isComposedStep) {
            this.urlService.navigateToComposedStep(this.model.path)
        } else {
            this.urlService.navigateToBasicStep(this.model.path)
        }
    }
}
