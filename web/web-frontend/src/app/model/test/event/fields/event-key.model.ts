import {PositionInParent} from "./position-in-parent.model";

export class EventKey implements Serializable<EventKey> {
    positionsFromRoot: Array<PositionInParent> = [];

    getCurrentNodeId(): string {
        if(this.positionsFromRoot.length == 0) {
            return null
        }

        return this.positionsFromRoot[this.positionsFromRoot.length-1].id;
    }

    isParentOf(childEvent: EventKey): boolean {
        if(this.positionsFromRoot.length > childEvent.positionsFromRoot.length) {
            return false;
        }

        for (let i = 0; i < this.positionsFromRoot.length; i++) {
            let parentNodePinInParent = this.positionsFromRoot[i];
            let childNodePinInParent = childEvent.positionsFromRoot[i];

            if(parentNodePinInParent.id != childNodePinInParent.id ||
                parentNodePinInParent.indexInParent != childNodePinInParent.indexInParent) {
                return false;
            }
        }

        return true;
    }

    deserialize(input: Object): EventKey {
        for (let idFromRootToCurrentNode of (input['positionsFromRoot'] || [])) {
            this.positionsFromRoot.push(new PositionInParent().deserialize(idFromRootToCurrentNode));
        }
        return this;
    }

    serialize(): string {
        return null;
    }
}
