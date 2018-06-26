import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeContainerEditorEvent} from "../../../../generic/components/json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {RenamePath} from "../../../../model/infrastructure/path/rename-path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {CopyPath} from "../../../../model/infrastructure/path/copy-path.model";
import {FeatureTreeContainerModel} from "../model/feature-tree-container.model";
import {FeaturesTreeService} from "../features-tree.service";
import {TestTreeNodeModel} from "../model/test-tree-node.model";
import {TestsService} from "../../../../service/tests.service";
import {ArrayUtil} from "../../../../utils/array.util";
import {TestsRunnerService} from "../../tests-runner/tests-runner.service";
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {JsonTreeNodeEventModel} from "../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {Subscription} from "rxjs/Subscription";
import {TestModel} from "../../../../model/test/test.model";

@Component({
    moduleId: module.id,
    selector: 'feature-container',
    templateUrl: 'feature-container.component.html',
    styleUrls: [
        'feature-container.component.css',
        '../../../../generic/components/json-tree/json-tree.generic.css',
        '../../../../generic/css/tree.css'
    ]
})
export class FeatureContainerComponent implements OnInit, OnDestroy {

    @Input() model: FeatureTreeContainerModel;
    hasMouseOver: boolean = false;
    isSelected:boolean = false;

    selectedNodeSubscription: Subscription;
    getTestsUnderPathSubscription: Subscription;

    constructor(private router: Router,
                private jsonTreeService: JsonTreeService,
                private featuresTreeService: FeaturesTreeService,
                private testsService: TestsService,
                private testsRunnerService: TestsRunnerService) {
    }

    ngOnInit(): void {
        this.selectedNodeSubscription = this.jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
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
        this.getTestsUnderPathSubscription = this.testsService.getAllAutomatedTestsUnderContaier(this.model.path).subscribe(
            (tests:Array<TestModel>) => {
                this.testsRunnerService.runTests(tests);
            }
        );
    }

    onStepSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) : void {
        if(selectedJsonTreeNodeEventModel.treeNode == this.model) {
            this.isSelected = true;
        } else {
            this.isSelected = false;
        }
    }

    setSelected() {
        this.jsonTreeService.setSelectedNode(this.model);

        this.router.navigate(["/features/show", {path : this.model.path.toString()} ]);
    }

    showCreateTest() {
        this.router.navigate(["/features/tests/create", { path : this.model.path.toString()}]);
    }

    showCreateDirectoryModal(): void {
        let childrenContainersName = this.getChildrenContainersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {
                let pathDirectories: Array<string> = ArrayUtil.copyArray(this.model.path.directories);
                pathDirectories.push(createEvent.newName);

                let newContainer = new FeatureTreeContainerModel(
                    this.model,
                    createEvent.newName,
                    new Path(pathDirectories, null, null)
                );
                newContainer.editable = true;
                this.model.children.push(newContainer);
                this.model.sort();
            }
        )
    }

    private getChildrenContainersName() {
        let childrenContainersName: Array<string> = [];
        for (const child of this.model.children) {
            if (child.isContainer()) {
                childrenContainersName.push(child.name)
            }
        }
        return childrenContainersName;
    }

    private getSiblingNames() {
        let siblingNames: Array<string> = [];
        let siblingContainers: Array<JsonTreePathNode> = this.model.parentContainer.getChildren();
        for (const child of siblingContainers) {
            if (child.isContainer()) {
                siblingNames.push(child.name)
            }
        }
        return siblingNames;
    }

    deleteDirectory(): void {
        this.jsonTreeService.triggerDeleteContainerAction(this.model.name).subscribe(
            (deleteEvent: JsonTreeContainerEditorEvent) => {

                this.testsService.deleteDirectory(
                    this.model.path
                ).subscribe(
                    it => {
                        this.testsService.showTestsScreen();
                        this.featuresTreeService.initializeTestsTreeFromServer();
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
