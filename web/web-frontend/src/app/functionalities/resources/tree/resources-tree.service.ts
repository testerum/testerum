import {Injectable} from '@angular/core';
import {ResourcesTreeContainer} from "./model/resources-tree-container.model";
import {HttpRequestResourceType} from "./model/type/http-request.resource-type.model";
import {RdbmsRootResourceType} from "./model/type/rdbms-root.resource-type.model";
import {RdbmsConnectionResourceType} from "./model/type/rdbms-connection.resource-type.model";
import {ResourcesTreeNode} from "./model/resources-tree-node.model";
import {RdbmsSqlResourceType} from "./model/type/rdbms-sql.resource-type.model";
import {ResourceType} from "./model/type/resource-type.model";
import {ResourceService} from "../../../service/resources/resource.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {RdbmsVerifyResourceType} from "./model/type/rdbms-verify.resource-type.model";
import {JsonVerifyResourceType} from "./model/type/json-verify.resource-type.model";
import {HttpResponseVerifyResourceType} from "./model/type/http-response-verify.resource-type.model";
import {HttpResourceType} from "./model/type/http.resource-type.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreePathUtil} from "../../../generic/components/json-tree/util/json-tree-path.util";
import {HttpMockStubResourceType} from "./model/type/http-mock-stub.resource-type.model";
import {HttpMockServerResourceType} from "./model/type/http-mock-server.resource-type.model";
import {HttpMockResourceType} from "./model/type/http-mock.resource-type.model";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {JsonResourceType} from "./model/type/json.resource-type.model";
import {JsonRootResourceType} from "./model/type/json-root.resource-type.model";

@Injectable()
export class ResourcesTreeService {

    private root: JsonTreeModel = new JsonTreeModel();
    private currentSelectedPath: Path;

    private resourceContainers: Array<ResourcesTreeContainer> = [
        new ResourcesTreeContainer(null, HttpRequestResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, HttpResponseVerifyResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, HttpMockServerResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, HttpMockStubResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, JsonResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, JsonVerifyResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, RdbmsConnectionResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, RdbmsSqlResourceType.getInstanceForRoot()),
        new ResourcesTreeContainer(null, RdbmsVerifyResourceType.getInstanceForRoot())
    ];

    constructor(private resourceService: ResourceService,
                private jsonTreeService:JsonTreeService) {
    }

    initializeResourceTreeFromServer(selectedPath: Path) {
        this.currentSelectedPath = selectedPath;
        this.root.children.length = 0;

        this.root.children.push(
            this.getHttpResourcesRoot(),
            this.getJsonResourcesRoot(),
            this.getRdbmsResourcesRoot()
        );
        this.fixTreeAfterNodesLoad();
    }

    getTreeRoot(): JsonTreeModel {
        return this.root;
    }

    private getHttpResourcesRoot(): ResourcesTreeContainer {
        this.initializeResources(HttpRequestResourceType.getInstanceForRoot(), HttpRequestResourceType.getInstanceForChildren());
        this.initializeResources(HttpResponseVerifyResourceType.getInstanceForRoot(), HttpResponseVerifyResourceType.getInstanceForChildren());


        let httpResource = new ResourcesTreeContainer(null, HttpResourceType.getInstanceForRoot());
        httpResource.jsonTreeNodeState.showChildren = true;

        httpResource.children.push(
            this.getResourceContainerByResourceType(HttpRequestResourceType.getInstanceForRoot()),
            this.getResourceContainerByResourceType(HttpResponseVerifyResourceType.getInstanceForRoot()),
            this.getHttpMockResourcesRoot()
        );
        return httpResource;
    }

    private getHttpMockResourcesRoot() {
        this.initializeResources(HttpMockServerResourceType.getInstanceForRoot(), HttpMockServerResourceType.getInstanceForChildren());
        this.initializeResources(HttpMockStubResourceType.getInstanceForRoot(), HttpMockStubResourceType.getInstanceForChildren());

        let httpMockResource = new ResourcesTreeContainer(null, HttpMockResourceType.getInstanceForRoot());
        httpMockResource.jsonTreeNodeState.showChildren = true;

        httpMockResource.children.push(
            this.getResourceContainerByResourceType(HttpMockServerResourceType.getInstanceForRoot()),
            this.getResourceContainerByResourceType(HttpMockStubResourceType.getInstanceForRoot())
        );
        return httpMockResource;
    }

    private getJsonResourcesRoot(): ResourcesTreeContainer {
        this.initializeResources(JsonResourceType.getInstanceForRoot(), JsonResourceType.getInstanceForChildren());
        this.initializeResources(JsonVerifyResourceType.getInstanceForRoot(), JsonVerifyResourceType.getInstanceForChildren());

        let jsonRoot = new ResourcesTreeContainer(null, JsonRootResourceType.getInstanceForRoot());
        jsonRoot.jsonTreeNodeState.showChildren = true;

        jsonRoot.children.push(
            this.getResourceContainerByResourceType(JsonResourceType.getInstanceForRoot()),
            this.getResourceContainerByResourceType(JsonVerifyResourceType.getInstanceForRoot()),
        );
        return jsonRoot;
    }

