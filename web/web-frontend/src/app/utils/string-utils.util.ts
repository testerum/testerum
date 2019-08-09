
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

    static equalsIgnoreCase(str1: string, str2: string): boolean {
        if (str1 == null || str2 == null) {
            return str1 === str2;
        }
        return str1.toUpperCase() === str2.toUpperCase();
    }

    static isMultilineString(str: string): boolean {
        return str.indexOf("\n") >= 0;
    }

    static substringAfter(mainString: string, searchedPart: string): string {
        let index = mainString.indexOf(searchedPart);
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
    static substringAfterLast(mainString: string, searchedPart: string): string {
        let index = mainString.lastIndexOf(searchedPart);
        if(index < 0) {
            return null;
        }
        return mainString.substring(index+1);
    }

    static substringBefore(mainString: string, searchedPart: string): string {
        let index = mainString.indexOf(searchedPart);
        if(index < 0) {
            return null;
        }
        return mainString.substring(0, index);
    }

    static isString(value: any): boolean {
        return typeof value === 'string' || value instanceof String;
    }
}
