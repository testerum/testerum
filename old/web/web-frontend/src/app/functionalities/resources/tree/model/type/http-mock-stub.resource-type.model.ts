import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class HttpMockStubResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/HTTP/Mock/Stub");
    readonly fileExtension: string = "http.stub.yaml";

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "fas fa-retweet";
    resourceUrl: string = "/resources/http/mock/stub/";
    createSubResourceUrl:any[] = ["/resources/http/mock/stub/create", {"path": this.rootFilePath}];

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!HttpMockStubResourceType.instanceForRoot) {
            this.instanceForRoot = new HttpMockStubResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new HttpMockStubResourceType()
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return HttpMockStubResourceType.getInstanceForChildren();
    }
}
