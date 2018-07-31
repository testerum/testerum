
import {ComponentFactoryResolver, ComponentRef, EventEmitter, Injectable, OnInit} from "@angular/core";
import {StepDef} from "../../../model/step-def.model";
import {JsonTreeNodeEventModel} from "../json-tree/event/selected-json-tree-node-event.model";
import {StepTreeNodeModel} from "../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import StepsTreeUtil from "../../../functionalities/steps/steps-tree/util/steps-tree.util";
import {JsonTreeExpandUtil} from "../json-tree/util/json-tree-expand.util";
import {StepsTreeFilter} from "../../../model/step/filter/steps-tree-filter.model";
import {StepsService} from "../../../service/steps.service";
import {JsonTreeService} from "../json-tree/json-tree.service";
import {RootStepNode} from "../../../model/step/tree/root-step-node.model";
import {ComposedContainerStepNode} from "../../../model/step/tree/composed-container-step-node.model";
import {ComposedStepNode} from "../../../model/step/tree/composed-step-node.model";
import {ArrayUtil} from "../../../utils/array.util";
import {Observable} from "rxjs/Observable";
import {Subject} from "rxjs/Subject";
import {AppComponent} from "../../../app.component";
import {StepChooserComponent} from "./step-chooser.component";

@Injectable()
export class StepChooserService {

    stepPathToRemove: Path;

    treeFilter: StepsTreeFilter = StepsTreeFilter.createEmptyFilter();
    treeModel: JsonTreeModel;

    selectedStep:StepTreeNodeModel;
    selectedNodeObserver: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    private modalComponentRef: ComponentRef<StepChooserComponent>;
    private modalComponent: StepChooserComponent;
    private modalSubject: Subject<StepDef>;

    constructor(private stepsService: StepsService) {
        this.selectedNodeObserver.subscribe((item: JsonTreeNodeEventModel) => this.selectedStep = item.treeNode as StepTreeNodeModel);
    }

    showStepChooserModal(stepPathToRemove:Path = null): Observable<StepDef> {
        this.stepPathToRemove = stepPathToRemove;

        this.initializeStepsTreeFromServer();

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(StepChooserComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        modalComponentRef.instance.stepChooserService = this;

        this.modalComponentRef = modalComponentRef;
        this.modalComponent = modalComponentRef.instance;
        this.modalSubject = new Subject<StepDef>();

        return this.modalSubject.asObservable();
    }

    onStepChooseAction() {
        let chosenStepDef = this.treeModel.selectedNode? (this.treeModel.selectedNode as StepTreeNodeModel).stepDef: null;
        let cloneOfChosenStepDef = chosenStepDef ? chosenStepDef.clone() : null;
        this.modalSubject.next(cloneOfChosenStepDef);
        this.clearModal();
    }

    onCancelAction() {
        this.clearModal();
    }

    private clearModal() {
        this.modalSubject.complete();
        this.modalComponent.modal.hide();

        let modalIndex = AppComponent.rootViewContainerRef.indexOf(this.modalComponentRef);
        AppComponent.rootViewContainerRef.remove(modalIndex);

        this.modalComponentRef = null;
        this.modalComponent = null;
        this.modalSubject = null;
    }

    initializeStepsTreeFromServer() {
        this.stepsService.getStepsTree(this.treeFilter).subscribe(
            (rootStepNode:RootStepNode) => this.setTreeModel(this.stepPathToRemove, rootStepNode, 2)
        );
    }

    setTreeModel(stepPathToRemove:Path, rootStepNode:RootStepNode, expandToLevel: number = 2): void {
        this.filterNodeFromRootStepNode(rootStepNode.composedStepsRoot, stepPathToRemove);
        let newTree = StepsTreeUtil.mapRootStepToStepJsonTreeModel(rootStepNode);

        JsonTreeExpandUtil.expandTreeToLevel(newTree, expandToLevel);

        this.treeModel = newTree;
    }

    private filterNodeFromRootStepNode(composedContainerStepNode: ComposedContainerStepNode, stepPathToRemove: Path) {
        if(this.stepPathToRemove == null) return;

        let childToRemove: ComposedStepNode = null;
        for (const child of composedContainerStepNode.children) {
            if (child.path.equals(stepPathToRemove)) {
                childToRemove = child;
            } else {
                if (child instanceof ComposedContainerStepNode) {
                    this.filterNodeFromRootStepNode(child, this.stepPathToRemove)
                }
            }
        }
        if (childToRemove != null) {
            ArrayUtil.removeElementFromArray(composedContainerStepNode.children, childToRemove);
        }
    }

    setSelectedStep(selectedStep: StepTreeNodeModel) {
        this.selectedNodeObserver.emit(
            new JsonTreeNodeEventModel(selectedStep)
        );

        this.selectedStep = selectedStep;
    }
}
