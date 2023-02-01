
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class HttpMockResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/HTTP/Mock");
    readonly fileExtension: string;

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "fas fa-retweet";
    resourceUrl: string;
    createSubResourceUrl:any[] = null;

    canHaveChildrenContainers: boolean = false;
    canBeDeleted: boolean = false;
    canBeEdited: boolean = false;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!HttpMockResourceType.instanceForRoot) {
            this.instanceForRoot = new HttpMockResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return this.instanceForRoot;
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return null;
    }

    getResourceTypeForChildren(): ResourceType {
        return HttpMockResourceType.getInstanceForChildren();
    }
}
