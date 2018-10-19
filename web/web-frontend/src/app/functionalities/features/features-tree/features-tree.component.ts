import {Component, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {TestTreeNodeModel} from "./model/test-tree-node.model";
import {FeatureContainerComponent} from "./container/feature-container.component";
import {FeaturesTreeService} from "./features-tree.service";
import {FeatureTreeContainerModel} from "./model/feature-tree-container.model";
import {TestNodeComponent} from "./container/node/test-node.component";
import {FeaturesTreeFilter} from "../../../model/feature/filter/features-tree-filter.model";
import {UrlUtil} from "../../../utils/url.util";

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

        router.events.forEach((event) => {
            if(event instanceof NavigationEnd) {
                featuresTreeService.selectNodeAtPath(UrlUtil.getPathParamFromUrl(this.activatedRoute))
            }
        });
    }

    ngOnInit(): void {
        let path = UrlUtil.getPathParamFromUrl(this.activatedRoute);

        this.featuresTreeService.treeFilter = FeaturesTreeFilter.createEmptyFilter();
        this.featuresTreeService.initializeTestsTreeFromServer(path);
    }
}
