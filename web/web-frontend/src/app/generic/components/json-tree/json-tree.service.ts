
import {Injectable,EventEmitter} from "@angular/core";
import {JsonTreeNode} from "./model/json-tree-node.model";
import {JsonTreeNodeEventModel} from "./event/selected-json-tree-node-event.model";
import {JsonTreeContainerEditor} from "./container-editor/json-tree-container-editor.component";
import {JsonTreeContainerEditorEvent} from "./container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../model/infrastructure/path/path.model";
import {BsModalService} from "ngx-bootstrap";
import {JsonTreeContainer} from "./model/json-tree-container.model";
import {FeatureTreeContainerModel} from "../../../functionalities/features/features-tree/model/feature-tree-container.model";
@Injectable()
export class JsonTreeService {

    selectedNode: JsonTreeNode;
    selectedNodeEmitter: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();
    expendedNodeEmitter: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    constructor(private modalService: BsModalService) {
        this.selectedNodeEmitter.subscribe((item: JsonTreeNodeEventModel) => this.selectedNode = item.treeNode);
    }

    setSelectedNode(selectedStep: JsonTreeNode) {
        this.selectedNodeEmitter.emit(
            new JsonTreeNodeEventModel(selectedStep)
        );

        this.selectedNode = selectedStep;
    }

    triggerExpendedNodeEvent(jsonTreeContainer:JsonTreeContainer) {
        this.expendedNodeEmitter.emit(
            new JsonTreeNodeEventModel(jsonTreeContainer)
        )
    }

    triggerCreateContainerAction(siblingsNodeNames: Array<string>): EventEmitter<JsonTreeContainerEditorEvent> {
        let bsModalRef = this.modalService.show(JsonTreeContainerEditor);
        return bsModalRef.content.showToCreateContainer(siblingsNodeNames);
    }
    triggerUpdateContainerAction(name: string, siblingsNodeNames: Array<string>): EventEmitter<JsonTreeContainerEditorEvent> {
        let bsModalRef = this.modalService.show(JsonTreeContainerEditor);
        return bsModalRef.content.showToUpdateContainerName(name, siblingsNodeNames);
    }
    triggerDeleteContainerAction(name: string): EventEmitter<JsonTreeContainerEditorEvent> {
        let bsModalRef = this.modalService.show(JsonTreeContainerEditor);
        return bsModalRef.content.showToDeleteContainer(name);
    }
    triggerCopyAction(pathToCopy: Path, destinationPath: Path): EventEmitter<JsonTreeContainerEditorEvent> {
        let bsModalRef = this.modalService.show(JsonTreeContainerEditor);
        return bsModalRef.content.showToCopyContainer(pathToCopy, destinationPath);
    }
}
