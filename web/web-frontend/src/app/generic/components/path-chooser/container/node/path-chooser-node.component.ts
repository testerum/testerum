import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {PathChooserNodeModel} from "../../model/path-chooser-node.model";
import {PathChooserService} from "../../path-chooser.service";
import {Subscription} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'path-chooser-node',
    templateUrl: 'path-chooser-node.component.html',
    styleUrls:['path-chooser-node.component.scss']
})
export class PathChooserNodeComponent implements OnInit, OnDestroy {

    @Input() model:PathChooserNodeModel;
    isSelected:boolean = false;

    selectedNodeEventSubscription: Subscription;

    constructor(private treeService:JsonTreeService,
                private pathChooserService: PathChooserService) {
    }

    ngOnInit(): void {
        this.selectedNodeEventSubscription = this.treeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
    }

    ngOnDestroy(): void {
        this.selectedNodeEventSubscription.unsubscribe();
    }

    onStepSelected(item:JsonTreeNodeEventModel) {
        if(item.treeNode == this.model) {
            this.isSelected = true;
            this.pathChooserService.selectPathSubject.next(this.model.path)
        } else {
            this.isSelected = false;
        }
    }
}
