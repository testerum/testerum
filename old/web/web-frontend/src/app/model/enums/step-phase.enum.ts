import {StringUtils} from "../../utils/string-utils.util";

export enum StepPhaseEnum {
    GIVEN,
    WHEN,
    THEN,
    AND
}

export class StepPhaseUtil {

    static toCamelCaseString(stepPhaseEnum: StepPhaseEnum): string {
        return StringUtils.toTitleCase(StepPhaseEnum[stepPhaseEnum].toString());
    }

    static toLowerCaseString(stepPhaseEnum: StepPhaseEnum): string {
        return StepPhaseEnum[stepPhaseEnum].toString().toLowerCase();
    }
}
