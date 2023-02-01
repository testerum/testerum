import {LogLineTypeEnum} from "./log-line-type.enum";
import {RunnerTreeNodeModel} from "../../tests-runner-tree/model/runner-tree-node.model";

export class TestsRunnerLogLineModel {
    text: string;
    subText: string;
    type: LogLineTypeEnum;

    constructor(text: string, type: LogLineTypeEnum, subText: string = null){
        this.text = text;
        this.type = type;
        this.subText = subText;
    }
}
