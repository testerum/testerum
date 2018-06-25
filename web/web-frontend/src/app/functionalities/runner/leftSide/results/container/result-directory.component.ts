import {Component, Input, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {FileDirectoryChooserContainerModel} from "../../../../../generic/components/form/file_dir_chooser/model/file-directory-chooser-container.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {ResultsDirectory} from "../model/results-directory.model";

@Component({
    selector: 'result-directory',
    templateUrl: 'result-directory.component.html',
    styleUrls: [
        'result-directory.component.css',
        '../../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../../generic/css/tree.css'
    ]
})

export class ResultDirectoryComponent implements OnInit {

    @Input() model: ResultsDirectory;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    constructor() {
    }

    ngOnInit() {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }
}
