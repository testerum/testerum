
export class ScenarioParamTypeReportEnum {

    public static TEXT = new ScenarioParamTypeReportEnum("TEXT", "Text");
    public static JSON = new ScenarioParamTypeReportEnum("JSON", "JSON");

    static readonly enums: Array<ScenarioParamTypeReportEnum> = [
        ScenarioParamTypeReportEnum.TEXT,
        ScenarioParamTypeReportEnum.JSON
    ];

    readonly enumAsString: string;
    readonly uiLabel: string;

    private constructor(enumAsString: string, uiLabel: string) {
        this.enumAsString = enumAsString;
        this.uiLabel = uiLabel;
    }

    public toUiLabel(): string {
        return this.uiLabel;
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ScenarioParamTypeReportEnum.enums) {
            if (enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum [" + methodAsString + "] is not defined")
    }
}