import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {PathChooserContainerModel} from "../model/path-chooser-container.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {PathChooserNodeModel} from "../model/path-chooser-node.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";

export class MapPathChooserUtil {
    static mapPathsToPathChooserModel(paths: Array<Path>,
                                      treeModel:JsonTreeModel,
                                      resourceMapping: ResourceMapEnum,
                                      mapFiles: boolean,
                                      allowDirSelection: boolean): JsonTreeModel {

        treeModel.children.length = 0;

        let rootContainer = new PathChooserContainerModel(treeModel, Path.createInstanceOfEmptyPath(), allowDirSelection);
        rootContainer.name = resourceMapping? resourceMapping.uiName + " root": "root";
        rootContainer.isRootPackage = true;
        treeModel.children.push(rootContainer);

        for (let path of paths) {
            this.mapPathPartsToPathChooserModal(
                ArrayUtil.copyArray(path.directories),
                path.fileName,
                path.fileExtension,
                rootContainer,
                mapFiles,
                allowDirSelection
            );
        }

        return treeModel;
    }

    private static mapPathPartsToPathChooserModal(pathDirectories: Array<String>,
                                                  fileName: string,
                                                  fileExtension: string,
                                                  parentContainer: JsonTreeContainer,
                                                  mapFiles:boolean,
                                                  allowDirSelection: boolean): void {
        if (pathDirectories.length == 0) {
            if (mapFiles) {
                MapPathChooserUtil.mapPathFileNameToPathChooserNode(fileName, fileExtension, parentContainer);
            }
            return;
        }

        let rootDirectory = pathDirectories[0];
        let remainingDirectories = ArrayUtil.copyArrayOfImmutableObjects(pathDirectories);
        remainingDirectories.splice(0, 1);

        let childPathChooserContainer: PathChooserContainerModel = MapPathChooserUtil.mapDirectoryToPathChooserContainer(
            rootDirectory,
            parentContainer,
            allowDirSelection
        );

        MapPathChooserUtil.mapPathPartsToPathChooserModal(
            remainingDirectories,
            fileName,
            fileExtension,
            childPathChooserContainer,
            mapFiles,
            allowDirSelection
        );
    }

    private static mapDirectoryToPathChooserContainer(directory: String,
                                                      parentContainer: JsonTreeContainer,
                                                      allowDirSelection: boolean): PathChooserContainerModel {
        let parentContainerPathDirectories = [];
        if (parentContainer instanceof PathChooserContainerModel) {
            parentContainerPathDirectories = parentContainer.path.directories;
        }
        let newNodePathDirectories = ArrayUtil.copyArrayOfImmutableObjects(parentContainerPathDirectories);
        newNodePathDirectories.push(directory);

        let directoryPath = new Path(newNodePathDirectories, null, null);

        let pathChooserConainer = new PathChooserContainerModel(parentContainer, directoryPath, allowDirSelection);
        parentContainer.getChildren().push(pathChooserConainer);
        this.sortChildren(parentContainer);

        return pathChooserConainer;
    }

    private static mapPathFileNameToPathChooserNode(fileName: string, fileExtension: string, parentContainer: JsonTreeContainer) {
        let parentContainerPathDirectories = [];
        if (parentContainer instanceof PathChooserContainerModel) {
            parentContainerPathDirectories = parentContainer.path.directories;
        }

        let filePath = new Path(
            parentContainerPathDirectories,
            fileName,
            fileExtension
        );
        let filePathChooserNodeModel = new PathChooserNodeModel(parentContainer, filePath);

        parentContainer.getChildren().push(filePathChooserNodeModel);
        this.sortChildren(parentContainer)
    }


    public static sortChildren(parentContainer: JsonTreeContainer) {
        parentContainer.getChildren().sort((left: PathChooserNodeModel, right: PathChooserNodeModel) => {
            if(left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if(! left.isContainer() && right.isContainer()) {
                return 1;
            }

            if (left.name.toUpperCase() < right.name.toUpperCase()) return -1;
            if (left.name.toUpperCase() > right.name.toUpperCase()) return 1;

            return 0;
        });
    }
}
