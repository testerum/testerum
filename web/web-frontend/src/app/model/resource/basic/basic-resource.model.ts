import {Resource} from "../resource.model";
import {StringUtils} from "../../../utils/string-utils.util";

export class BasicResource implements Resource<BasicResource> {

    static SMALL_TEXT_LENGTH: number = 60;
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
        return this.content ? this.content : "";
    }

    clone(): BasicResource {
        let objectAsJson = this.serialize() ? "" + this.serialize() : this.serialize();
        return new BasicResource().deserialize(objectAsJson);
    }

    createInstance(): BasicResource {
        return new BasicResource();
    }

    isSmallText(): boolean {
        if (!this.content) {
            return true;
        }

        if (this.content.length > BasicResource.SMALL_TEXT_LENGTH || StringUtils.isMultilineString(this.content)) {
            return false;
        }
        return true;
    }
}
