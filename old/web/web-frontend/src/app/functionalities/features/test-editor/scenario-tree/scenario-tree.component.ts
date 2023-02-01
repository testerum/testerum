import {Component, EventEmitter, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {TestModel} from "../../../../model/test/test.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeUtil} from "./util/scenario-tree.util";
import {ScenarioTreeComponentService} from "./scenario-tree.component-service";
import {ScenarioContainerModel} from "./model/scenario-container.model";
import {ScenarioContainerComponent} from "./nodes/scenario-container/scenario-container.component";
import {ScenarioParamsContainerModel} from "./model/scenario-params-container.model";
import {ScenarioParamsContainerComponent} from "./nodes/scenario-params-container/scenario-params-container.component";
import {ScenarioParamNodeComponent} from "./nodes/scenario-param-node/scenario-param-node.component";
import {ScenarioParamNodeModel} from "./model/scenario-param-node.model";

@Component({
    selector: 'scenario-tree',
    templateUrl: './scenario-tree.component.html',
    styleUrls: ['./scenario-tree.component.scss'],
    providers: [ScenarioTreeComponentService]
})
export class ScenarioTreeComponent implements OnChanges, OnInit {

    @Input() testModel: TestModel;
    @Input() isEditMode: boolean = false;
    @Input() editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ScenarioContainerModel, ScenarioContainerComponent)
        .addPair(ScenarioParamsContainerModel, ScenarioParamsContainerComponent)
        .addPair(ScenarioParamNodeModel, ScenarioParamNodeComponent);

    constructor(public scenarioTreeComponentService: ScenarioTreeComponentService) {
    }

    ngOnInit(): void {
        this.scenarioTreeComponentService.editModeEventEmitter = this.editModeEventEmitter;
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.initComponentTree();
    }

    private initComponentTree() {
        this.scenarioTreeComponentService.testModel = this.testModel;
        this.scenarioTreeComponentService.isEditMode = this.isEditMode;
        this.scenarioTreeComponentService.jsonTreeModel = this.jsonTreeModel;
        this.scenarioTreeComponentService.initComponentTree();
    }
}
