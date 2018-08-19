import { Path } from "../../infrastructure/path/path.model";
import { FeatureNode } from "./feature-node.model";
import { TestProperties } from "../../test/test-properties.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class TestFeatureNode implements Serializable<TestFeatureNode>, FeatureNode {
    name: string;
    properties: TestProperties;
    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): TestFeatureNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.properties = new TestProperties().deserialize(input['properties']);
        this.hasOwnOrDescendantWarnings = input["hasOwnOrDescendantWarnings"];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
