import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {TestTreeNodeModel} from "../../model/test-tree-node.model";
import {UrlService} from "../../../../../service/url.service";
import {Subscription} from "rxjs/Subscription";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'test-node.component.html',
    styleUrls:['test-node.component.scss']
})
export class TestNodeComponent implements OnInit, OnDestroy {

    @Input() model:TestTreeNodeModel;
    private isSelected:boolean = false;

    selectedNodeSubscription: Subscription;

    constructor(private router: Router,
                private urlService: UrlService,
                private jsonTreeService:JsonTreeService) {
        this.selectedNodeSubscription = jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onTestSelected(item));
    }

    ngOnInit(): void {
        this.isSelected = this.jsonTreeService.isSelectedNodeEqualsWithTreeNode(this.model);
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
    }

    onTestSelected(item:JsonTreeNodeEventModel) {
        this.isSelected = item.treeNode == this.model;
        if(item.treeNode == this.model) {
            this.showTest();
        }
    }

    showTest() {
        this.urlService.navigateToTest(this.model.path);
    }
}
