import {Enum} from "../../../model/enums/enum.interface";

export class ManualTestStepStatus extends Enum {

    public static NOT_EXECUTED = new ManualTestStepStatus("NOT_EXECUTED");
    public static PASSED = new ManualTestStepStatus("PASSED");
    public static FAILED = new ManualTestStepStatus("FAILED");

    static readonly enums: Array<ManualTestStepStatus> = [
        ManualTestStepStatus.NOT_EXECUTED,
        ManualTestStepStatus.PASSED,
        ManualTestStepStatus.FAILED,
    ];

    private constructor(httpMethodAsString: string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ManualTestStepStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
