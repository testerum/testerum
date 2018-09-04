import {Enum} from "../../../model/enums/enum.interface";

export class OldManualTestStepStatus extends Enum {

    public static NOT_EXECUTED = new OldManualTestStepStatus("NOT_EXECUTED");
    public static PASSED = new OldManualTestStepStatus("PASSED");
    public static FAILED = new OldManualTestStepStatus("FAILED");

    static readonly enums: Array<OldManualTestStepStatus> = [
        OldManualTestStepStatus.NOT_EXECUTED,
        OldManualTestStepStatus.PASSED,
        OldManualTestStepStatus.FAILED,
    ];

    private constructor(httpMethodAsString: string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of OldManualTestStepStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
