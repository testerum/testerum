import { FeatureFeatureNode } from "../feature-feature-node.model";
import { TestFeatureNode } from "../test-feature-node.model";
import { FeatureNode } from "../feature-node.model";
import { RootFeatureNode } from "../root-feature-node.model";

export class FeatureTreeDeserializationUtil {

    static deserialize(inputNodes: Object[]): FeatureNode[] {
        let result: FeatureNode[] = [];

        for (const inputNode of inputNodes) {
            result.push(
                FeatureTreeDeserializationUtil.deserializeNode(inputNode)
            )
        }

        return result;
    }

    static deserializeNode(inputNode: Object): FeatureNode {
        if (inputNode["@type"] == "FEATURE_ROOT") {
            return new RootFeatureNode().deserialize(inputNode);
        }
        if (inputNode["@type"] == "FEATURE_FEATURE") {
            return new FeatureFeatureNode().deserialize(inputNode);
        }
        if (inputNode["@type"] == "FEATURE_TEST") {
            return new TestFeatureNode().deserialize(inputNode);
        }

        throw Error(`unknown node type [${inputNode["@type"]}]`);
    }

}
