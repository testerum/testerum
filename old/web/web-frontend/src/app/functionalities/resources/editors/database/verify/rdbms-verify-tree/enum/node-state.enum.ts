

export class NodeState {
    public static UNUSED = new NodeState("UNUSED");
    public static USED = new NodeState("USED");
    public static MISSING_FROM_SCHEMA = new NodeState("MISSING_FROM_SCHEMA");

    constructor(private stateAsString: string) { //for debug
    }
}
