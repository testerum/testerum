import {
    AfterViewChecked,
    Component,
    ElementRef,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {StepCallEditorContainerModel} from "../../model/step-call-editor-container.model";
import {StepCallSuggestion} from "./model/step-call-suggestion.model";
import {StepsService} from "../../../../../service/steps.service";
import {StepDef} from "../../../../../model/step-def.model";
import {BasicStepDef} from "../../../../../model/basic-step-def.model";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";
import {StepPhaseEnum, StepPhaseUtil} from "../../../../../model/enums/step-phase.enum";
import {StepCall} from "../../../../../model/step-call.model";
import {StepTextUtil} from "./util/StepTextUtil";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {Arg} from "../../../../../model/arg/arg.model";
import {StepPattern} from "../../../../../model/text/step-pattern.model";
import {AutoComplete} from "primeng/primeng";
import {UndefinedStepDef} from "../../../../../model/undefined-step-def.model";
import {MessageService} from "../../../../../service/message.service";
import {MessageKey} from "../../../../../model/messages/message.enum";
import {Warning} from "../../../../../model/warning/Warning";
import {WarningType} from "../../../../../model/warning/WarningType";
import {StepChooserService} from "../../../step-chooser/step-chooser.service";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {merge} from "rxjs";
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {PathUtil} from "../../../../../utils/path.util";
import {StepSearch} from "../../../../../utils/step-search/step-search.class";
import {ParamNameValidatorDirective} from "../../../../validators/param-name-validator.directive";

@Component({
    selector: 'step-call-editor-container',
    templateUrl: 'step-call-editor-container.component.html',
    styleUrls: [
        'step-call-editor-container.component.scss',
        '../step-call-tree.scss',
        '../../../../../generic/css/tree.scss',
    ],
    encapsulation: ViewEncapsulation.None
})
export class StepCallEditorContainerComponent implements OnInit, OnDestroy, AfterViewChecked {

    @Input() model: StepCallEditorContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    allPossibleSuggestions: StepCallSuggestion[] = [];
    suggestions: StepCallSuggestion[] = [];

    errorsKey: string[] = [];
    invalidParameterName: string;

    hasMouseOver: boolean = false;
    stepSearch: StepSearch<StepCallSuggestion>;

    @ViewChild("autoCompleteComponent") autocompleteComponent: AutoComplete;

    private onDocumentClick: (event) => void;

    constructor(private stepsService: StepsService,
                private stepChooserService: StepChooserService,
                private messageService: MessageService,
                private element: ElementRef,
                private stepCallTreeComponentService: StepCallTreeComponentService) {
    }

    ngOnInit() {
        merge(this.stepsService.getBasicSteps(), this.stepsService.getComposedStepDefs()).subscribe((steps: BasicStepDef[] | ComposedStepDef[]) => {
            for (const stepDef of steps) {
                let stepDefText = StepPhaseUtil.toCamelCaseString(stepDef.phase) + " " +stepDef.stepPattern.getPatternText();
                let stepCallSuggestion = new StepCallSuggestion(stepDefText, stepDef);
                this.allPossibleSuggestions.push(stepCallSuggestion);
            }

            this.stepSearch = new StepSearch(this.allPossibleSuggestions);
        });

        this.onDocumentClick = (event) => {
            if (StepCallEditorContainerComponent.hasAscendant(event.target, this.element.nativeElement)) {
                // we clicked inside the p-autoComplete
                return;
            }

            if (this.suggestions.length == 0) {
                this.stepCallTreeComponentService.removeStepCallEditorIfExist();
            } else {
                this.onSuggestionSelected(this.suggestions[0]);
            }
        };
        document.addEventListener('click', this.onDocumentClick, true);
    }

    private isFocusSet = false;
    ngAfterViewChecked(): void {
        if (!this.isFocusSet) {
            this.isFocusSet = true;
            setTimeout(() => {
                this.autocompleteComponent.focusInput();
            });
        }
    }

    private static hasAscendant(element, ascendantElement) {
        let current = element;

        while (current != null) {
            if (current.tagName === ascendantElement.tagName) {
                return true;
            }

            current = current.parentNode;
        }

        return false;
    }

    ngOnDestroy() {
        document.removeEventListener('click', this.onDocumentClick, true);
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeComponentService.isEditMode;
    }

    public removeStep(): void {
        this.removeThisEditorFromTree();
    }

    searchSuggestions(event) {
        this.errorsKey = [];

        let query: string = event.query;
        let previewsStepDefPhase = this.getPreviewsStepDefPhase();
        let newSuggestions = this.stepSearch.find(query, previewsStepDefPhase);

        let queryStepPhase = StepTextUtil.getStepPhaseFromStepText(query);
        let queryStepTextWithoutPhase = StepTextUtil.getStepTextWithoutStepPhase(query).trim();

        if((newSuggestions.length > 0 && !newSuggestions[0].isPerfectMatch) || newSuggestions.length == 0) {
            if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.THEN)
                newSuggestions.unshift(this.createUndefinedStepCallSuggestion(StepPhaseEnum.THEN, queryStepTextWithoutPhase, "Create Step &rarr;&nbsp;"));
            if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.WHEN)
                newSuggestions.unshift(this.createUndefinedStepCallSuggestion(StepPhaseEnum.WHEN, queryStepTextWithoutPhase, "Create Step &rarr;&nbsp;"));
            if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.GIVEN)
                newSuggestions.unshift(this.createUndefinedStepCallSuggestion(StepPhaseEnum.GIVEN, queryStepTextWithoutPhase, "Create Step &rarr;&nbsp;"));

            if (StepPhaseEnum.AND == queryStepPhase && previewsStepDefPhase != null) {
                if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.AND)
                    newSuggestions.unshift(
                    this.createUndefinedStepCallSuggestion(previewsStepDefPhase, queryStepTextWithoutPhase,"Create Step &rarr;&nbsp;", true)
                );
            }

            if (newSuggestions.length > 0 && newSuggestions[0].actionText != null) {
                this.errorsKey = this.getStepCallErrors(newSuggestions[0].stepDef);
                if (this.errorsKey.length > 0) {
                    this.suggestions = [];
                    return;
                }
            }
        }

        this.suggestions = newSuggestions;
    }

    private findStepIndex(): number {
        return this.model.parentContainer.getChildren().indexOf(this.model);
    }

    private getPreviousStepDef(): StepDef | null {
        let previewsStepIndex = this.model.parentContainer.getChildren().indexOf(this.model) - 1;
        if(previewsStepIndex < 0) return null;
        return (this.model.parentContainer.getChildren()[previewsStepIndex] as StepCallContainerModel).stepCall.stepDef;
    }

    private getPreviewsStepDefPhase(): StepPhaseEnum | null {
        let previewsStepDef = this.getPreviousStepDef();
        if(!previewsStepDef) return null;
        return previewsStepDef.phase;
    }

    onKeyUp(event: KeyboardEvent) {
        if (event.code == 'Escape') {
            this.removeThisEditorFromTree()
        }
    }

    private createUndefinedStepCallSuggestion(stepPhase: StepPhaseEnum, stepTextWithoutPhase: string, actionText: string, userAndAsTextPhase: boolean = false): StepCallSuggestion {

        let stepDef = new UndefinedStepDef();
        stepDef.path = PathUtil.generateStepDefPath(this.stepCallTreeComponentService.containerPath, stepPhase, stepTextWithoutPhase);
        stepDef.phase = stepPhase;

        stepDef.stepPattern = new StepPattern();
        stepDef.stepPattern.setPatternText(stepTextWithoutPhase);

        let stepPhaseForText = StepPhaseUtil.toCamelCaseString(userAndAsTextPhase ? StepPhaseEnum.AND : stepPhase);
        let stepText =  stepPhaseForText + " " + stepTextWithoutPhase;

        return new StepCallSuggestion(stepText, stepDef, actionText);
    }

    onSuggestionSelected(event: StepCallSuggestion) {
        this.suggestions = [];

        let newStepCall: StepCall = this.createStepCallFromExistingStepDef(event);

        if (event.actionText != null) {
            let warning = new Warning();
            warning.type = WarningType.UNDEFINED_STEP_CALL;
            warning.message = this.messageService.getMessage(MessageKey.WARNING_UNDEFINED_STEP_CALL);
            newStepCall.addWarning(warning);
        }
        this.addNewStepCallToTree(newStepCall);
    }

    private addNewStepCallToTree(newStepCall: StepCall) {
        this.removeThisEditorFromTree();

        this.stepCallTreeComponentService.addStepCallToParentContainer(newStepCall, this.model.parentContainer);

        this.stepCallTreeComponentService.addStepCallEditor(this.model.parentContainer);
    }

    private createStepCallFromExistingStepDef(selectedStepCallSuggestion: StepCallSuggestion): StepCall {

        let selectedStepCallDef: StepDef = selectedStepCallSuggestion.stepDef;

        let stepCall = new StepCall();
        stepCall.stepDef = selectedStepCallDef;
        this.addEmptyArgsToStepCall(stepCall);
        return stepCall;
    }

    private addEmptyArgsToStepCall(stepCall: StepCall) {
        let paramParts: Array<ParamStepPatternPart> =  stepCall.stepDef.stepPattern.getParamParts();
        if (paramParts.length > 0) {
            for (const paramPart of paramParts) {
                stepCall.args.push(
                    Arg.createArgInstanceFromParamStepPatternPart(paramPart)
                );
            }
        }
    }

    selectStepFromPopup() {
        this.stepChooserService.showStepChooserModal().subscribe((stepDef: StepDef) => {
            this.removeThisEditorFromTree();

            let stepCall = new StepCall();
            stepCall.stepDef = stepDef;
            this.addEmptyArgsToStepCall(stepCall);

            this.addNewStepCallToTree(stepCall);
        })
    }

    private removeThisEditorFromTree() {
        this.stepCallTreeComponentService.removeStepCallEditorIfExist();
    }

    private getStepCallErrors(stepDef: StepDef): string[] {
        this.invalidParameterName = null;

        let paramParts = stepDef.stepPattern.getParamParts();
        for (const paramPart of paramParts) {
            if (!ParamNameValidatorDirective.isValidParamName(paramPart.name)) {
                this.invalidParameterName = paramPart.name;
                return ["invalidParamName"]
            }
        }
        return [];
    }
}
