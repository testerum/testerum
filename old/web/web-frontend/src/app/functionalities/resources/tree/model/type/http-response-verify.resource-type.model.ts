import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class HttpResponseVerifyResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/HTTP/Response Verify");
    readonly fileExtension: string = "http.response.verify.yaml";

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "nf nf-fa-reply flip";
    resourceUrl: string = "/resources/http/response_verify/";
    createSubResourceUrl:any[] = ["/resources/http/response/create", {"path": this.rootFilePath}];

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!HttpResponseVerifyResourceType.instanceForRoot) {
            this.instanceForRoot = new HttpResponseVerifyResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new HttpResponseVerifyResourceType()
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return HttpResponseVerifyResourceType.getInstanceForChildren();
    }
}
