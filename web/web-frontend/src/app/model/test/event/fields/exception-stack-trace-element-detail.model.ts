
export class ExceptionStackTraceElementDetail implements Serializable<ExceptionStackTraceElementDetail> {

    class:string;
    methodName:string;
    fileName:string;
    lineNumber:number;

    toString(): string {
        let result = this.class + "." + this.methodName;

        result += "{";

        if(this.nativeMethod()) {
            result += "Native Method";
        } else {
            if (!this.fileName) {
                result += this.fileName;

                if (this.lineNumber >= 0) {
                    result += ":" + this.lineNumber;
                }
            } else {
                result += "Unknown Source";
            }
        }

        result += "}";

        return result;
    }

    nativeMethod(): boolean {
        return this.lineNumber == -2;
    }

    deserialize(input: Object): ExceptionStackTraceElementDetail {
        this.class = input["class"];
        this.methodName = input["methodName"];
        this.fileName = input["fileName"];
        this.lineNumber = input["lineNumber"];

        return this;
    }

    serialize(): string {
        return "";
    }
}
