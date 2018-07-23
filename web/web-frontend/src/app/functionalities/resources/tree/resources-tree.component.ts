import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ResourcesTreeNode} from "./model/resources-tree-node.model";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";
import {ResourcesTreeService} from "./resources-tree.service";

@Component({
    selector: 'resources-tree',
    template: `        
        <json-tree [treeModel]="treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})

export class ResourcesTreeComponent implements OnInit {

    @Input() treeModel:JsonTreeModel ;
    @Input() modelComponentMapping: ModelComponentMapping;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private resourcesTreeService: ResourcesTreeService,
                private jsonTreeService: JsonTreeService) {
    }

    ngOnInit(): void {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path']: null;
        let path: Path = pathAsString !=null ? Path.createInstance(pathAsString) : null;

        this.resourcesTreeService.initializeResourceTreeFromServer(path);

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

        let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.treeModel, selectedPath);
        this.jsonTreeService.setSelectedNode(selectedNode);
    }
}
