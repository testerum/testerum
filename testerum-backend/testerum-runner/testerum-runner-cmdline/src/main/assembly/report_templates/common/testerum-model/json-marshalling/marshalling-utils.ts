export class MarshallingUtils {

    static parseUtcToLocalDateTime(input: string): Date {
        input = input + "Z";

        return new Date(input);
    }

    static parseLocalDate(input: string): Date {
        return new Date(input);
    }

    static parseEnum<E>(enumText: string, enumClass: { [p: number]: string }): keyof E {
        return enumClass[enumText];
    }

    static parseListOfStrings<T>(input: Array<any>): Array<string> {
        if (!input) {
            return [];
        }

        const result: Array<string> = [];

        for (let item of (input || [])) {
            result.push(
                String(item)
            );
        }

        return result;
    }

    static parseList<T>(input: Array<any>,
                        parser: {parse(input: Object): T}): Array<T> {
        if (!input) {
            return [];
        }

        const result: Array<T> = [];

        for (let item of (input || [])) {
            result.push(
                parser.parse(item)
            );
        }

        return result;
    }

    static parsePolymorphically<T> (input: Object,
                                    parserMap: { [key:string]: {parse(input: Object): T} }): T {
        if (!input) {
            return null;
        }

        const type = input["@type"];
        const parser = parserMap[type];
        if (!parser) {
            throw Error(`cannot find parser for type [${type}]`);
        }

        return parser.parse(input);
    }

    static parseListPolymorphically<T> (input: Array<any>,
                                        typeMap: { [key:string]: {parse(input: Object): T} }): Array<T> {
        if (!input) {
            return [];
        }

        const result: Array<T> = [];

        for (let item of (input || [])) {
            result.push(
                this.parsePolymorphically(item, typeMap)
            );
        }

        return result;
    }

    static parseMapPolymorficaly<T>(input: Object,
                                    typeMap: { [key:string]: {parse(input: Object): T} }): Map<string, T> {
        if (!input) {
            return new Map();
        }

        const result: Map<string, T> = new Map();

        for (const key in input) {
            if (!input.hasOwnProperty(key)) {
                continue;
            }

            let value = input[key];

            result.set(key, this.parsePolymorphically(value, typeMap));
        }
        return result;
    }

    static parseMap<K, V>(input: Object,
                          parseKey: (Object) => K,
                          parseValue: (Object) => V): Map<K, V> {
        const result = new Map<K, V>();

        for (const key in input) {
            if (!input.hasOwnProperty(key)) {
                continue;
            }

            const parsedKey = parseKey(key);
            const parsedValue = parseValue(input[key]);

            result.set(parsedKey, parsedValue);
        }

        return result;
    }

}
