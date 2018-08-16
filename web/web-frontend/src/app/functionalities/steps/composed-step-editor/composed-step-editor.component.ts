import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import {StepsService} from "../../../service/steps.service";
import {StepsTreeService} from "../steps-tree/steps-tree.service";
import {IdUtils} from "../../../utils/id.util";
import {FormValidationModel} from "../../../model/exception/form-validation.model";
import {ErrorService} from "../../../service/error.service";
import {FormUtil} from "../../../utils/form.util";
import {ValidationErrorResponse} from "../../../model/exception/validation-error-response.model";
import {CheckComposedStepDefUpdateCompatibilityResponse} from "../../../model/step/CheckComposedStepDefUpdateCompatibilityResponse";
import {UpdateComposedStepDef} from "../../../model/step/UpdateComposedStepDef";
import {ApplicationEventBus} from "../../../event-bus/application.eventbus";
import {UrlService} from "../../../service/url.service";
import {ComposedStepViewComponent} from "../../../generic/components/step/composed-step-view/composed-step-view.component";
import {UpdateIncompatibilityDialogComponent} from "./update-incompatilibity-dialog/update-incompatibility-dialog.component";
import {Path} from "../../../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'composed-step-editor',
    templateUrl: 'composed-step-editor.component.html',
    styleUrls: ['./composed-step-editor.component.scss']
})

export class ComposedStepEditorComponent implements OnInit, OnDestroy {

    model: ComposedStepDef;
    isEditMode: boolean = false;
    isCreateAction: boolean = false;
    pathForTitle: string = "";

    @ViewChild(ComposedStepViewComponent) composedStepViewComponent: ComposedStepViewComponent;
    @ViewChild(UpdateIncompatibilityDialogComponent) updateIncompatibilityDialogComponent: UpdateIncompatibilityDialogComponent;

    private editModeSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private stepsService: StepsService,
                private stepsTreeService: StepsTreeService,
                private errorService: ErrorService,
                private applicationEventBus: ApplicationEventBus) {
    }

    ngOnInit() {
        this.route.data.subscribe(data => {
            this.model = data['composedStepDef'];
            this.isEditMode = IdUtils.isTemporaryId(this.model.id);
            this.composedStepViewComponent.isEditMode = IdUtils.isTemporaryId(this.model.id);
            this.isCreateAction = !this.model.path.fileName;

            this.initPathForTitle();
        });

        this.editModeSubscription = this.composedStepViewComponent.editModeEventEmitter.subscribe((editMode: boolean) => {
            this.isEditMode = editMode;
        })
    }

    ngOnDestroy(): void {
        if(this.editModeSubscription) this.editModeSubscription.unsubscribe();
    }

    initPathForTitle() {
        this.pathForTitle = "";
        let nodeName = null;
        if (!this.isCreateAction) {
            nodeName = this.model.toString();
        }

        if (this.model.path) {
            this.pathForTitle = "/" + new Path(this.model.path.directories, nodeName, null).toString();
        }
    }

    enableEditTestMode() {
        this.isEditMode = true;
        this.composedStepViewComponent.enableEditTestMode();
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.urlService.navigateToSteps();
        } else {
            this.stepsService.getComposedStepDef(this.model.path.toString()).subscribe(
                result => {
                    Object.assign(this.model, result);
                    this.isEditMode = false;
                    this.composedStepViewComponent.setEditMode(this.isEditMode)
                }
            )
        }
    }

    deleteAction(): void {
        this.stepsService.deleteComposedStepsDef(this.model).subscribe(restul => {
            this.stepsTreeService.initializeStepsTreeFromServer();
            this.urlService.navigateToSteps();
        });
    }

    saveAction(): void {
        this.composedStepViewComponent.onBeforeSave();
        if (this.isCreateAction) {
            this.stepsService.createComposedStepDef(this.model).subscribe(
                composedStepDef => {
                    this.actionsAfterSave(composedStepDef);
                },
                (validationErrorResponse: ValidationErrorResponse) => {
                    FormUtil.setErrorsToForm(this.composedStepViewComponent.form, validationErrorResponse.validationModel);
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
                        FormUtil.setErrorsToForm(this.composedStepViewComponent.form, formValidationModel);
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
                FormUtil.setErrorsToForm(this.composedStepViewComponent.form, validationErrorResponse.validationModel);
                this.errorService.showGenericValidationError(validationErrorResponse);
            }
        );
    }

    private actionsAfterSave(composedStepDef: ComposedStepDef): void {
        this.applicationEventBus.triggerAfterPageSaveEvent();
        this.isEditMode = false;
        this.stepsTreeService.initializeStepsTreeFromServer(composedStepDef.path);
        this.urlService.navigateToComposedStep(composedStepDef.path);
    };
}
