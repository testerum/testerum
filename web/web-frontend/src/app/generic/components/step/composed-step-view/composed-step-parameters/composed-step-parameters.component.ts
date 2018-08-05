import {Component, Input, OnInit} from '@angular/core';
import {StepPatternPart} from "../../../../../model/text/parts/step-pattern-part.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {BasicResourceComponent} from "../../../../../functionalities/resources/editors/basic/basic-resource.component";

@ Component({
    moduleId: module.id,
    selector: 'composed-step-parameters',
    templateUrl: 'composed-step-parameters.component.html',
    styleUrls: ['composed-step-parameters.component.scss']
})
export class ComposedStepParametersComponent implements OnInit {

    @Input() patternParts: Array<StepPatternPart>;
    @Input() isEditMode: boolean = false;

    ParamStepPatternPart = ParamStepPatternPart;

    ngOnInit(): void {
    }

    getStepParamTypesValues():Array<ResourceMapEnum> {
        let resourceMapEnums = ResourceMapEnum.ALL_PARAM_TYPES;

        let results: ResourceMapEnum[] = [];
        for (const resourceMapEnum of resourceMapEnums) {
            if (resourceMapEnum.resourceComponent == BasicResourceComponent) {
                results.push(resourceMapEnum);
            }
        }
        return results
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

    onValueChanged(uiType:string, paramPatternPart:ParamStepPatternPart):void {
        paramPatternPart.uiType = uiType;
        paramPatternPart.serverType = ResourceMapEnum.getResourceMapEnumByUiType(uiType).serverType;
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

    asParam(patternPart: StepPatternPart): ParamStepPatternPart {
        return patternPart as ParamStepPatternPart;
    }
}
