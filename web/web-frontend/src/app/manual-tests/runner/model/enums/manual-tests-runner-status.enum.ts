
import {Enum} from "../../../../model/enums/enum.interface";

export class ManualTestsRunnerStatus extends Enum {

    public static IN_EXECUTION = new ManualTestsRunnerStatus("IN_EXECUTION");
    public static FINISHED = new ManualTestsRunnerStatus("FINISHED");

    static readonly enums: Array<ManualTestsRunnerStatus> = [
        ManualTestsRunnerStatus.IN_EXECUTION,
        ManualTestsRunnerStatus.FINISHED,
    ];

    private constructor(httpMethodAsString: string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ManualTestsRunnerStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
