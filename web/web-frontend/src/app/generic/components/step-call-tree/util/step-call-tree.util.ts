
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {StepCall} from "../../../../model/step-call.model";
import {StepCallContainerModel} from "../model/step-call-container.model";
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {SubStepsContainerModel} from "../model/sub-steps-container.model";
import {ParamsContainerModel} from "../model/params-container.model";
import {ArgNodeModel} from "../model/arg-node.model";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";

export class StepCallTreeUtil {

    static mapStepCallsToJsonTreeModel(stepCalls: Array<StepCall>, treeModel:JsonTreeModel): JsonTreeModel {

        treeModel.children = StepCallTreeUtil.mapChildrenStepCallsToJsonTreeModel(stepCalls, treeModel);

        return treeModel;
    }

    private static mapChildrenStepCallsToJsonTreeModel(stepCalls: Array<StepCall>, parentNode: JsonTreeContainer): Array<StepCallContainerModel> {

        let children: Array<StepCallContainerModel> = [];
        for (const stepCall of stepCalls) {
            let childStepCallContainerModel = this.createStepCallContainerWithChildren(stepCall, parentNode);
            children.push(childStepCallContainerModel);
        }
        return children;
    }

    public static createStepCallContainerWithChildren(stepCall, parentNode: JsonTreeContainer) {
        let childStepCallContainerModel = StepCallTreeUtil.createStepCallContainer(stepCall, parentNode, parentNode.getChildren().length);

        if (stepCall.args.length > 0) {
            let paramsContainer = new ParamsContainerModel(childStepCallContainerModel);
            paramsContainer.jsonTreeNodeState.showChildren = true;
            childStepCallContainerModel.children.push(
                paramsContainer
            );

            stepCall.args.forEach((arg, index) => {
                let stepPatternParam: ParamStepPatternPart = stepCall.getStepPatternParamByIndex(index);
                let argNode = new ArgNodeModel(paramsContainer, arg, stepPatternParam);
                paramsContainer.children.push(argNode)
            });
        }

        if (stepCall.stepDef instanceof ComposedStepDef) {
            let subStepsContainer = new SubStepsContainerModel(childStepCallContainerModel);
            childStepCallContainerModel.children.push(
                subStepsContainer
            );

            subStepsContainer.children = StepCallTreeUtil.mapChildrenStepCallsToJsonTreeModel(stepCall.stepDef.stepCalls, subStepsContainer);
        }
        return childStepCallContainerModel;
    }

    private static createStepCallContainer(stepCall: StepCall, parentNode: JsonTreeContainer, indexInParent:number): StepCallContainerModel {
        let isRootNode = !(parentNode instanceof SubStepsContainerModel);
        return new StepCallContainerModel(parentNode, indexInParent, stepCall, isRootNode)
    }
}
