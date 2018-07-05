import {Component, OnInit, Input} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {ResourcesTreeNode} from "../../model/resources-tree-node.model";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";

@Component({
    moduleId: module.id,
    selector: 'resource-node',
    templateUrl: 'resource-node.component.html',
    styleUrls:['resource-node.component.css']
})
export class ResourceNodeComponent implements OnInit {

    @Input() model:ResourcesTreeNode;
    private isSelected:boolean = false;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private treeService:JsonTreeService) {
    }

    ngOnInit(): void {
        this.activatedRoute.children.forEach(
            (childActivateRoute: ActivatedRoute) => {
                childActivateRoute.params.subscribe( (params: Params) => {
                    let selectedPath = params['path'];

                    if(selectedPath == this.model.path.toString()){
                        this.treeService.setSelectedNode(this.model);
                    }
                })
            }
        );

        this.treeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.onStepSelected(selectedNodeEvent.treeNode);
            }
        );
        if(this.treeService.selectedNode != null && this.treeService.selectedNode == this.model) {
            this.isSelected = true;
        }
    }

    getName() {
       return this.model.name.split(".")[0]
    }

    onStepSelected(selectedNode:ResourcesTreeNode) {
        this.isSelected = selectedNode == this.model;
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
