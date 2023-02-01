import {Enum} from "../../../enums/enum.interface";

export class ScenarioParamType extends Enum {

    public static TEXT = new ScenarioParamType("TEXT", "Text");
    public static JSON = new ScenarioParamType("JSON", "JSON");

    static readonly enums: Array<ScenarioParamType> = [
        ScenarioParamType.TEXT,
        ScenarioParamType.JSON
    ];

    private uiLabel: string;

    private constructor(serverForm: string, uiLabel: string) {
        super(serverForm);
        this.uiLabel = uiLabel;
    }

    public toUiLabel(): string {
        return this.uiLabel;
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
