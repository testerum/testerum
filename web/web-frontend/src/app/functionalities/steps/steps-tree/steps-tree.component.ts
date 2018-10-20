import {Component, Input} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {UrlUtil} from "../../../utils/url.util";
import {StepsTreeService} from "./steps-tree.service";

@Component({
    selector: 'steps-tree',
    template: `
        <json-tree [treeModel]="treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})
export class StepsTreeComponent {

    @Input() treeModel: JsonTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private stepTreeService: StepsTreeService) {

        router.events.forEach((event) => {
            if (event instanceof NavigationEnd) {
                stepTreeService.selectNodeAtPath(UrlUtil.getPathParamFromUrl(this.activatedRoute))
            }
        });
    }
}
