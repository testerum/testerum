import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../../../generic/components/json-tree/container-editor/model/json-tree-container-editor.event";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {CopyPath} from "../../../../model/infrastructure/path/copy-path.model";
import {FeatureTreeContainerModel} from "../model/feature-tree-container.model";
import {FeaturesTreeService} from "../features-tree.service";
import {TestTreeNodeModel} from "../model/test-tree-node.model";
import {TestsService} from "../../../../service/tests.service";
import {TestsRunnerService} from "../../tests-runner/tests-runner.service";
import {Subscription} from "rxjs";
import {UrlService} from "../../../../service/url.service";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {FeatureService} from '../../../../service/feature.service';

@Component({
    moduleId: module.id,
    selector: 'feature-container',
    templateUrl: 'feature-container.component.html',
    styleUrls: [
        'feature-container.component.scss',
        '../../../../generic/css/tree.scss'
    ]
})
export class FeatureContainerComponent implements OnInit, OnDestroy {

    @Input() model: FeatureTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    selectedNodeSubscription: Subscription;
    getTestsUnderPathSubscription: Subscription;

    constructor(private urlService: UrlService,
                private jsonTreeService: JsonTreeService,
                private featuresTreeService: FeaturesTreeService,
                private testsService: TestsService,
                private testsRunnerService: TestsRunnerService,
                private featureService: FeatureService) {
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
        if(this.getTestsUnderPathSubscription) this.getTestsUnderPathSubscription.unsubscribe();
    }

    allowDrop():any {
        return (dragData: TestTreeNodeModel) => {
            let isNotChildOfContainer = !JsonTreePathUtil.isChildOf(dragData, this.model) &&
                !JsonTreePathUtil.isDirectChildOf(this.model, dragData) &&
                this.model !== dragData;
            return isNotChildOfContainer;
        }
    }

    runTests() {
        this.testsRunnerService.runTests([this.model.path]);
    }

    setSelected() {
        this.urlService.navigateToFeature(this.model.path);
    }

    showCreateTest() {
        this.urlService.navigateToCreateTest(this.model.path);
    }

    showCreateDirectoryModal(): void {
        this.urlService.navigateToCreateFeature(this.model.path);
    }

    deleteDirectory(): void {
        this.jsonTreeService.triggerDeleteContainerAction(this.model.name).subscribe(
            (deleteEvent: JsonTreeContainerEditorEvent) => {

                this.featureService
                    .delete(this.model.path)
                    .subscribe(
                        it => {
                            this.testsService.showTestsScreen();
                            this.featuresTreeService.initializeTestsTreeFromServer(null);
                        }
                    )
            }
        )
    }

    toggleNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    expandNode() {
        this.model.jsonTreeNodeState.showChildren = true;
    }

    copyResource(event: any) {
        let stepToCopyTreeNode: TestTreeNodeModel = event.dragData;
        let pathToCopy = stepToCopyTreeNode.path;
        let destinationPath = this.model.path;
        this.jsonTreeService.triggerCopyAction(pathToCopy, destinationPath).subscribe(
            (copyEvent: JsonTreeContainerEditorEvent) => {

                let copyPath = new CopyPath(pathToCopy, destinationPath);
                this.testsService.moveDirectoryOrFile (
                    copyPath
                ).subscribe(
                    it => {
                        this.featuresTreeService.copy(pathToCopy, destinationPath);
                    }
                )
            }
        )
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
