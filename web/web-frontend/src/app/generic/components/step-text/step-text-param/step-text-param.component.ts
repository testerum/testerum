import {Component, Input, OnInit} from '@angular/core';
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {Arg} from "../../../../model/arg/arg.model";
import {VariableHolder} from "../../../variable/variable-holder.model";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {BasicResource} from "../../../../model/resource/basic/basic-resource.model";

@Component({
    moduleId: module.id,
    selector: 'step-text-param',
    templateUrl: 'step-text-param.component.html',
    styleUrls: ['step-text-param.component.css']
})

export class StepTextParamComponent {

    @Input() isStepCall: boolean;
    @Input() paramStepPart:ParamStepPatternPart;
    @Input() arg:Arg;
    @Input() variableHolder:VariableHolder;

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
