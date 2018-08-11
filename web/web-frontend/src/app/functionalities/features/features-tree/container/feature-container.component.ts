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
import {JsonTreeNodeEventModel} from "../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {Subscription} from "rxjs";
import {TestModel} from "../../../../model/test/test.model";
import {UrlService} from "../../../../service/url.service";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'feature-container',
    templateUrl: 'feature-container.component.html',
    styleUrls: [
        'feature-container.component.scss',
        '../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../generic/css/tree.scss'
    ]
})
export class FeatureContainerComponent implements OnInit, OnDestroy {

    @Input() model: FeatureTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;
    isSelected:boolean = false;

    selectedNodeSubscription: Subscription;
    getTestsUnderPathSubscription: Subscription;

    constructor(private urlService: UrlService,
                private jsonTreeService: JsonTreeService,
                private featuresTreeService: FeaturesTreeService,
                private testsService: TestsService,
                private testsRunnerService: TestsRunnerService) {
        this.selectedNodeSubscription = jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onFeatureSelected(item));
    }

    ngOnInit(): void {
        this.isSelected = this.jsonTreeService.isSelectedNodeEqualsWithTreeNode(this.model);
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
        if(this.getTestsUnderPathSubscription) this.getTestsUnderPathSubscription.unsubscribe();
    }

    onFeatureSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) : void {
        this.isSelected = selectedJsonTreeNodeEventModel.treeNode == this.model;
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
        this.jsonTreeService.setSelectedNode(this.model);
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

                this.testsService.deleteDirectory(
                    this.model.path
                ).subscribe(
                    it => {
                        this.testsService.showTestsScreen();
                        this.featuresTreeService.initializeTestsTreeFromServer(null);
                    }
                )
            }
        )
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
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
