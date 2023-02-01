import {JsonUtil} from "../../utils/json.util";
import {Path} from "../infrastructure/path/path.model";
import {ObjectUtil} from "../../utils/object.util";
import {BasicResource} from "./basic/basic-resource.model";
import {Serializable} from "../infrastructure/serializable.model";

export class ResourceContext<T> implements Serializable<ResourceContext<T>> {

    path: Path;
    oldPath: Path;
    body: any;

    /**
     * This property is used to deserialize the resource body.
     * In case the resource body is not a JSON but a string you should not provide this parameter
     **/
    constructor(private bodyInstanceForDeserialization?: any) {
    }

    static createInstance<T>(path: Path, body?: any): ResourceContext<T> {
        let resource = new ResourceContext<T>(null);
        resource.path = path;
        resource.body = body;
        return resource
    }

    static createInstanceOfSavedResource<T>(path: Path, oldPath: Path, body: any): ResourceContext<T> {
        let resource = new ResourceContext<T>(null);
        resource.path = path;
        resource.oldPath = oldPath;
        resource.body = body;
        return resource
    }

    isCreateNewResource(): boolean {
        return this.path.fileExtension == null
    }

    isUpdate(): boolean {
        return !this.isCreateNewResource()
    }

    deserialize(input: Object): ResourceContext<T> {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        let bodyAsString = input["body"];
        if (this.bodyInstanceForDeserialization) {
            if (this.bodyInstanceForDeserialization instanceof BasicResource) {
                this.body = this.bodyInstanceForDeserialization.deserialize(bodyAsString);
            } else {
                let bodyAsJson = JsonUtil.parseJson(bodyAsString);
                this.body = this.bodyInstanceForDeserialization.deserialize(bodyAsJson);
            }
        } else {
            this.body = bodyAsString;
        }

        return this;
    }

    serialize() {
        let result = "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) + ',' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',';

        if (ObjectUtil.hasAMethodCalled(this.body, "serialize")) {
            result += '"body":' + JsonUtil.stringify(this.body.serialize());
        } else {
            result += '"body":' + JsonUtil.stringify(this.body);
        }
        result += '}';

        return result;
    }
}
