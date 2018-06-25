import {Enum} from "../../../model/enums/enum.interface";

export class ManualTestStatus extends Enum {

    public static NOT_EXECUTED = new ManualTestStatus("NOT_EXECUTED");
    public static IN_PROGRESS = new ManualTestStatus("IN_PROGRESS");
    public static PASSED = new ManualTestStatus("PASSED");
    public static FAILED = new ManualTestStatus("FAILED");
    public static BLOCKED = new ManualTestStatus("BLOCKED");
    public static NOT_APPLICABLE = new ManualTestStatus("NOT_APPLICABLE");

    static readonly enums: Array<ManualTestStatus> = [
        ManualTestStatus.NOT_EXECUTED,
        ManualTestStatus.IN_PROGRESS,
        ManualTestStatus.PASSED,
        ManualTestStatus.FAILED,
        ManualTestStatus.BLOCKED,
        ManualTestStatus.NOT_APPLICABLE,
    ];

    private constructor(httpMethodAsString:string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ManualTestStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
