import {Enum} from "../../../../../model/enums/enum.interface";

export class ManualTestPlanStatus extends Enum {

    public static IN_EXECUTION = new ManualTestPlanStatus("IN_EXECUTION");
    public static FINISHED = new ManualTestPlanStatus("FINISHED");

    static readonly enums: Array<ManualTestPlanStatus> = [
        ManualTestPlanStatus.IN_EXECUTION,
        ManualTestPlanStatus.FINISHED,
    ];

    private constructor(httpMethodAsString: string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ManualTestPlanStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
