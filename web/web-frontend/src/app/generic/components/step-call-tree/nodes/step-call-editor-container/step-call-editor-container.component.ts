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
import * as Fuse from 'fuse.js'
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
import {ArrayUtil} from "../../../../../utils/array.util";
import {PathUtil} from "../../../../../utils/path.util";

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

    private MAX_SUGGESTION_NUMBER = 10;

    @Input() model: StepCallEditorContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    allPossibleSuggestions: StepCallSuggestion[] = [];
    suggestions: StepCallSuggestion[] = [];

    hasMouseOver: boolean = false;
    fuseSearch: Fuse<StepCallSuggestion, Fuse.FuseOptions<StepCallSuggestion>>;
    fuseSearchWithPerfercMatch: Fuse<StepCallSuggestion, Fuse.FuseOptions<StepCallSuggestion>>;

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

            this.fuseSearch = new Fuse<StepCallSuggestion, any>(
                this.allPossibleSuggestions,
        {
                    keys: ["stepCallText"],
                    shouldSort: true,
                    includeScore: true,
                    includeMatches: false,
                    tokenize: false,
                    matchAllTokens: false,
                    findAllMatches: false,
                    threshold: 0.5,
                    location: 0,
                    distance: 50,
                    maxPatternLength: 32,
                }
            );

            this.fuseSearchWithPerfercMatch = new Fuse<StepCallSuggestion, any>(
                this.allPossibleSuggestions,
        {
                    keys: ["stepCallText"],
                    shouldSort: false,
                    includeScore: true,
                    includeMatches: false,
                    threshold: 0.0,
                    location: 0,
                    distance: 0,
                    maxPatternLength: 500,
                }
            )
        });

        this.onDocumentClick = (event) => {
            if (StepCallEditorContainerComponent.hasAscendant(event.target, this.element.nativeElement)) {
                // we clicked inside the p-autoComplete
                return;
            }

            this.stepCallTreeComponentService.removeStepCallEditorIfExist();
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
        let query: string = event.query;
        let newSuggestions = [];


        let fuseSearchResult: Fuse.FuseResult<StepCallSuggestion>[] = this.searchMatchingSteps(query);

        for (const fuseSearchResultElement of fuseSearchResult) {
            newSuggestions.push(fuseSearchResultElement.item);
        }

        let queryStepPhase = StepTextUtil.getStepPhaseFromStepText(query);
        let queryStepTextWithoutPhase = StepTextUtil.getStepTextWithoutStepPhase(query).trim();

        if (fuseSearchResult.length > 0 && fuseSearchResult[0].score == 0) {
            this.suggestions = newSuggestions;
            return;
        }

        let hasGetPhasePerfectMatch = this.isAnyPerfectMatchForStepText("Given " + queryStepTextWithoutPhase);
        let hasWhenPhasePerfectMatch = this.isAnyPerfectMatchForStepText("When " + queryStepTextWithoutPhase);
        let hasThenPhasePerfectMatch = this.isAnyPerfectMatchForStepText("Then " + queryStepTextWithoutPhase);
        let hasAndPhasePerfectMatch = false;
        let previewsStepDef: StepDef = null;

        if (StepPhaseEnum.AND == queryStepPhase && this.findStepIndex() > 0) {
            previewsStepDef = this.getPreviousStepDef();
            let previewsStepPhaseAsString = StepPhaseUtil.toCamelCaseString(previewsStepDef.phase);
            hasAndPhasePerfectMatch = this.isAnyPerfectMatchForStepText(previewsStepPhaseAsString + " " + query);
        }

        if(!hasGetPhasePerfectMatch && !hasWhenPhasePerfectMatch && !hasThenPhasePerfectMatch && !hasAndPhasePerfectMatch) {
            if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.THEN)
                newSuggestions.unshift(this.createUndefinedStepCallSuggestion(StepPhaseEnum.THEN, queryStepTextWithoutPhase, "Create Step &rarr;&nbsp;"));
            if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.WHEN)
                newSuggestions.unshift(this.createUndefinedStepCallSuggestion(StepPhaseEnum.WHEN, queryStepTextWithoutPhase, "Create Step &rarr;&nbsp;"));
            if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.GIVEN)
                newSuggestions.unshift(this.createUndefinedStepCallSuggestion(StepPhaseEnum.GIVEN, queryStepTextWithoutPhase, "Create Step &rarr;&nbsp;"));

            if (StepPhaseEnum.AND == queryStepPhase && previewsStepDef != null) {
                if(queryStepPhase == null || queryStepPhase == StepPhaseEnum.AND)
                    newSuggestions.unshift(
                    this.createUndefinedStepCallSuggestion(previewsStepDef.phase, queryStepTextWithoutPhase,"Create Step &rarr;&nbsp;", true)
                );
            }
        }

        this.suggestions = newSuggestions;
    }

    private searchMatchingSteps(query: string): Fuse.FuseResult<StepCallSuggestion>[] {
        // @ts-ignore
        let fuseSearchResult: Fuse.FuseResult<StepCallSuggestion>[] = this.fuseSearch.search(query) as Fuse.FuseResult<StepCallSuggestion>[];

        //this fixes a bug in FuseJs. If the search query is long, at a moment is going to retrun all the steps
        if (fuseSearchResult.length == this.allPossibleSuggestions.length && fuseSearchResult.length > this.MAX_SUGGESTION_NUMBER) {
            return [];
        }

        return fuseSearchResult.slice(0, this.MAX_SUGGESTION_NUMBER);

    }

    private isAnyPerfectMatchForStepText(query: string): boolean {
        let searchResults = this.fuseSearchWithPerfercMatch.search(query);
        if (searchResults.length == 0) return false;
        for (const searchResult of searchResults) {
            if(searchResult.score != 0) return false;
        }

        return true;
    }

    private findStepIndex(): number {
        return this.model.parentContainer.getChildren().indexOf(this.model);
    }

    private getPreviousStepDef(): StepDef {
        let previewsStepIndex = this.model.parentContainer.getChildren().indexOf(this.model) - 1;
        return (this.model.parentContainer.getChildren()[previewsStepIndex] as StepCallContainerModel).stepCall.stepDef;
    }

    onKeyUp(event: KeyboardEvent) {
        if (event.code == 'Escape') {
            this.removeThisEditorFromTree()
        }
    }

    createUndefinedStepCallSuggestion(stepPhase: StepPhaseEnum, stepTextWithoutPhase: string, actionText: string, userAndAsTextPhase: boolean = false): StepCallSuggestion {

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


}
