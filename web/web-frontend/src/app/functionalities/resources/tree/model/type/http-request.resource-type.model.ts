
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class HttpRequestResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/HTTP/Request");
    readonly fileExtension: string = "http.request.json";

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "fa fa-share";
    resourceUrl: string = "/automated/resources/http/request/"; //TODO Ionut: after INLINE check if is still need it
    createSubResourceUrl:any[] = ["/automated/resources/http/request/create", {"path": this.rootFilePath}]; //TODO Ionut: after INLINE check if is still need it

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!HttpRequestResourceType.instanceForRoot) {
            this.instanceForRoot = new HttpRequestResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new HttpRequestResourceType();
    }

    getCreateSubResourceUrl(path: Path): any[] { //TODO Ionut: after INLINE check if is still need it
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return HttpRequestResourceType.getInstanceForChildren();
    }
}
