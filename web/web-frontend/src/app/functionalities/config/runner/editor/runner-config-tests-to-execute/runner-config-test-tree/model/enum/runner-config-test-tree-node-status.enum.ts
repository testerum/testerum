export class RunnerConfigTestTreeNodeStatusEnum {

    public static SELECTED = new RunnerConfigTestTreeNodeStatusEnum("NOT_EXECUTED");
    public static PARTIAL_SELECTED = new RunnerConfigTestTreeNodeStatusEnum("PARTIAL_SELECTED");
    public static NOT_SELECTED = new RunnerConfigTestTreeNodeStatusEnum("NOT_SELECTED");

    static readonly enums: Array<RunnerConfigTestTreeNodeStatusEnum> = [
        RunnerConfigTestTreeNodeStatusEnum.SELECTED,
        RunnerConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED,
        RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED,
    ];

    enumAsString: string;
    private constructor(enumAsString: string) {
        this.enumAsString = enumAsString;
    }

    public static fromString(enumAsString: string) {
        for (let enumValue of RunnerConfigTestTreeNodeStatusEnum.enums) {
            if (enumValue.enumAsString == enumAsString) {
                return enumValue;
            }
        }

        throw Error("Enum [" + enumAsString + "] is not defined")
    }
}
