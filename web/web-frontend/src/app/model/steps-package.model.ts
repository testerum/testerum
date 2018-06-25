import {BasicStepDef} from "./basic-step-def.model";
import {TreeContainerModel} from "./infrastructure/tree-container.model";
import {JsonUtil} from "../utils/json.util";
import {StepDef} from "./step-def.model";

export class StepsPackageModel implements Serializable<StepsPackageModel>, TreeContainerModel<StepsPackageModel, StepDef> {

    path:string = "";
    name:string;
    childStepsPackages:Array<StepsPackageModel> = [];
    steps:Array<StepDef> = [];
    editable:boolean = false;

    public getChildContainers(): Array<StepsPackageModel> {
        return this.childStepsPackages;
    }

    public getChildNodes(): Array<StepDef> {
        return this.steps;
    }

    deserialize(input: Object): StepsPackageModel {
        this.path = input['path'];
        this.name = input['name'];
        for (let step of (input['steps'] || [])) {
            this.steps.push(new BasicStepDef().deserialize(step));
        }

        return this;
    }

    serialize():string {
        return ""+
            '{' +
            '"path":'+JsonUtil.stringify(this.path)+','+
            '"name":'+JsonUtil.stringify(this.name)+','+
            '"childStepsPackages":'+JsonUtil.serializeArrayOfSerializable(this.childStepsPackages)+','+
            '"steps":'+JsonUtil.serializeArrayOfSerializable(this.steps)+','+
            '"editable":'+JsonUtil.stringify(this.editable)+
            '}'
    }
}
