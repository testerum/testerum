export class RunnerPropertiesParser {

    private static readonly PROPERTIES_SPLITTER    = /(?<!\\),/g;
    private static readonly PROPERTY_PAIR_SPLITTER = /(?<!\\)=/g;

    public static parse(propertiesString: string): {[key: string]: string} {
        if (!propertiesString || propertiesString === "") {
            return {};
        }

        const result = {};

        const propertyPairs: Array<string> = propertiesString.split(this.PROPERTIES_SPLITTER);
        for (const propertyPair of propertyPairs) {
            const propertyPairParts: Array<string> = propertyPair.split(this.PROPERTY_PAIR_SPLITTER);

            if (propertyPairParts.length == 1) {
                const key = this.unescape(propertyPairParts[0]);

                result[key] = ""
            } else if (propertyPairParts.length == 2) {
                const key = this.unescape(propertyPairParts[0]);
                const value = this.unescape(propertyPairParts[1]);

                result[key] = value
            } else {
                throw Error(
                    `[${propertiesString}] has invalid format" +
                    ": the pair [${propertyPair}] should contain an '=' character that separates the key and the value`
                )
            }
        }

        return result;
    }

    private static unescape(text: string): string {
        return text.replace("\\=", "=")
            .replace("\\,", ",")
    }

}
