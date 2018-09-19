import {IdUtils} from "../../utils/id.util";
import {TreeNodeModel} from "../infrastructure/tree-node.model";
import {Path} from "../infrastructure/path/path.model";
import {JsonUtil} from "../../utils/json.util";
import {Attachment} from "../file/attachment.model";
import {Serializable} from "../infrastructure/serializable.model";

export class Feature implements Serializable<Feature>, TreeNodeModel {

    id: string = IdUtils.getTemporaryId();
    path: Path;
    oldPath: Path;
    name: string;
    description: string;
    tags: Array<string> = [];
    attachments: Array<Attachment> = [];

    deserialize(input: Object): Feature {
        this.id = input['id'];
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.name = input['name'];
        this.description = input['description'];
        this.tags = input['tags'] || [];

        this.attachments = [];
        for (let attachment of (input['attachments'] || [])) {
            this.attachments.push(new Attachment().deserialize(attachment));
        }

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"id":' + JsonUtil.stringify(this.id) +
            ',"path":' + JsonUtil.serializeSerializable(this.path) +
            ',"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) +
            ',"name":' + JsonUtil.stringify(this.name) +
            ',"description":' + JsonUtil.stringify(this.description) +
            ',"tags":' + JsonUtil.stringify(this.tags) +
            ',"attachments":'+JsonUtil.serializeArrayOfSerializable(this.attachments)+
            '}'
    }
}
