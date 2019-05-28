import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges, OnDestroy,
    OnInit,
    SimpleChanges
} from '@angular/core';
import {RunnerConfigTestTreeNodeComponent} from "./nodes/runner-tree-node/runner-config-test-tree-node.component";
import {RunnerConfigTestTreeContainerModel} from "./model/runner-config-test-tree-container.model";
import {RunnerConfigTestTreeRootModel} from "./model/runner-config-test-tree-root.model";
import {RunnerConfigTestTreeNodeModel} from "./model/runner-config-test-tree-node.model";
import {RunnerConfigTestTreeService} from "./runner-config-test-tree.service";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";
import {Subscription} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'runner-config-test-tree',
    templateUrl: 'runner-config-test-tree.component.html',
    styleUrls: ['runner-config-test-tree.component.scss']
})
export class RunnerConfigTestTreeComponent implements OnInit, OnChanges, OnDestroy {

    @Input() paths: Array<Path>;
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(RunnerConfigTestTreeRootModel, RunnerConfigTestTreeNodeComponent)
        .addPair(RunnerConfigTestTreeContainerModel, RunnerConfigTestTreeNodeComponent)
        .addPair(RunnerConfigTestTreeNodeModel, RunnerConfigTestTreeNodeComponent);

    refreshTreeSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                public runnerConfigTestTreeService: RunnerConfigTestTreeService) {
    }

    ngOnInit(): void {
        this.refreshTreeSubscription = this.runnerConfigTestTreeService.refreshTreeEventEmitter.subscribe(value => {
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
        this.runnerConfigTestTreeService.treeFilter = FeaturesTreeFilter.createEmptyFilter();
        this.runnerConfigTestTreeService.initializeTreeFromServer(this.paths);
    }
}
