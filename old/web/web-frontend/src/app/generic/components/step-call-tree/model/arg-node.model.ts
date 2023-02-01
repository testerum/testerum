import {JsonTreeNodeAbstract} from "../../json-tree/model/json-tree-node.abstract";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {Arg} from "../../../../model/arg/arg.model";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";

export class ArgNodeModel extends JsonTreeNodeAbstract {

    parentContainer: JsonTreeContainer;
    arg: Arg;
    stepPatternParam: ParamStepPatternPart;

    constructor(parentContainer: JsonTreeContainer, arg: Arg, stepPatternParam: ParamStepPatternPart) {
        super(parentContainer);

        this.arg = arg;
        this.stepPatternParam = stepPatternParam;
    }
}
