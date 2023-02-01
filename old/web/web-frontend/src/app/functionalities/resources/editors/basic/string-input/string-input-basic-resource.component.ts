import {Component, Input, OnInit} from '@angular/core';
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {ResourceContextActions} from "../../infrastructure/model/resource-context-actions.model";

@Component({
    selector: 'string-input-basic-resource',
    templateUrl: './string-input-basic-resource.component.html',
    styleUrls: ['./string-input-basic-resource.component.scss']
})
export class StringInputBasicResourceComponent implements OnInit {

    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() contextActions: ResourceContextActions;

    multiLineText: boolean = false;

    constructor() {
    }

    ngOnInit() {
        this.multiLineText = !this.model.isSmallText();
    }

    isSmallText(): boolean {
        return this.model.isSmallText()
    }

    isMultilineText(): boolean {
        if (!this.model.isSmallText()) {
            return true;
        }
        return this.multiLineText;
    }

    onKeyUp(event: KeyboardEvent) {
        if (event.code == 'Escape') {
            this.contextActions.cancel();
        }

        if (event.code == 'Enter' && !this.isMultilineText()) {
            this.contextActions.save();
        }
    }

    setMultilineText(multiline: boolean) {
        this.multiLineText = multiline;
    }
}
