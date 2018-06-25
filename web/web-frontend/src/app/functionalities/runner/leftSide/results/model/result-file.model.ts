import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";

export class ResultFile extends JsonTreeNodeAbstract {

    path: Path;
    name: string;
    executionResult: ExecutionStatusEnum;
    durationMillis: number;

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }
}
