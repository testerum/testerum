import { RunnerBasicStepNode } from "../runner-basic-step-node.model";
import { RunnerComposedStepNode } from "../runner-composed-step-node.model";
import { RunnerFeatureNode } from "../runner-feature-node.model";
import { RunnerTestNode } from "../runner-test-node.model";
import { RunnerTestOrFeatureNode } from "../runner-test-or-feature-node.model";
import { RunnerStepNode } from "../runner-step-node.model";

export class RunnerTreeDeserializationUtil {

    public static deserializeRunnerTestOrFeatureNodes(inputNodes: Object[]): RunnerTestOrFeatureNode[] {
        return inputNodes.map((inputNode) => RunnerTreeDeserializationUtil.deserializeRunnerTestOrFeatureNode(inputNode));
    }

    private static deserializeRunnerTestOrFeatureNode(inputNode: Object): RunnerTestOrFeatureNode {
        if (inputNode["@type"] == "RUNNER_TEST") {
            return new RunnerTestNode().deserialize(inputNode)
        }
        if (inputNode["@type"] == "RUNNER_FEATURE") {
            return new RunnerFeatureNode().deserialize(inputNode)
        }

        throw Error(`unknown node type [${inputNode["@type"]}]`);
    }

    public static deserializeRunnerStepNodes(inputNodes: Object[]): RunnerStepNode[] {
        return inputNodes.map((inputNode) => RunnerTreeDeserializationUtil.deserializeRunnerStepNode(inputNode));
    }

    private static deserializeRunnerStepNode(inputNode: Object): RunnerStepNode {
        if (inputNode["@type"] == "RUNNER_BASIC_STEP") {
            return new RunnerBasicStepNode().deserialize(inputNode)
        }
        if (inputNode["@type"] == "RUNNER_COMPOSED_STEP") {
            return new RunnerComposedStepNode().deserialize(inputNode)
        }

        throw Error(`unknown node type [${inputNode["@type"]}]`);
    }

}
