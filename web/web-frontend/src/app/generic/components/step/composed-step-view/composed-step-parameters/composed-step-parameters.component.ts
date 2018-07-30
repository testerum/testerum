import {Component, Input, OnInit} from '@angular/core';
import {StepPatternPart} from "../../../../../model/text/parts/step-pattern-part.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";

@ Component({
    moduleId: module.id,
    selector: 'composed-step-parameters',
    templateUrl: 'composed-step-parameters.component.html',
    styleUrls: ['composed-step-parameters.component.scss', '../../../../css/generic.scss']
})
export class ComposedStepParametersComponent implements OnInit {

    @Input() patternParts: Array<StepPatternPart>;
    @Input() isEditMode: boolean = false;

    ngOnInit(): void {
    }

    getStepParamTypesValues():Array<ResourceMapEnum> {
        return ResourceMapEnum.ALL_PARAM_TYPES
    }
    patternHasParameters(): boolean {
        for (let part of this.patternParts) {
            if (part instanceof ParamStepPatternPart) {
                return true;
            }
        }
        return false;
    }

    isParamPattern(stepPatternPart: StepPatternPart): boolean {
        if (stepPatternPart instanceof ParamStepPatternPart) {
            return true;
        }
        return false;
    }

    onValueChanged(value:string, paramPatternPart:ParamStepPatternPart):void {
        paramPatternPart.uiType = value;
    }

    setAllowedValues(value:string, paramPatternPart:ParamStepPatternPart): void {
        let splitedValues: string[] = value.split(",");
        splitedValues.forEach(value => value.trim());
        paramPatternPart.enumValues = splitedValues
    }

    isEnumType(serverType: string):boolean {
        return serverType == ResourceMapEnum.ENUM.serverType;
    }

    getParamUiName(serverType: string): string {
        return ResourceMapEnum.getUiNameByUiType(serverType)
    }

}
