export class ObjectUtil {

    static hasAMethodCalled(obj: any, methodName: string): boolean {
        if (!obj) {
            return false;
        }
        return obj[methodName]
    }

    static isANumber(value: string): boolean {

        if(value == null || "" === value.trim()) {
            return false;
        }

        let number = Number(value);
        if (Number.isNaN(number)) {
            return false
        }
        return true;
    }

    static getAsNumber(value: string): number {
        return +value;
    }
}
