
export class StringUtils {

    public static toCamelCase(str:string):string {
        if(!str) return str;
        return str.replace(/(?:^\w|[A-Z]|\b\w)/g, function(letter, index) {
            return index == 0 ? letter.toLowerCase() : letter.toUpperCase();
        }).replace(/\s+/g, '');
    }

    public static toTitleCase(str:string):string {
        str = str.toLowerCase();
        let fistLetter = str.charAt(0).toUpperCase();

        return fistLetter + str.substr(1, str.length);
    }


    static isEmpty(str: string): boolean {
        if(!str) {
            return true;
        }

        if(str.trim && str.trim() === "") {
            return true;
        }

        return false;
    }

    static isNotEmpty(str: string): boolean {
        return !StringUtils.isEmpty(str);
    }

    static substringAfter(mainString: string, searchedPart: string): string {
        let index = mainString.lastIndexOf(searchedPart);
        if(index < 0) {
            return null;
        }
        return mainString.substring(index + 1);
    }
    static substringBeforeLast(mainString: string, searchedPart: string): string {
        let index = mainString.lastIndexOf(searchedPart);
        if(index < 0) {
            return null;
        }
        return mainString.substring(0, index);
    }

    static substringBefore(mainString: string, searchedPart: string): string {
        let index = mainString.indexOf(searchedPart);
        if(index < 0) {
            return null;
        }
        return mainString.substring(0, index);
    }

    static string2ByteArray(str: string): number[] {
        let result = [];
        for (let i = 0; i < str.length; i++) {
            result.push(str.charCodeAt(i));
        }
        return result;
    }

    static byteArray2String(byteArray: number[]): string {
        return String.fromCharCode.apply(String, byteArray);
    }
}
