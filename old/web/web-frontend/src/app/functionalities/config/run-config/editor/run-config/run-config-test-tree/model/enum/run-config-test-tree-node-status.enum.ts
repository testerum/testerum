export class RunConfigTestTreeNodeStatusEnum {

    public static SELECTED = new RunConfigTestTreeNodeStatusEnum("NOT_EXECUTED");
    public static PARTIAL_SELECTED = new RunConfigTestTreeNodeStatusEnum("PARTIAL_SELECTED");
    public static NOT_SELECTED = new RunConfigTestTreeNodeStatusEnum("NOT_SELECTED");

    static readonly enums: Array<RunConfigTestTreeNodeStatusEnum> = [
        RunConfigTestTreeNodeStatusEnum.SELECTED,
        RunConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED,
        RunConfigTestTreeNodeStatusEnum.NOT_SELECTED,
    ];

    enumAsString: string;
    private constructor(enumAsString: string) {
        this.enumAsString = enumAsString;
    }

    public static fromString(enumAsString: string) {
        for (let enumValue of RunConfigTestTreeNodeStatusEnum.enums) {
            if (enumValue.enumAsString == enumAsString) {
                return enumValue;
            }
        }

        throw Error("Enum [" + enumAsString + "] is not defined")
    }
}
