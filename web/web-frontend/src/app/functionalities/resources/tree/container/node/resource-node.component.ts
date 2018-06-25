import {Component, OnInit, Input} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
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
                private treeService:JsonTreeService) {
        treeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
    }

    ngOnInit(): void {
        this.router.events
            .filter(
                event => event instanceof NavigationEnd
            )
            .map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                let selectedPath = params['path'];

                if(selectedPath == this.model.path.toString()){
                    this.treeService.setSelectedNode(this.model);
                }
            });


        this.treeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as ResourcesTreeNode) == this.model;
            }
        )
    }

    getName() {
       return this.model.name.split(".")[0]
    }

    onStepSelected(item:JsonTreeNodeEventModel) {
        if(item.treeNode == this.model) {
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
