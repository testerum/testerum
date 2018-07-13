import {Component, Input, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {StepCallTreeService} from "../../step-call-tree.service";
import {StepTextComponent} from "../../../step-text/step-text.component";
import {StepCallEditorContainerModel} from "../../model/step-call-editor-container.model";
import {StepCallSuggestion} from "./model/step-call-suggestion.model";
import {StringUtils} from "../../../../../utils/string-utils.util";

@Component({
    selector: 'step-call-editor-container',
    templateUrl: 'step-call-editor-container.component.html',
    styleUrls: [
        'step-call-editor-container.component.css',
        '../step-call-tree.css',
        '../../../json-tree/json-tree.generic.css',
        '../../../../../generic/css/tree.css',
    ],
    encapsulation: ViewEncapsulation.None
})
export class StepCallEditorContainerComponent implements OnInit, OnDestroy {

    stepText: string;
    @Input() model: StepCallEditorContainerModel;

    suggestions: Array<StepCallSuggestion> = [];

    hasMouseOver: boolean = false;
    showParameters: boolean = true;

    constructor(private stepCallTreeService: StepCallTreeService) {
    }

    private editModeSubscription: any;
    ngOnInit() {
        if(!this.isEditMode()) this.showParameters = false;
        this.editModeSubscription = this.stepCallTreeService.editModeEventEmitter.subscribe(
            (editMode: boolean) => {
                this.showParameters = editMode;
            }
        );
    }

    ngOnDestroy() {
        if (this.editModeSubscription) {
            this.editModeSubscription.unsubscribe();
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeService.isEditMode;
    }

    toggleParameters() {
        this.showParameters = !this.showParameters;
    }

    public removeStep(): void {
        this.stepCallTreeService.removeStepCall(this.model);
    }

    searchSuggestions(event) {
        let newSuggestions: Array<StepCallSuggestion> = [];

        let query: string = event.query;
        let lowerCaseQuery = query.toLowerCase();

        if (!lowerCaseQuery.startsWith("when") && !lowerCaseQuery.startsWith("then")) {
            if (lowerCaseQuery.startsWith("given")) {
                let queryWithoutGiven = query.slice(5);
                newSuggestions.push(
                    new StepCallSuggestion("Given " + queryWithoutGiven, "Create Step -> "),
                );
            } else {
                newSuggestions.push(
                    new StepCallSuggestion("Given " + query, "Create Step -> "),
                );
            }
        }
        if (!lowerCaseQuery.startsWith("given") && !lowerCaseQuery.startsWith("then")) {
            if (lowerCaseQuery.startsWith("when")) {
                let queryWithoutGiven = query.slice(4);
                newSuggestions.push(
                    new StepCallSuggestion("When " + queryWithoutGiven, "Create Step -> "),
                );
            } else {
                newSuggestions.push(
                    new StepCallSuggestion("When " + query, "Create Step -> "),
                );
            }
        }
        if (!lowerCaseQuery.startsWith("given") && !lowerCaseQuery.startsWith("when")) {
            if (lowerCaseQuery.startsWith("then")) {
                let queryWithoutGiven = query.slice(4);
                newSuggestions.push(
                    new StepCallSuggestion("Then " + queryWithoutGiven, "Create Step -> "),
                );
            } else {
                newSuggestions.push(
                    new StepCallSuggestion("Then " + query, "Create Step -> "),
                );
            }
        }

        newSuggestions.push(
            new StepCallSuggestion("Given some virtual step"),
            new StepCallSuggestion("Given some other virtual step"),
            new StepCallSuggestion("Given some extra other virtual step"),
        );

        this.suggestions = newSuggestions;
    }
}
