import {Component, Input} from '@angular/core';

import {JsonTreeModel} from "./model/json-tree.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {TreeState} from "./model/state/TreeState";

@Component({
    moduleId: module.id,
    selector: 'json-tree',
    templateUrl: 'json-tree.component.html',
    styleUrls:['json-tree.component.scss']
})
export class JsonTreeComponent {

    @Input() treeModel:JsonTreeModel ;
    @Input() treeState:TreeState;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() allowContainerSelection: boolean = false;
}
