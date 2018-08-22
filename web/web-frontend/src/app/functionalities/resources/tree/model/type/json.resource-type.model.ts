import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class JsonResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/JSON");
    readonly fileExtension: string = "json";

    name: string = this.rootFilePath.getLastPathPart();
    iconClass: string = null;
    resourceUrl: string = "/resources/json";
    createSubResourceUrl: any[] = ["/resources/json/create", {"path": this.rootFilePath}];

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!JsonResourceType.instanceForRoot) {
            this.instanceForRoot = new JsonResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new JsonResourceType();
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return JsonResourceType.getInstanceForChildren();
    }
}
