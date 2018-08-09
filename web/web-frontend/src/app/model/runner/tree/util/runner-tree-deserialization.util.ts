import { RunnerNode } from "../runner-node.model";
import { RunnerBasicStepNode } from "../runner-basic-step-node.model";
import { RunnerComposedStepNode } from "../runner-composed-step-node.model";
import { RunnerFeatureNode } from "../runner-feature-node.model";
import { RunnerRootNode } from "../runner-root-node.model";
import { RunnerTestNode } from "../runner-test-node.model";

export class RunnerTreeDeserializationUtil {

    static deserialize(inputNodes: Object[]): RunnerNode[] {
        let result: RunnerNode[] = [];

        for (const inputNode of inputNodes) {
            result.push(
                RunnerTreeDeserializationUtil.deserializeNode(inputNode)
            )
        }

        return result;
    }

    private static deserializeNode(inputNode: Object): RunnerNode {
        if (inputNode["@type"] == "RUNNER_BASIC_STEP") {
            return new RunnerBasicStepNode().deserialize(inputNode)
        }
        if (inputNode["@type"] == "RUNNER_COMPOSED_STEP") {
            return new RunnerComposedStepNode().deserialize(inputNode)
        }
        if (inputNode["@type"] == "RUNNER_TEST") {
            return new RunnerTestNode().deserialize(inputNode)
        }
        if (inputNode["@type"] == "RUNNER_FEATURE") {
            return new RunnerFeatureNode().deserialize(inputNode)
        }
        if (inputNode["@type"] == "RUNNER_ROOT") {
            return new RunnerRootNode().deserialize(inputNode)
        }

        throw Error(`unknown node type [${inputNode["@type"]}]`);
    }
}
