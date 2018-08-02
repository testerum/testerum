import {AfterViewInit, Component, ComponentRef, OnDestroy, OnInit, ViewChild,} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {StepsService} from "../../../../../service/steps.service";
import {ComposedContainerStepNode} from "../../../../../model/step/tree/composed-container-step-node.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreeModel} from "../../../json-tree/model/json-tree.model";
import StepsPathTreeUtil from "./util/steps-path-tree.util";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {StepPathContainerModel} from "./model/step-path-container.model";
import {StepPathContainerComponent} from "./nodes/step-path-container.component";
import {StepPathModalComponentService} from "./step-path-modal.component-service";

@Component({
    selector: 'step-path-modal',
    templateUrl: 'step-path-modal.component.html',
    styleUrls: ['step-path-modal.component.scss'],
    providers:[StepPathModalComponentService]
})

export class StepPathModalComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild("modal") modal: ModalDirective;

    modalComponentRef: ComponentRef<StepPathModalComponent>;
    modalSubject: Subject<Path>;

    treeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepPathContainerModel, StepPathContainerComponent);

    constructor(private stepsService: StepsService,
                private stepPathModalComponentService: StepPathModalComponentService) {
    }

    ngOnInit(): void {
        this.stepsService.getComposedStepsDirectoryTree().subscribe((rootNode: ComposedContainerStepNode) => {
            StepsPathTreeUtil.mapToTreeModel(rootNode, this.treeModel);
        });
    }

    ngAfterViewInit(): void {
        this.modal.show();
    }

    ngOnDestroy(): void {
        this.clearStepModal();
    }

    onOkAction() {
        this.modalSubject.next(this.stepPathModalComponentService.selectedStepPathContainer ? this.stepPathModalComponentService.selectedStepPathContainer.path : null);
        this.clearStepModal();
    }

    onCancelAction() {
        this.clearStepModal();
    }

    isStepSelected(): boolean {
        return this.stepPathModalComponentService.selectedStepPathContainer != null;
    }

    getSelectedPathAsString(): string {
        return this.stepPathModalComponentService.selectedStepPathContainer ? this.stepPathModalComponentService.selectedStepPathContainer.path.toString() : "";
    }

    private clearStepModal() {
        this.modalSubject.complete();
        this.modal.hide();

        this.modalComponentRef.destroy();

        this.modalComponentRef = null;
        this.modalSubject = null;
    }
}
