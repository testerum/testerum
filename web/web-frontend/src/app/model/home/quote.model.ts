import {Serializable} from "../infrastructure/serializable.model";
import {JsonUtil} from "../../utils/json.util";

export class Quote implements Serializable<Quote>  {

    text: string;
    author: string | null;

    deserialize(input: Object): Quote {
        this.text = input["text"];
        this.author = input["author"] || null;

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"text":' + JsonUtil.stringify(this.text) + ',' +
            '"author":' + JsonUtil.stringify(this.author) + ',' +
            '}';
    }

}
