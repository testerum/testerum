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
import {ApplicationEventBus} from "../../../event-bus/application.eventbus";
import {UrlService} from "../../../service/url.service";
import {ComposedStepViewComponent} from "../../../generic/components/step/composed-step-view/composed-step-view.component";
import {UpdateIncompatibilityDialogComponent} from "./update-incompatilibity-dialog/update-incompatibility-dialog.component";
import {Path} from "../../../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";
import {AreYouSureModalEnum} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AreYouSureModalService} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";

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
                private applicationEventBus: ApplicationEventBus,
                private areYouSureModalService: AreYouSureModalService) {
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
            this.pathForTitle = new Path(this.model.path.directories, nodeName, null).toString();
        }
    }

    enableEditTestMode() {
        this.isEditMode = true;
        this.composedStepViewComponent.enableEditTestMode();
    }

    cancelAction(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Cancel",
            "Are you sure you want to cancel all your changes?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.cancelActionAfterConfirmation();
            }
        });
    }

    private cancelActionAfterConfirmation(): void {
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
        this.areYouSureModalService.showAreYouSureModal(
            "Delete",
            "Are you sure you want to delete this step?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.deleteActionAfterConfirmation();
            }
        });
    }

    private deleteActionAfterConfirmation(): void {
        this.stepsService.deleteComposedStepsDef(this.model).subscribe(restul => {
            this.stepsTreeService.initializeStepsTreeFromServer();
            this.urlService.navigateToSteps();
        });
    }

    saveAction(): void {
        this.composedStepViewComponent.onBeforeSave();

        if (this.isCreateAction) {
            this.doSave();
        } else {
            this.stepsService.checkComposedStepDefUpdate(this.model).subscribe (
                (compatibilityResponse: CheckComposedStepDefUpdateCompatibilityResponse) => {
                    if(!compatibilityResponse.isUniqueStepPattern) {
                        let formValidationModel = new FormValidationModel();
                        formValidationModel.fieldsWithValidationErrors.set("stepPattern", "step_pattern_already_exists");
                        FormUtil.setErrorsToForm(this.composedStepViewComponent.form, formValidationModel);
                        return;
                    }

                    if(compatibilityResponse.isCompatible ||
                        (!compatibilityResponse.isCompatible &&
                            compatibilityResponse.pathsForAffectedTests.length == 0 &&
                            compatibilityResponse.pathsForDirectAffectedSteps.length == 0 &&
                            compatibilityResponse.pathsForTransitiveAffectedSteps.length == 0)) {

                        this.doSave();
                    } else {
                        this.updateIncompatibilityDialogComponent.show(
                            compatibilityResponse.pathsForAffectedTests,
                            compatibilityResponse.pathsForDirectAffectedSteps,
                            compatibilityResponse.pathsForTransitiveAffectedSteps
                        ).subscribe(callback => {
                            this.doSave()
                        })
                    }
                }
            );
        }
    }

    private doSave(): void {
        this.stepsService.save(this.model).subscribe(
            composedStepDef => {
                this.actionsAfterSave(composedStepDef);
            },
            (validationErrorResponse: ValidationErrorResponse) => {
                FormUtil.setErrorsToForm(this.composedStepViewComponent.form, validationErrorResponse.validationModel);
            }
        );
    }

    private actionsAfterSave(composedStepDef: ComposedStepDef): void {
        this.composedStepViewComponent.setEditMode(false);

        this.applicationEventBus.triggerAfterPageSaveEvent();
        this.isEditMode = false;
        this.stepsTreeService.initializeStepsTreeFromServer(composedStepDef.path);
        this.urlService.navigateToComposedStep(composedStepDef.path);
    };
}
