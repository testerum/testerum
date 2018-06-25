import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreePathNode} from "../model/path/json-tree-path-node.model";
import {JsonTreePathContainer} from "../model/path/json-tree-path-container.model";
import {JsonTreeNode} from "../model/json-tree-node.model";
import {StringUtils} from "../../../../utils/string-utils.util";
import {JsonTreeModel} from "../model/json-tree.model";
import {ObjectUtil} from "../../../../utils/object.util";
import {ArrayUtil} from "../../../../utils/array.util";
import {JsonTreeContainer} from "../model/json-tree-container.model";

export class JsonTreePathUtil {

    static getParentContainer(rootContainer: JsonTreeContainer, path: Path): JsonTreePathContainer {
        let pathAsString = path.toString();
        let lastIndexOfSlash = pathAsString.lastIndexOf("/");
        if(lastIndexOfSlash < 1) {
            return rootContainer as JsonTreePathContainer;
        }

        let parentPathAsString = StringUtils.substringBeforeLast(pathAsString, "/");
        let parentPath = Path.createInstance(parentPathAsString);

        return this.getNode(rootContainer, parentPath) as JsonTreePathContainer;
    }

    static getNode(rootContainer: JsonTreeContainer, pathToFind: Path): JsonTreePathNode {
        let pathDirectories = pathToFind.directories;

        if(pathToFind.directories.length == 0 && pathToFind.fileName == null) {
            return rootContainer as JsonTreePathContainer;
        }

        let rootChildren: Array<JsonTreeNode> = rootContainer.getChildren();
        for (let index = 0; index < pathDirectories.length; index++) {
            let dir = pathDirectories[index];
            let jsonNode: JsonTreePathContainer = JsonTreePathUtil.findNode(dir, rootChildren);

            if(jsonNode == null) {
                return null;
            }

            if(index == pathDirectories.length - 1 && !pathToFind.fileName) {
                return jsonNode;
            }

            rootChildren = jsonNode.getChildren()
        }

        return this.findNodeTypeLeaf(pathToFind.fileName, pathToFind.fileExtension, rootChildren);
    }

    public static findNode(nodeName: string, nodes: Array<JsonTreeNode>): JsonTreePathContainer  {
        for (let node of nodes) {
            let jsonTreePathContainer = node as JsonTreePathContainer;
            if(jsonTreePathContainer.name == nodeName) {
                return jsonTreePathContainer;
            }
        }
        return null;
    }

    private static findNodeTypeLeaf(fileName: string,
                                    fileExtension: string,
                                    nodes: Array<JsonTreeNode>): JsonTreePathContainer  {
        for (let node of nodes) {
            let jsonTreePathContainer = node as JsonTreePathContainer;
            if(jsonTreePathContainer.path.fileName == fileName && jsonTreePathContainer.path.fileExtension == fileExtension) {
                return jsonTreePathContainer;
            }
        }
        return null;
    }

    static copy(root: JsonTreeModel, pathToCopy: Path, destinationPath: Path) {

        let rootNode:JsonTreeContainer = (root.children[0] as JsonTreeContainer);

        let oldParent:JsonTreePathContainer = JsonTreePathUtil.getParentContainer(rootNode, pathToCopy);
        let nodeToCopy:JsonTreePathNode = JsonTreePathUtil.getNode(rootNode, pathToCopy);
        let newParent:JsonTreePathContainer = JsonTreePathUtil.getNode(rootNode, destinationPath) as JsonTreePathContainer;

        JsonTreePathUtil.resolvePathsOfCopyedNode(nodeToCopy, newParent);

        ArrayUtil.removeElementFromArray(oldParent.getChildren(), nodeToCopy);
        newParent.getChildren().push(nodeToCopy);
    }

    private static resolvePathsOfCopyedNode(nodeToCopy: JsonTreePathNode, newParent: JsonTreePathContainer) {
        if(nodeToCopy.isContainer()) {
            let directories = ArrayUtil.copyArray(newParent.path.directories);
            directories.push(nodeToCopy.path.directories[nodeToCopy.path.directories.length-1]);

            nodeToCopy.path = new Path(
                directories,
                nodeToCopy.path.fileName,
                nodeToCopy.path.fileExtension
            );
        } else {
            nodeToCopy.path = new Path(
                newParent.path.directories,
                nodeToCopy.path.fileName,
                nodeToCopy.path.fileExtension
            );
        }

        if(nodeToCopy.isContainer()) {
            let nodeToCopyAsContainer: JsonTreePathContainer = nodeToCopy as JsonTreePathContainer;
            for (let copyChildNode of nodeToCopyAsContainer.getChildren()) {
                JsonTreePathUtil.resolvePathsOfCopyedNode(copyChildNode as JsonTreePathNode, nodeToCopyAsContainer)
            }
        }
    }

    static isChildOf(expectedParentNode: JsonTreePathNode, expectedChildNode: JsonTreePathNode): boolean {
        if(!expectedParentNode.isContainer()) {
            return false;
        }

        let expectedParentContainer = expectedParentNode as JsonTreePathContainer;

        for (let child of expectedParentContainer.getChildren()) {
            if(child === expectedChildNode) {
                return true;
            }

            let isChildOfChild = JsonTreePathUtil.isChildOf(child as JsonTreePathNode, expectedChildNode);
            if(isChildOfChild) {
                return true;
            }
        }

        return false;
    }

    static isDirectChildOf(expectedParentNode: JsonTreePathNode, expectedChildNode: JsonTreePathNode): boolean {
        if(!expectedParentNode.isContainer()) {
            return false;
        }

        let expectedParentContainer = expectedParentNode as JsonTreePathContainer;

        for (let child of expectedParentContainer.getChildren()) {
            if(child === expectedChildNode) {
                return true;
            }
        }

        return false;
    }
}
