import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ResourcesTreeNode} from "../../model/resources-tree-node.model";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {Subscription} from "rxjs";
import {UrlService} from "../../../../../service/url.service";

@Component({
    moduleId: module.id,
    selector: 'resource-node',
    templateUrl: 'resource-node.component.html',
    styleUrls:['resource-node.component.scss']
})
export class ResourceNodeComponent {

    @Input() model:ResourcesTreeNode;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private jsonTreeService:JsonTreeService,
                private urlService: UrlService) {
    }

    getName() {
       return this.model.name.split(".")[0]
    }

    setSelected() {
        this.urlService.navigateToResource(this.model.path);
    }
}
