import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {IdUtils} from "../../../../../utils/id.util";
import {ResourceContextActions} from "../../infrastructure/model/resource-context-actions.model";

@Component({
    selector: 'boolean-input-basic-resource',
    templateUrl: './boolean-input-basic-resource.component.html',
    styleUrls: ['./boolean-input-basic-resource.component.scss']
})
export class BooleanInputBasicResourceComponent {

    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() contextActions: ResourceContextActions;

    @ViewChild('booleanInput') inputElementRef: ElementRef;
    private tempValueHolder: string;

    id = IdUtils.getTemporaryId();
    possibleValues: string[] = ["true", "false"];

    onValueChange(newValue: string) {
        this.model.content = newValue;
    }

    onInputEvent(event: MouseEvent) {
        this.tempValueHolder = this.inputElementRef.nativeElement.value;
        this.inputElementRef.nativeElement.value = '';
        let that = this;
        setTimeout(() => {
            this.inputElementRef.nativeElement.value = this.tempValueHolder;
        }, 10);
    }
}
