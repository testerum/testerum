import {filter, map} from 'rxjs/operators';
import {Component, Input, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {ManualTestTreeNodeModel} from "../../model/manual-test-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'manual-test-tree-node.component.html',
    styleUrls:['manual-test-tree-node.component.scss']
})
export class ManualTestTreeNodeComponent implements OnInit {

    @Input() model:ManualTestTreeNodeModel;
    private isSelected:boolean = false;

    constructor(private router: Router,
                private jsonTreeService:JsonTreeService) {
        jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onTestSelected(item));
    }

    ngOnInit(): void {

        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            }),)
            .subscribe((params: Params) => {
                let selectedPath = params['id'];

                if(selectedPath == this.model.path.toString()){
                    this.jsonTreeService.setSelectedNode(this.model);
                }
            });

        this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as ManualTestTreeNodeModel) == this.model;
            }
        )
    }

    onTestSelected(item:JsonTreeNodeEventModel) {
        if(item.treeNode == this.model) {
            this.showTest();
        }
    }

    showTest() {
        this.router.navigate(["/manual/tests/show", {path : this.model.path.toString()} ]);
    }
}
