
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class BasicResourceType implements ResourceType {

   readonly rootFilePath: Path = null;
   readonly fileExtension: string = null;

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = null;
    resourceUrl: string;
    createSubResourceUrl:any[] = null;

    canHaveChildrenContainers: boolean = false;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static readonly instanceForRoot = null;

    static getInstanceForRoot(): ResourceType {
        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return this.instanceForRoot;
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return null;
    }

    getResourceTypeForChildren(): ResourceType {
        return BasicResourceType.getInstanceForChildren();
    }
}
