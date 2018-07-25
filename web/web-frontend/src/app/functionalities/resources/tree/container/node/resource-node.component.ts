import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ResourcesTreeNode} from "../../model/resources-tree-node.model";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {Subscription} from "rxjs/Subscription";

@Component({
    moduleId: module.id,
    selector: 'resource-node',
    templateUrl: 'resource-node.component.html',
    styleUrls:['resource-node.component.scss']
})
export class ResourceNodeComponent implements OnInit, OnDestroy {

    @Input() model:ResourcesTreeNode;
    private isSelected:boolean = false;

    selectedNodeSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private jsonTreeService:JsonTreeService) {
        this.selectedNodeSubscription = jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onNodeSelected(item));
    }

    ngOnInit(): void {
        this.isSelected = this.jsonTreeService.isSelectedNodeEqualsWithTreeNode(this.model);
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
    }

    getName() {
       return this.model.name.split(".")[0]
    }

    onNodeSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) {
        this.isSelected = selectedJsonTreeNodeEventModel.treeNode == this.model;

        if(this.isSelected) {
            this.showViewer();
        }
    }

    private showViewer() {
        //TODO: Check why is this called twice???!!!
        this.router.navigate([
           "/automated/resources/show",
           {"path":this.model.path}]);
    }
}
