import {Component, Input} from '@angular/core';
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {Arg} from "../../../../model/arg/arg.model";
import {VariableHolder} from "../../../variable/variable-holder.model";
import {BasicResource} from "../../../../model/resource/basic/basic-resource.model";
import {StringUtils} from "../../../../utils/string-utils.util";

@Component({
    moduleId: module.id,
    selector: 'step-text-param',
    templateUrl: 'step-text-param.component.html',
    styleUrls: ['step-text-param.component.scss']
})

export class StepTextParamComponent {

    @Input() isStepCall: boolean;
    @Input() paramStepPart:ParamStepPatternPart;
    @Input() arg:Arg;
    @Input() variableHolder:VariableHolder;

    hasValue(): boolean {
        if (this.arg.content instanceof BasicResource) {
            return this.arg.content.content != null
                && (typeof this.arg.content.content == "string" && !StringUtils.isEmpty(this.arg.content.content));
        }else
        if (this.arg.content) {
            return true;
        }
        return false
    }

    getArgumentName(): string {
        if (this.arg.name) {
            return this.arg.name
        }

        if (this.paramStepPart.name) {
            return this.paramStepPart.name
        }

        if (this.arg.path && this.arg.path.fileName) {
            return this.arg.path.fileName;
        }

        return "param";
    }

    getArgumentValue(): string {
        if (this.arg.name) {
            return this.arg.name
        }

        if (this.arg.path && this.arg.path.fileName) {
            return this.arg.path.fileName;
        }

        if (this.arg.content instanceof BasicResource) {
            if(typeof this.arg.content.content === "string" || typeof this.arg.content.content === "number" ) {
                return this.arg.content.content;
            }
        }
        return "";
    }
}
