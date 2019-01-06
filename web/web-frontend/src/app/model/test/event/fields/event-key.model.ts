import {PositionInParent} from "./position-in-parent.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {RunnerEvent} from "../runner.event";

export class EventKey implements Serializable<EventKey> {
    private positionsFromRoot: Array<PositionInParent> = [];
    eventKeyAsString: string;

    deserialize(input: Object): EventKey {
        for (let idFromRootToCurrentNode of (input['positionsFromRoot'] || [])) {
            this.positionsFromRoot.push(new PositionInParent().deserialize(idFromRootToCurrentNode));
        }
        this.eventKeyAsString = this.getEventKeyAsString(this.positionsFromRoot);
        return this;
    }

    serialize(): string {
        return null;
    }

    private getEventKeyAsString(positionsFromRoot: Array<PositionInParent>): string {

        let result: string = "";
        for (const positionInParent of positionsFromRoot) {
            result = result + positionInParent.id + "_" + positionInParent.indexInParent + "#";
        }
        return result;
    }
}
