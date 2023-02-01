
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class HttpResourceType implements ResourceType {

   readonly rootFilePath: Path = Path.createInstance("resources/HTTP");
   readonly fileExtension: string = null;

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "fas fa-globe";
    resourceUrl: string;
    createSubResourceUrl:any[] = null;

    canHaveChildrenContainers: boolean = false;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static readonly instanceForRoot = new HttpResourceType();

    static getInstanceForRoot(): ResourceType {
        this.instanceForRoot.canBeEdited = false;
        this.instanceForRoot.canBeDeleted = false;
        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return this.instanceForRoot;
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return null;
    }

    getResourceTypeForChildren(): ResourceType {
        return HttpResourceType.getInstanceForChildren();
    }
}
