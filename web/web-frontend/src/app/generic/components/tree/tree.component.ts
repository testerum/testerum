import {
    Component, OnInit, ElementRef, ViewChild, Input,
    ComponentFactoryResolver, Type
} from '@angular/core';

import {TreeModel} from "./model/tree.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'tree',
    templateUrl: 'tree.component.html',
    styleUrls:['tree.component.css']
})
export class TreeComponent {

    @Input() treeId: string;
    @Input() treeModel:TreeModel<any, any> ;
    @Input() modelComponentMapping: ModelComponentMapping;
}