    private getRdbmsResourcesRoot(): ResourcesTreeContainer {
        this.initializeResources(RdbmsConnectionResourceType.getInstanceForRoot(), RdbmsConnectionResourceType.getInstanceForChildren());
        this.initializeResources(RdbmsSqlResourceType.getInstanceForRoot(), RdbmsSqlResourceType.getInstanceForChildren());
        this.initializeResources(RdbmsVerifyResourceType.getInstanceForRoot(), RdbmsVerifyResourceType.getInstanceForChildren());

        let dbRoot = new ResourcesTreeContainer(null, RdbmsRootResourceType.getInstanceForRoot());
        dbRoot.jsonTreeNodeState.showChildren = true;

        dbRoot.children.push(
            this.getResourceContainerByResourceType(RdbmsConnectionResourceType.getInstanceForRoot()),
            this.getResourceContainerByResourceType(RdbmsSqlResourceType.getInstanceForRoot()),
            this.getResourceContainerByResourceType(RdbmsVerifyResourceType.getInstanceForRoot()),
        );
        return dbRoot;
    }

    initializeResources(rootResourceType: ResourceType, childrenResourceType: ResourceType) {
        let container: ResourcesTreeContainer = this.getResourceContainerByResourceType(rootResourceType);
        container.children.length = 0;
        this.resourceService.getResourcePaths(rootResourceType.fileExtension).subscribe(
            paths => paths.forEach(
                path => {
                    this.addPathToRoot(container, path, childrenResourceType);
                    this.fixTreeAfterNodesLoad()
                }
            )
        )
    }

    private fixTreeAfterNodesLoad() {
        this.sort();

        this.fixParentFieldForEachNode(this.root);

        let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.root, this.currentSelectedPath);
        this.jsonTreeService.setSelectedNode(selectedNode);
    }

    private getResourceContainerByResourceType(resourceType: ResourceType): ResourcesTreeContainer {
        for (let container of this.resourceContainers) {
            if (container.resourceType == resourceType) {
                return container;
            }
        }

        throw new Error("no container found for this ResourceType");
    }

    private addPathToRoot(rootContainer: ResourcesTreeContainer, path: Path, resourceType: ResourceType) {
        let container: ResourcesTreeContainer = rootContainer;

        for (let i = 0; i < path.directories.length; i++) {
            let directory = path.directories[i];
            let childContainer: ResourcesTreeContainer = container.getChildContainerByName(directory);
            if (!childContainer) {
                childContainer = this.createChildContainer(container, directory, resourceType);
            }
            container = childContainer;
        }

        this.createChildNode(container, path, resourceType);
    }

    private createChildContainer(parentContainer: ResourcesTreeContainer, name: string, resourceType: ResourceType): ResourcesTreeContainer {
        let childContainer = new ResourcesTreeContainer(
            parentContainer,
            resourceType,
            parentContainer.path.addDirectory(name)
        );
        parentContainer.children.push(childContainer);

        return childContainer;
    }

    private createChildNode(container: ResourcesTreeContainer, path: Path, resourceType: ResourceType): ResourcesTreeNode {
        let resourcesTreeNode = new ResourcesTreeNode(
            container,
            resourceType,
            path,
        );
        container.children.push(resourcesTreeNode);
        return resourcesTreeNode
    }

    sort(): void {
        this.sortChildren(this.root.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: ResourcesTreeNode, right: ResourcesTreeNode) => {
            if (left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if (!left.isContainer() && right.isContainer()) {
                return 1;
            }

            if (left.name.toUpperCase() < right.name.toUpperCase()) return -1;
            if (left.name.toUpperCase() > right.name.toUpperCase()) return 1;

            return 0;
        });

        children.forEach( it => {
            if(it.isContainer()) {
                this.sortChildren((it as ResourcesTreeContainer).children)
            }
        })
    }

    copy(pathToCopy: Path, destinationPath: Path) {
        JsonTreePathUtil.copy(this.root, pathToCopy, destinationPath);

        let newParent:ResourcesTreeContainer = JsonTreePathUtil.getNode(this.root, destinationPath) as ResourcesTreeContainer;
        newParent.sort();
    }

    private fixParentFieldForEachNode(tree: JsonTreeModel) {
        for (const child of tree.getChildren()) {
            (child as ResourcesTreeNode).parentContainer = tree;
            if (child.isContainer()) {
                this.fixParentFieldForChild(child as ResourcesTreeContainer)
            }
        }
    }

    private fixParentFieldForChild(parent: ResourcesTreeContainer) {
        for (const child of parent.getChildren()) {
            child.parentContainer = parent;
            if (child.isContainer()) {
                this.fixParentFieldForChild((child as ResourcesTreeContainer))
            }
        }
    }

    selectNodeAtPath(path: Path) {
        if (this.root) {
            let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.root, path);
            this.jsonTreeService.setSelectedNode(selectedNode);
        }
    }
}
