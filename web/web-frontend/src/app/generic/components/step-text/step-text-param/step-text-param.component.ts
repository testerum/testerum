import {Component, Input} from '@angular/core';
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {Arg} from "../../../../model/arg/arg.model";
import {VariableHolder} from "../../../variable/variable-holder.model";
import {BasicResource} from "../../../../model/resource/basic/basic-resource.model";
import {StringUtils} from "../../../../utils/string-utils.util";
import {StepTextParamUtil} from "./util/step-text-param.util";

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
        return StepTextParamUtil.hasValue(this.arg)
    }

    getArgumentName(): string {
        return StepTextParamUtil.getArgumentName(this.arg, this.paramStepPart)
    }

    getArgumentValue(): string {
        return StepTextParamUtil.getArgumentValue(this.arg);
    }
}
