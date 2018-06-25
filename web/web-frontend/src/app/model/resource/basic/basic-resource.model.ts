import {Resource} from "../resource.model";
import {JsonUtil} from "../../../utils/json.util";
import {StringUtils} from "../../../utils/string-utils.util";

export class BasicResource implements Resource<BasicResource> {

    content: string;

    constructor() {
        this.reset()
    }

    reset(): void {
        this.content = undefined;
    }

    isEmpty(): boolean {
        return StringUtils.isEmpty(this.content);
    }

    deserialize(input: string): BasicResource {
        this.content = input as string;
        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.content);
    }

    clone(): BasicResource {
        let objectAsJson = JSON.parse(this.serialize());
        return new BasicResource().deserialize(objectAsJson);
    }

    createInstance(): BasicResource {
        return new BasicResource();
    }
}
