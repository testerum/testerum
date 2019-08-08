import {Enum} from "../../../enums/enum.interface";

export class ScenarioParamType extends Enum {

    public static TEXT = new ScenarioParamType("TEXT");
    public static JSON = new ScenarioParamType("JSON");

    static readonly enums: Array<ScenarioParamType> = [
        ScenarioParamType.TEXT,
        ScenarioParamType.JSON
    ];

    private constructor(httpMethodAsString: string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ScenarioParamType.enums) {
            if (enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum [" + methodAsString + "] is not defined")
    }
}
