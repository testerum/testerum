import {Component, OnInit, ViewChild} from '@angular/core';
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {ActivatedRoute, Router} from "@angular/router";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import {StepDef} from "../../../model/step-def.model";
import {StepCall} from "../../../model/step-call.model";
import {Arg} from "../../../model/arg/arg.model";
import {StepsService} from "../../../service/steps.service";
import {StepsTreeService} from "../steps-tree/steps-tree.service";
import {StepChooserComponent} from "../../../generic/components/step-chooser/step-chooser.component";
import {StepTreeNodeModel} from "../steps-tree/model/step-tree-node.model";
import {StepTreeContainerModel} from "../steps-tree/model/step-tree-container.model";
import {IdUtils} from "../../../utils/id.util";
import {FormValidationModel} from "../../../model/exception/form-validation.model";
import {ErrorService} from "../../../service/error.service";
import {FormUtil} from "../../../utils/form.util";
import {NgForm} from "@angular/forms";
import {ValidationErrorResponse} from "../../../model/exception/validation-error-response.model";
import {CheckComposedStepDefUpdateCompatibilityResponse} from "../../../model/step/CheckComposedStepDefUpdateCompatibilityResponse";
import {UpdateComposedStepDef} from "../../../model/step/UpdateComposedStepDef";
import {UpdateIncompatibilityDialogComponent} from "./update-incompatilibity-dialog/update-incompatibility-dialog.component";
import {ApplicationEventBus} from "../../../event-bus/application.eventbus";
import {ResourceMapEnum} from "../../resources/editors/resource-map.enum";
import {StepCallTreeService} from "../../../generic/components/step-call-tree/step-call-tree.service";

@Component({
    moduleId: module.id,
    selector: 'composed-step-editor',
    templateUrl: 'composed-step-editor.component.html',
    styleUrls: ['./composed-step-editor.component.css', '../../../generic/css/generic.css', '../../../generic/css/forms.css']
})

export class ComposedStepEditorComponent implements OnInit {

    @ViewChild(StepChooserComponent) stepChooserComponent: StepChooserComponent;
    @ViewChild(UpdateIncompatibilityDialogComponent) updateIncompatibilityDialogComponent: UpdateIncompatibilityDialogComponent;
    @ViewChild(NgForm) form: NgForm;
    StepPhaseEnum = StepPhaseEnum;

    model: ComposedStepDef = new ComposedStepDef();
    isEditMode: boolean = false;
    isCreateAction: boolean = false;

    pattern: string;

    areChildComponentsValid: boolean = true;

    constructor(private router: Router,
                private route: ActivatedRoute,
                private stepsService: StepsService,
                private stepsTreeService: StepsTreeService,
                private stepCallTreeService: StepCallTreeService,
                private errorService: ErrorService,
                private applicationEventBus: ApplicationEventBus) {
    }

    ngOnInit() {
        this.route.data.subscribe(data => {
            this.model = data['composedStepDef'];
            this.isEditMode = IdUtils.isTemporaryId(this.model.id);
            this.pattern = this.model.stepPattern.getPatternText();
            this.isCreateAction = !this.model.path.fileName
        });
    }

    onPatternChanged() {
        this.model.stepPattern.setPatternText(this.pattern)
    }

    openStepChooser() {
        this.stepChooserComponent.showStepChooserModelWithoutStepReference(this, this.model.id);
    }

    onStepChose(choseStep: StepDef): void {
        let stepCall = new StepCall();
        stepCall.stepDef = choseStep;
        for (let stepParam of choseStep.stepPattern.getParamParts()) {
            let valueArg = new Arg();
            valueArg.serverType = stepParam.serverType;
            valueArg.uiType = stepParam.uiType;
            valueArg.content = ResourceMapEnum.getResourceMapEnumByServerType(stepParam.uiType).getNewInstance();

            stepCall.args.push(valueArg);
        }

        this.stepCallTreeService.addStepCall(stepCall);
    }

    enableEditTestMode(): void {
        this.isEditMode = true;
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.router.navigate(["automated/steps"]);
        } else {
            this.stepsService.getComposedStepDef(this.model.path.toString()).subscribe(
                result => {
                    Object.assign(this.model, result);
                    this.isEditMode = false;
                }
            )
        }
    }

    deleteAction(): void {
        this.stepsService.deleteComposedStepsDef(this.model).subscribe(restul => {
            this.stepsTreeService.initializeStepsTreeFromServer();
            this.router.navigate(["automated/steps"]);
        });
    }

    saveAction(): void {
        if (this.isCreateAction) {
            this.stepsService.createComposedStepDef(this.model).subscribe(
                composedStepDef => {
                    this.actionsAfterSave(composedStepDef);
                },
                (validationErrorResponse: ValidationErrorResponse) => {
                    FormUtil.setErrorsToForm(this.form, validationErrorResponse.validationModel);
                    this.errorService.showGenericValidationError(validationErrorResponse);
                }
            );
        } else {
            let updateComposedStepDef = new UpdateComposedStepDef(this.model.path, this.model);
            this.stepsService.checkComposedStepDefUpdate(updateComposedStepDef).subscribe (
                (compatibilityResponse: CheckComposedStepDefUpdateCompatibilityResponse) => {
                    if(!compatibilityResponse.isUniqueStepPattern) {
                        let formValidationModel = new FormValidationModel();
                        formValidationModel.fieldsWithValidationErrors.set("stepPattern", "step_pattern_already_exists");
                        FormUtil.setErrorsToForm(this.form, formValidationModel);
                        return;
                    }

                    if(compatibilityResponse.isCompatible) {
                        this.callUpdateAfterCheck();
                    } else {
                        this.updateIncompatibilityDialogComponent.show(
                            compatibilityResponse.pathsForAffectedTests,
                            compatibilityResponse.pathsForDirectAffectedSteps,
                            compatibilityResponse.pathsForTransitiveAffectedSteps
                        ).subscribe(callback => {
                            this.callUpdateAfterCheck()
                        })
                    }
                }
            );
        }
    }

    private callUpdateAfterCheck(): void {
        this.stepsService.updateComposedStepDef(this.model).subscribe(
            composedStepDef => {
                this.actionsAfterSave(composedStepDef);
            },
            (validationErrorResponse: ValidationErrorResponse) => {
                FormUtil.setErrorsToForm(this.form, validationErrorResponse.validationModel);
                this.errorService.showGenericValidationError(validationErrorResponse);
            }
        );
    }

    private actionsAfterSave(composedStepDef: ComposedStepDef): void {
        this.applicationEventBus.triggerAfterPageSaveEvent();
        this.isEditMode = false;
        this.stepsTreeService.initializeStepsTreeFromServer();
        this.router.navigate(['/automated/steps/composed', {path: composedStepDef.path.toString()}]);
    };

    getPhaseEnumValues(): Array<StepPhaseEnum> {
        return [StepPhaseEnum.GIVEN, StepPhaseEnum.WHEN, StepPhaseEnum.THEN]
    }

    allowDrop(): any {
        return (dragData: StepTreeNodeModel) => {
            let isTheSameStep = dragData.stepDef == this.model;
            let isStepContainer = dragData instanceof StepTreeContainerModel;
            return this.isEditMode && !isStepContainer && !isTheSameStep;
        }
    }

    onTreeNodeDrop($event: any) {
        let stepToCopyTreeNode: StepTreeNodeModel = $event.dragData;
        this.onStepChose(stepToCopyTreeNode.stepDef)
    }
}
