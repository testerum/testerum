import {
    Component,
    EventEmitter,
    Input, OnDestroy,
    OnInit,
    Output,
} from '@angular/core';
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {MapPathChooserUtil} from "./util/map-path-chooser.util";
import {ResourceService} from "../../../service/resources/resource.service";
import {PathChooserContainerModel} from "./model/path-chooser-container.model";
import {PathChooserNodeModel} from "./model/path-chooser-node.model";
import {PathChooserContainerComponent} from "./container/path-chooser-container.component";
import {PathChooserNodeComponent} from "./container/node/path-chooser-node.component";
import {ResourceMapEnum} from "../../../functionalities/resources/editors/resource-map.enum";
import {PathChooserService} from "./path-chooser.service";
import {Subscription} from "rxjs/Subscription";
import {JsonTreeNodeEventModel} from "../json-tree/event/selected-json-tree-node-event.model";
import {StepTreeNodeModel} from "../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {JsonTreeService} from "../json-tree/json-tree.service";

@Component({
    selector: 'path-chooser',
    templateUrl: 'path-chooser.component.html'
})
export class PathChooserComponent implements OnInit, OnDestroy {

    @Input() resourceMapping: ResourceMapEnum;
    @Input() paths: Array<Path> = [];
    @Input() showFiles: boolean = true;
    @Input() allowDirSelection: boolean = true;

    @Output() selectPath = new EventEmitter<Path>();

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();

    selectedNodeSubscription: Subscription;

    constructor(private pathChooserService: PathChooserService,
                private treeService:JsonTreeService) {
    }

    ngOnInit(): void {
        this.initJsonModel();

        this.selectedNodeSubscription = this.treeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent: JsonTreeNodeEventModel) => {
                this.selectPath.emit((selectedNodeEvent.treeNode as PathChooserNodeModel).path);
            }
        );
    }

    ngOnDestroy(): void {
        if (this.selectedNodeSubscription) {
            this.selectedNodeSubscription.unsubscribe();
        }
    }

    initJsonModel() {
        MapPathChooserUtil.mapPathsToPathChooserModel(
            this.paths,
            this.jsonTreeModel,
            this.resourceMapping,
            this.showFiles,
            this.allowDirSelection
        );
    }

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(PathChooserContainerModel, PathChooserContainerComponent)
        .addPair(PathChooserNodeModel, PathChooserNodeComponent)
}
