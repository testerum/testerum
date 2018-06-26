import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {FileDirectoryChooserContainerModel} from "../model/file-directory-chooser-container.model";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";

@Component({
    moduleId: module.id,
    selector: 'file-directory-chooser-container',
    templateUrl: 'file-directory-chooser-container.component.html',
    styleUrls: [
        'file-directory-chooser-container.component.css',
        '../../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../../generic/css/tree.css'
    ]
})
export class FileDirectoryChooserContainerComponent implements OnInit, OnDestroy {

    @Input() model: FileDirectoryChooserContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    isSelected:boolean = false;
    hasMouseOver: boolean = false;

    private selectedNodeSubscription: any;

    constructor(private router: Router,
                private jsonTreeService: JsonTreeService) {
    }

    ngOnInit(): void {
        this.selectedNodeSubscription = this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as FileDirectoryChooserContainerModel) == this.model;
            }
        )
    }

    ngOnDestroy(): void {
        this.selectedNodeSubscription.unsubscribe()
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
        if (this.model.jsonTreeNodeState.showChildren) {
            this.jsonTreeService.triggerExpendedNodeEvent(this.model)
        }
    }

    setSelected() {
        this.jsonTreeService.setSelectedNode(this.model);
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
