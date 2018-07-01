import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class HttpMockServerResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/HTTP/Mock/Server");
    readonly fileExtension: string = "http.mock.server.yaml";

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "fa fa-server";
    resourceUrl: string = "/automated/resources/http/mock/server/";
    createSubResourceUrl:any[] = ["/automated/resources/http/mock/server/create"];

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!HttpMockServerResourceType.instanceForRoot) {
            this.instanceForRoot = new HttpMockServerResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new HttpMockServerResourceType()
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return HttpMockServerResourceType.getInstanceForChildren();
    }
}
