import {Component, OnInit, Input} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {TestTreeNodeModel} from "../../model/test-tree-node.model";
import {UrlService} from "../../../../../service/url.service";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'test-node.component.html',
    styleUrls:['test-node.component.css']
})
export class TestNodeComponent implements OnInit {

    @Input() model:TestTreeNodeModel;
    private isSelected:boolean = false;

    constructor(private router: Router,
                private urlService: UrlService,
                private jsonTreeService:JsonTreeService) {
        jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onTestSelected(item));
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
                    this.jsonTreeService.setSelectedNode(this.model);
                }
            });

        this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as TestTreeNodeModel) == this.model;
            }
        )
    }

    onTestSelected(item:JsonTreeNodeEventModel) {
        if(item.treeNode == this.model) {
            this.showTest();
        }
    }

    showTest() {
        this.urlService.navigateToTest(this.model.path);
    }
}
