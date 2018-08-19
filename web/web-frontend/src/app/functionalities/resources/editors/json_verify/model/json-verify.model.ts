import {Resource} from "../../../../../model/resource/resource.model";
import {JsonUtil} from "../../../../../utils/json.util";

export class JsonVerify implements Resource<JsonVerify> {

    jsonAsString: string;

    createInstance(): JsonVerify {
        return new JsonVerify();
    }

    clone(): JsonVerify {
        let objectAsJson = JSON.parse(this.serialize());
        return new JsonVerify().deserialize(objectAsJson);
    }

    isEmpty(): boolean {
        if (this.jsonAsString) return false;
        return true;
    }

    reset(): void {
        this.jsonAsString = null;
    }

    deserialize(input: Object): JsonVerify {
        this.jsonAsString = input as string;
        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.jsonAsString);
    }
}
