import {Resource} from "../../../../model/resource/resource.model";
import {StringUtils} from "../../../../utils/string-utils.util";

export class ObjectResourceModel implements Resource<ObjectResourceModel> {

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

    deserialize(input: string): ObjectResourceModel {
        this.content = input as string;
        return this;
    }

    serialize(): string {
        return this.content ? this.content : "";
    }

    clone(): ObjectResourceModel {
        let objectAsJson = this.serialize() ? "" + this.serialize() : this.serialize();
        return new ObjectResourceModel().deserialize(objectAsJson);
    }

    createInstance(): ObjectResourceModel {
        return new ObjectResourceModel();
    }
}
