import {Enum} from "../../../model/enums/enum.interface";

export class OldManualTestStatus extends Enum {

    public static NOT_EXECUTED = new OldManualTestStatus("NOT_EXECUTED");
    public static IN_PROGRESS = new OldManualTestStatus("IN_PROGRESS");
    public static PASSED = new OldManualTestStatus("PASSED");
    public static FAILED = new OldManualTestStatus("FAILED");
    public static BLOCKED = new OldManualTestStatus("BLOCKED");
    public static NOT_APPLICABLE = new OldManualTestStatus("NOT_APPLICABLE");

    static readonly enums: Array<OldManualTestStatus> = [
        OldManualTestStatus.NOT_EXECUTED,
        OldManualTestStatus.IN_PROGRESS,
        OldManualTestStatus.PASSED,
        OldManualTestStatus.FAILED,
        OldManualTestStatus.BLOCKED,
        OldManualTestStatus.NOT_APPLICABLE,
    ];

    private constructor(httpMethodAsString:string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of OldManualTestStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
