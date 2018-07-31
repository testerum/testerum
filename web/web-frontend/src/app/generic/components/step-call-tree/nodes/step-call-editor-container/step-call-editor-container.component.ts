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
import {StepCallTreeService} from "../../step-call-tree.service";
import {StepCallEditorContainerModel} from "../../model/step-call-editor-container.model";
import {StepCallSuggestion} from "./model/step-call-suggestion.model";
import {StepsService} from "../../../../../service/steps.service";
import {StepDef} from "../../../../../model/step-def.model";
import {BasicStepDef} from "../../../../../model/basic-step-def.model";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";
import {StepPhaseEnum, StepPhaseUtil} from "../../../../../model/enums/step-phase.enum";
import * as stringSimilarity from 'string-similarity'
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

@Component({
    selector: 'step-call-editor-container',
    templateUrl: 'step-call-editor-container.component.html',
    styleUrls: [
        'step-call-editor-container.component.scss',
        '../step-call-tree.scss',
        '../../../json-tree/json-tree.generic.scss',
        '../../../../../generic/css/tree.scss',
    ],
    encapsulation: ViewEncapsulation.None
})
export class StepCallEditorContainerComponent implements OnInit, OnDestroy, AfterViewChecked {

    @Input() model: StepCallEditorContainerModel;

    existingStepsDefs: Array<StepDef> = [];
    existingStepsText: Array<string> = [];

    suggestions: Array<StepCallSuggestion> = [];

    hasMouseOver: boolean = false;

    @ViewChild("autoCompleteComponent") autocompleteComponent: AutoComplete;

    private onDocumentClick: (event) => void;

    constructor(private stepCallTreeService: StepCallTreeService,
                private stepsService: StepsService,
                private stepChooserService: StepChooserService,
                private messageService: MessageService,
                private element: ElementRef) {
    }

    ngOnInit() {
        this.stepsService.getBasicSteps().subscribe((basicSteps: Array<BasicStepDef>)=> {
            basicSteps.forEach(value =>
                this.existingStepsDefs.push(value)
            );
            this.addToExistingStepsText(basicSteps);
        });
        this.stepsService.getComposedStepDefs().subscribe((composedSteps: Array<ComposedStepDef>)=> {
            composedSteps.forEach(value =>
                this.existingStepsDefs.push(value)
            );
            this.addToExistingStepsText(composedSteps)
        });

        this.onDocumentClick = (event) => {
            if (StepCallEditorContainerComponent.hasAscendant(event.target, this.element.nativeElement)) {
                // we clicked inside the p-autoComplete
                return;
            }

            this.stepCallTreeService.removeStepCallEditorIfExist();
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
        document.removeEventListener('click', this.onDocumentClick);
    }

    private addToExistingStepsText(stepsDefs: Array<StepDef>) {
        for (const stepsDef of stepsDefs) {
            this.existingStepsText.push(
                StepPhaseUtil.toCamelCaseString(stepsDef.phase) + " " +stepsDef.stepPattern.getPatternText()
            )
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeService.isEditMode;
    }

    public removeStep(): void {
        this.removeThisEditorFromTree();
    }

    searchSuggestions(event) {
        let query: string = event.query;

        let newSuggestions: Array<StepCallSuggestion> = this.findSuggestionsFromExistingSteps(query);

        if (newSuggestions.length > 0 && newSuggestions[0].matchingPercentage == 1) {
            this.suggestions = newSuggestions;
            return;
        }

        let queryStepPhase = StepTextUtil.getStepPhaseFromStepText(query);
        let queryStepTextWithoutPhase = StepTextUtil.getStepTextWithoutStepPhase(query);

        if (queryStepPhase != null) {
            newSuggestions.unshift(
                new StepCallSuggestion(queryStepPhase, queryStepTextWithoutPhase, 0,"Create Step -> ")
            )
        } else {
            newSuggestions.unshift(
                new StepCallSuggestion(StepPhaseEnum.GIVEN, queryStepTextWithoutPhase, 0,"Create Step -> "),
                new StepCallSuggestion(StepPhaseEnum.WHEN, queryStepTextWithoutPhase, 0,"Create Step -> "),
                new StepCallSuggestion(StepPhaseEnum.THEN, queryStepTextWithoutPhase, 0,"Create Step -> "),
            )
        }

        this.suggestions = newSuggestions;
    }

    private findSuggestionsFromExistingSteps(query: string) {
        let suggestions: Array<StepCallSuggestion> = [];
        let compareTwoStringsResults = stringSimilarity.findBestMatch(query, this.existingStepsText);
        let compareArrayResults: Array<any> = compareTwoStringsResults.ratings;

        let sortedCompareResults: Array<any> = compareArrayResults.sort((o1,o2) => {
            return o2.rating - o1.rating;
        });

        let index = 0;
        for (const compareResult of sortedCompareResults) {
            suggestions.push(
                new StepCallSuggestion(
                    StepTextUtil.getStepPhaseFromStepText(compareResult.target),
                    StepTextUtil.getStepTextWithoutStepPhase(compareResult.target),
                    compareResult.rating
                )
            );

            if (compareResult.rating < 0.2 || compareResult.rating == 1) {
                break;
            }
            index ++;
        }
        return suggestions;
    }

    onKeyUp(event: KeyboardEvent) {
        if (event.code == 'Escape') {
            this.removeThisEditorFromTree()
        }
    }

    onSuggestionSelected(event: StepCallSuggestion) {
        console.log(this.messageService.getMessage(MessageKey.WARNING_COMPOSED_STEP_WITHOUT_STEP_CALLS));

        let newStepCall: StepCall = null;
        if (event.actionText == null) {
            newStepCall = this.createStepCallFromExistingStepDef(event);
        } else {
            newStepCall = new StepCall();
            newStepCall.stepDef = new UndefinedStepDef();
            newStepCall.stepDef.phase = event.phase;
            newStepCall.stepDef.stepPattern = new StepPattern();
            newStepCall.stepDef.stepPattern.setPatternText(event.stepCallText);

            let warning = new Warning();
            warning.type = WarningType.UNDEFINED_STEP_CALL;
            warning.message = this.messageService.getMessage(MessageKey.WARNING_UNDEFINED_STEP_CALL);
            newStepCall.addWarning(warning);

            this.addEmptyArgsToStepCall(newStepCall);
        }
        this.addNewStepCallToTree(newStepCall);
    }

    private addNewStepCallToTree(newStepCall: StepCall) {
        this.removeThisEditorFromTree();

        this.stepCallTreeService.addStepCallToParentContainer(newStepCall, this.model.parentContainer);

        this.stepCallTreeService.addStepCallEditor(this.model.parentContainer);
    }

    private createStepCallFromExistingStepDef(selectedStepCallSuggestion: StepCallSuggestion): StepCall {

        let selectedStepCallDef: StepDef = this.getStepDefByStepText(selectedStepCallSuggestion);

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

    private getStepDefByStepText(selectedStepCallSuggestion: StepCallSuggestion): StepDef {
        for (const existingStepsDef of this.existingStepsDefs) {
            if (existingStepsDef.phase == selectedStepCallSuggestion.phase
                && existingStepsDef.stepPattern.getPatternText() == selectedStepCallSuggestion.stepCallText) {
                return existingStepsDef;
            }
        }
        throw new Error("this case should not have happed: No StepDef with Phase ["+StepPhaseUtil.toLowerCaseString(selectedStepCallSuggestion.phase)+"] and text ["+selectedStepCallSuggestion.stepCallText+"] was found")
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
        this.stepCallTreeService.removeStepCallEditorIfExist();
    }

}
