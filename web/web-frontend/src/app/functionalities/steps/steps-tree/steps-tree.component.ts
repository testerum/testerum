import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {StepTreeNodeModel} from "./model/step-tree-node.model";

@Component({
    selector: 'steps-tree',
    template: `        
        <json-tree [treeModel]="treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})

export class StepsTreeComponent implements OnChanges {

    @Input() treeModel:JsonTreeModel ;
    @Input() modelComponentMapping: ModelComponentMapping;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private treeService:JsonTreeService) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.activatedRoute.children.forEach(
            (childActivateRoute: ActivatedRoute) => {
                childActivateRoute.params.subscribe( (params: Params) => {
                        this.handleSelectedRouteParams(params)
                    }
                )
            }
        );

        this.router.events
            .filter(
                event => event instanceof NavigationEnd
            )
            .map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                this.handleSelectedRouteParams(params);
            });
    }

    private handleSelectedRouteParams(params: Params) {
        let selectedPathAsString = params['path'];

        if (!selectedPathAsString) { return; }
        let selectedPath = Path.createInstance(selectedPathAsString);

        if(!this.treeModel) { return; }

        let allTreeNodes: Array<StepTreeNodeModel> = this.treeModel.getAllTreeNodes<StepTreeNodeModel>();

        for (const treeNode of allTreeNodes) {
            if (treeNode.path && treeNode.path.equals(selectedPath)) {
                this.treeService.setSelectedNode(treeNode);
                break;
            }
        }
    }
}
