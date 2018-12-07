import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {StepCall} from "../../../../model/step-call.model";
import {StepCallContainerModel} from "../model/step-call-container.model";
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {SubStepsContainerModel} from "../model/sub-steps-container.model";
import {ParamsContainerModel} from "../model/params-container.model";
import {ArgNodeModel} from "../model/arg-node.model";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {StepDef} from "../../../../model/step-def.model";

export class StepCallTreeUtil {

    static mapStepCallsToJsonTreeModel(stepCalls: Array<StepCall>, treeModel:JsonTreeModel): JsonTreeModel {

        let mappedStepCallContainer = new Map<string, SubStepsContainerModel>();
        treeModel.children = StepCallTreeUtil.mapChildrenStepCallsToJsonTreeModel(stepCalls, treeModel, mappedStepCallContainer);

        return treeModel;
    }

    private static mapChildrenStepCallsToJsonTreeModel(stepCalls: Array<StepCall>, parentNode: JsonTreeContainer, mappedStepCallContainer: Map<string, SubStepsContainerModel>): Array<StepCallContainerModel> {

        let children: Array<StepCallContainerModel> = [];
        for (const stepCall of stepCalls) {
            let childStepCallContainerModel = this.createStepCallContainerWithChildren(stepCall, parentNode, mappedStepCallContainer);
            children.push(childStepCallContainerModel);
        }
        return children;
    }

    public static createStepCallContainerWithChildren(stepCall: StepCall, parentNode: JsonTreeContainer, mappedStepCallContainer: Map<string, SubStepsContainerModel>): StepCallContainerModel {
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

        let subStepContainer = StepCallTreeUtil.createSubStepsContainerWithChildren(stepCall.stepDef, mappedStepCallContainer);
        if(subStepContainer != null) {
            subStepContainer.parentContainer = childStepCallContainerModel;
            childStepCallContainerModel.children.push(
                subStepContainer
            );
        }

        return childStepCallContainerModel;
    }

    public static createSubStepsContainerWithChildren(stepDef: StepDef, mappedStepCallContainer: Map<string, SubStepsContainerModel>): SubStepsContainerModel {
        if (stepDef instanceof ComposedStepDef) {

            let stepDefKey = stepDef.path.toString();
            let existingSubStepsContainerModel = mappedStepCallContainer.get(stepDefKey);
            if (existingSubStepsContainerModel) {
                return existingSubStepsContainerModel;
            }

            let subStepsContainer = new SubStepsContainerModel(null);

            if (stepDef.stepCalls) {
                subStepsContainer.children = StepCallTreeUtil.mapChildrenStepCallsToJsonTreeModel(stepDef.stepCalls, subStepsContainer, mappedStepCallContainer);
            }

            subStepsContainer.descendantsHaveWarnings = false;
            for (const childStepCall of stepDef.stepCalls) {
                if (childStepCall.getAllWarnings().length > 0 || childStepCall.getAnyDescendantsHaveWarnings()) {
                    subStepsContainer.descendantsHaveWarnings = true;
                    break;
                }
            }

            mappedStepCallContainer.set(stepDefKey, subStepsContainer);

            return subStepsContainer;
        }
        return null;
    }

    private static createStepCallContainer(stepCall: StepCall, parentNode: JsonTreeContainer, indexInParent:number): StepCallContainerModel {
        let isRootNode = !(parentNode instanceof SubStepsContainerModel);
        return new StepCallContainerModel(parentNode, indexInParent, stepCall, isRootNode)
    }
}
