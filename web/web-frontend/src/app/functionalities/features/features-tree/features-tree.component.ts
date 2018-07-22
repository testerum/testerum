import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {TestTreeNodeModel} from "./model/test-tree-node.model";
import {FeatureContainerComponent} from "./container/feature-container.component";
import {FeaturesTreeService} from "./features-tree.service";
import {FeatureTreeContainerModel} from "./model/feature-tree-container.model";
import {TestNodeComponent} from "./container/node/test-node.component";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";

@Component({
    selector: 'features-tree',
    template: `
        <json-tree [treeModel]="featuresTreeService.treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})

export class FeaturesTreeComponent implements OnInit {

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(FeatureTreeContainerModel, FeatureContainerComponent)
        .addPair(TestTreeNodeModel, TestNodeComponent);

    featuresTreeService: FeaturesTreeService;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private treeService:JsonTreeService,
                featuresTreeService: FeaturesTreeService) {
        this.featuresTreeService = featuresTreeService;
    }

    ngOnInit(): void {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path']: null;
        let path: Path = pathAsString !=null ? Path.createInstance(pathAsString) : null;

        this.featuresTreeService.initializeTestsTreeFromServer(path);

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

        if(!this.featuresTreeService.treeModel) { return; }

        let allTreeNodes: Array<TestTreeNodeModel> = this.featuresTreeService.treeModel.getAllTreeNodes<TestTreeNodeModel>();

        for (const treeNode of allTreeNodes) {
            if (treeNode.path && treeNode.path.equals(selectedPath)) {
                this.treeService.setSelectedNode(treeNode);
                JsonTreeExpandUtil.expandTreeToNode(treeNode);
                break;
            }
        }
    }
}
