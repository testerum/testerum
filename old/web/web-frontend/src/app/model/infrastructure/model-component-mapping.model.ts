import {Type} from "@angular/core";

export class ModelComponentMapping {

    mappings: Array<ModelComponentPair> = [];

    addPair(model: Type<any>, component: Type<any>): ModelComponentMapping {
        this.mappings.push(new ModelComponentPair(model, component));

        return this;
    }

    getComponentOfModel(modelInstance: any): Type<any> {
        for (let mapping of this.mappings) {
            if(modelInstance instanceof mapping.modelType) {
                return mapping.componentType
            }
        }
// return this.mappings[0].componentType;
        throw new Error("Model - Component type mapping not found")
    }
}

class ModelComponentPair {
    modelType: Type<any>;
    componentType: Type<any>;

    constructor(modelType: Type<any>,
                componentType: Type<any>) {
        this.modelType = modelType;
        this.componentType = componentType;

    }
}
