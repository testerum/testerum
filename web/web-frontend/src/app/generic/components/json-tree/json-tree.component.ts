import {
    Component, Input, OnInit, ViewChild
} from '@angular/core';

import {JsonTreeModel} from "./model/json-tree.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeContainerEditor} from "./container-editor/json-tree-container-editor.component";
import {JsonTreeService} from "./json-tree.service";

@Component({
    moduleId: module.id,
    selector: 'json-tree',
    templateUrl: 'json-tree.component.html',
    styleUrls:['json-tree.component.css']
})
export class JsonTreeComponent implements OnInit {

    @Input() treeModel:JsonTreeModel ;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() allowContainerSelection: boolean = false;

    constructor(private jsonTreeService: JsonTreeService) {
    }

    ngOnInit(): void {
    }
}
