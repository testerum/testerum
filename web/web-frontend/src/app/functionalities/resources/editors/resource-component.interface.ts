import {Input} from "@angular/core";
import {Resource} from "../../../model/resource/resource.model";
import {NgForm} from "@angular/forms";
import {ParamStepPatternPart} from "../../../model/text/parts/param-step-pattern-part.model";

export abstract class ResourceComponent<T extends Resource<T>> {

    @Input() name: string;
    @Input() model:T;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = true;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;

    abstract isFormValid(): boolean;
    abstract getForm(): NgForm;
    abstract refresh(): void;
}
