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
import {Path} from "../../../../model/infrastructure/path/path.model";

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
                            this.featuresTreeService.initializeTestsTreeFromServer(this.model.parentContainer.path);
                            this.urlService.navigateToFeature(this.model.parentContainer.path);
                        }
                    )
            }
        )
    }

    toggleNode() {
        this.model.getNodeState().showChildren = !this.model.getNodeState().showChildren
    }

    expandNode() {
        this.model.getNodeState().showChildren = true;
        this.expandChildIfIsASingleFeature(this.model);
    }

    private expandChildIfIsASingleFeature(parentContainer: FeatureTreeContainerModel) {
        if(parentContainer.getChildren().length == 1 && parentContainer.getChildren()[0] instanceof FeatureTreeContainerModel) {
            let currentNodeToExpand = parentContainer.getChildren()[0] as FeatureTreeContainerModel;
            currentNodeToExpand.getNodeState().showChildren = true;
            this.expandChildIfIsASingleFeature(currentNodeToExpand);
        }
    }

    moveResource(event: any) {
        let stepToMoveTreeNode: TestTreeNodeModel = event.dragData;
        let pathToMove = stepToMoveTreeNode.path;
        let destinationPath = this.model.path;
        this.jsonTreeService.triggerMoveAction(pathToMove, destinationPath).subscribe(
            (copyEvent: JsonTreeContainerEditorEvent) => {

                this.featureService.move(pathToMove, destinationPath).subscribe( (resultPath: Path) => {
                    this.afterPasteOperation(resultPath);
                });
            }
        )
    }

    isOpenedNode(): boolean {
        return this.model.getNodeState().showChildren
    }

    onCutFeature() {
        this.setSelected();
        this.featuresTreeService.setPathToCut(this.model.path);
    }

    onCopyFeature() {
        this.setSelected();
        this.featuresTreeService.setPathToCopy(this.model.path);
    }

    onPasteFeature() {
        let destinationPath = this.model.path;
        if (this.featuresTreeService.pathToCopy) {
            let sourcePath = this.featuresTreeService.pathToCopy;
            this.jsonTreeService.triggerCopyAction(sourcePath, destinationPath).subscribe((copyEvent: JsonTreeContainerEditorEvent) => {
                    this.featureService.copy(sourcePath, destinationPath).subscribe( (resultPath: Path) => {
                        this.afterPasteOperation(resultPath);
                    });
                }
            )
        }
        if (this.featuresTreeService.pathToCut) {
            let sourcePath = this.featuresTreeService.pathToCut;
            this.jsonTreeService.triggerMoveAction(sourcePath, destinationPath).subscribe((copyEvent: JsonTreeContainerEditorEvent) => {
                    this.featureService.move(sourcePath, destinationPath).subscribe( (resultPath: Path) => {
                        this.afterPasteOperation(resultPath);
                    });
                }
            )
        }
    }

    private afterPasteOperation(resultPath: Path) {
        this.featuresTreeService.pathToCut = null;
        this.featuresTreeService.pathToCopy = null;

        this.featuresTreeService.initializeTestsTreeFromServer(resultPath);
        if (resultPath.isFile()) {
            this.urlService.navigateToTest(resultPath);
        } else {
            this.urlService.navigateToFeature(resultPath);
        }
    }

    canPaste(): boolean {
        return this.featuresTreeService.canPaste(this.model.path);
    }
}
