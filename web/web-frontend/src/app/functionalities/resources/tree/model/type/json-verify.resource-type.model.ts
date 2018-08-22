
import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class JsonVerifyResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/JSON Verify");
    readonly fileExtension: string = "verify.json";

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "nf nf-mdi-json";
    resourceUrl: string = "/resources/json_verify/";
    createSubResourceUrl:any[] = ["/resources/json_verify/create", {"path": this.rootFilePath}];

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!JsonVerifyResourceType.instanceForRoot) {
            this.instanceForRoot = new JsonVerifyResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new JsonVerifyResourceType();
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return JsonVerifyResourceType.getInstanceForChildren();
    }
}
