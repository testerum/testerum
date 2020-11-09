import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {StepCall} from "../../../../model/step/step-call.model";
import {StepCallContainerModel} from "../model/step-call-container.model";
import {ComposedStepDef} from "../../../../model/step/composed-step-def.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {SubStepsContainerModel} from "../model/sub-steps-container.model";
import {ParamsContainerModel} from "../model/params-container.model";
import {ArgNodeModel} from "../model/arg-node.model";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {StepDef} from "../../../../model/step/step-def.model";
import {UndefinedStepDef} from "../../../../model/step/undefined-step-def.model";
import {BasicStepDef} from "../../../../model/step/basic-step-def.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {JsonTreeNode} from "../../json-tree/model/json-tree-node.model";

export class StepCallTreeUtil {

    static mapStepCallsToJsonTreeModel(stepCalls: Array<StepCall>, treeModel:JsonTreeModel, isManualExecutionMode: boolean = false): JsonTreeModel {

        let mappedStepCallContainer = new Map<string, SubStepsContainerModel>();
        let newChildren = StepCallTreeUtil.mapChildrenStepCallsToJsonTreeModel(stepCalls, treeModel, mappedStepCallContainer, isManualExecutionMode);

        this.copyChildrenStateFromOldToNew(treeModel.children, newChildren);
        treeModel.children = newChildren;

        return treeModel;
    }

    private static mapChildrenStepCallsToJsonTreeModel(stepCalls: Array<StepCall>, parentNode: JsonTreeContainer, mappedStepCallContainer: Map<string, SubStepsContainerModel>, isManualExecutionMode: boolean = false): Array<StepCallContainerModel> {

        let children: Array<StepCallContainerModel> = [];
        for (const stepCall of stepCalls) {
            let childStepCallContainerModel = this.createStepCallContainerWithChildren(stepCall, parentNode, mappedStepCallContainer, isManualExecutionMode);
            children.push(childStepCallContainerModel);
        }
        return children;
    }

    public static createStepCallContainerWithChildren(stepCall: StepCall, parentNode: JsonTreeContainer, mappedStepCallContainer: Map<string, SubStepsContainerModel>, isManualExecutionMode: boolean = false): StepCallContainerModel {
        let childStepCallContainerModel = StepCallTreeUtil.createStepCallContainer(stepCall, parentNode, parentNode.getChildren().length);
        this.createParamContainerWithChildren(stepCall, childStepCallContainerModel);

        let subStepContainer = StepCallTreeUtil.createSubStepsContainerWithChildren(stepCall.stepDef, mappedStepCallContainer, isManualExecutionMode);
        if(subStepContainer != null) {
            subStepContainer.parentContainer = childStepCallContainerModel;
            childStepCallContainerModel.children.push(
                subStepContainer
            );
        }

        return childStepCallContainerModel;
    }

    public static createParamContainerWithChildren(stepCall: StepCall, stepCallContainerModel: StepCallContainerModel) {
        let existingParamContainerModel: ParamsContainerModel = stepCallContainerModel.children.find(it => { return it instanceof ParamsContainerModel}) as ParamsContainerModel;

        if (stepCall.args.length > 0) {
            let paramsContainer = existingParamContainerModel;
            if (paramsContainer == null) {
                paramsContainer = new ParamsContainerModel(stepCallContainerModel);
                paramsContainer.jsonTreeNodeState.showChildren = true;
                stepCallContainerModel.children.unshift(
                    paramsContainer
                );
            }

            paramsContainer.children.length = 0;
            stepCall.args.forEach((arg, index) => {
                let stepPatternParam: ParamStepPatternPart = stepCall.getStepPatternParamByIndex(index);
                let argNode = new ArgNodeModel(paramsContainer, arg, stepPatternParam);
                paramsContainer.children.push(argNode)
            });
        } else {
            ArrayUtil.removeElementFromArray(stepCallContainerModel.children, existingParamContainerModel);
        }
    }

    public static createSubStepsContainerWithChildren(stepDef: StepDef, mappedStepCallContainer: Map<string, SubStepsContainerModel>, isManualExecutionMode: boolean = false): SubStepsContainerModel {
        if (stepDef instanceof BasicStepDef) { return null; }

        if(isManualExecutionMode) {
            if (stepDef instanceof UndefinedStepDef) { return null; }
            if (stepDef instanceof ComposedStepDef && stepDef.stepCalls.length == 0) { return null; }
        }

        let subStepsContainer = new SubStepsContainerModel(null);

        if (stepDef.path) {
            let stepDefKey = stepDef.path.toString();
            let existingSubStepsContainerModel = mappedStepCallContainer.get(stepDefKey);
            if (existingSubStepsContainerModel) {
                return existingSubStepsContainerModel;
            }

            mappedStepCallContainer.set(stepDefKey, subStepsContainer);
        }

        if (stepDef instanceof UndefinedStepDef) {
            return subStepsContainer;
        }

        if (stepDef instanceof ComposedStepDef) {
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

            return subStepsContainer;
        }


        return null;
    }

    private static createStepCallContainer(stepCall: StepCall, parentNode: JsonTreeContainer, indexInParent:number): StepCallContainerModel {
        let isRootNode = !(parentNode instanceof SubStepsContainerModel);
        return new StepCallContainerModel(parentNode, indexInParent, stepCall, isRootNode)
    }

    private static copyChildrenStateFromOldToNew(oldChildren: Array<JsonTreeNode>, newChildren: Array<JsonTreeNode>) {
        if(oldChildren == null || oldChildren.length == 0 || newChildren == null || newChildren.length == 0) return;
        if(oldChildren.length != newChildren.length) return;

        for (let i = 0; i < oldChildren.length; i++) {
            let oldChild = oldChildren[i];
            let newChild = newChildren[i];

            if (oldChild.isContainer() && newChild.isContainer()) {
                this.copyTreeContainerStateFromOldToNew(oldChild as JsonTreeContainer, newChild as JsonTreeContainer);
            }
        }
    }

    private static copyTreeContainerStateFromOldToNew(oldChild: JsonTreeContainer, newChild: JsonTreeContainer) {
        newChild.getNodeState().showChildren = oldChild.getNodeState().showChildren;
        this.copyChildrenStateFromOldToNew(oldChild.getChildren(), newChild.getChildren());
    }
}
