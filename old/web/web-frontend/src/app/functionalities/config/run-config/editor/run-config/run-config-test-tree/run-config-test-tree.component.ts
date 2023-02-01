import {ChangeDetectorRef, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {RunConfigTestTreeNodeComponent} from "./nodes/run-tree-node/run-config-test-tree-node.component";
import {RunConfigTestTreeContainerModel} from "./model/run-config-test-tree-container.model";
import {RunConfigTestTreeRootModel} from "./model/run-config-test-tree-root.model";
import {RunConfigTestTreeNodeModel} from "./model/run-config-test-tree-node.model";
import {RunConfigTestTreeService} from "./run-config-test-tree.service";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";
import {Subscription} from "rxjs";
import {PathWithScenarioIndexes} from "../../../model/path-with-scenario-indexes.model";

@Component({
    moduleId: module.id,
    selector: 'run-config-test-tree',
    templateUrl: 'run-config-test-tree.component.html',
    styleUrls: ['run-config-test-tree.component.scss']
})
export class RunConfigTestTreeComponent implements OnInit, OnChanges, OnDestroy {

    @Input() paths: Array<PathWithScenarioIndexes>;
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(RunConfigTestTreeRootModel, RunConfigTestTreeNodeComponent)
        .addPair(RunConfigTestTreeContainerModel, RunConfigTestTreeNodeComponent)
        .addPair(RunConfigTestTreeNodeModel, RunConfigTestTreeNodeComponent);

    refreshTreeSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                public runConfigTestTreeService: RunConfigTestTreeService) {
    }

    ngOnInit(): void {
        this.refreshTreeSubscription = this.runConfigTestTreeService.refreshTreeEventEmitter.subscribe(value => {
            this.refresh();
        });
        this.refresh();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.initTree();
        this.refresh();
    }

    ngOnDestroy(): void {
        if (this.refreshTreeSubscription) this.refreshTreeSubscription.unsubscribe();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    private initTree(){
        this.runConfigTestTreeService.treeFilter = FeaturesTreeFilter.createEmptyFilter();
        this.runConfigTestTreeService.initializeTreeFromServer(this.paths);
    }
}
