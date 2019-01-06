export class JsonUtils {

    static parseJson(jsonAsString: string): {[key: string]: string} {
        try {
            return JSON.parse(process.argv[3]);
        } catch (e) {
            console.error(`failed to parse as JSON [${process.argv[3]}]`, e);
            throw e
        }
    }

}
