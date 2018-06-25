import {Injectable} from "@angular/core";
import {ExecutionPieModel} from "./model/execution-pie.model";

@Injectable()
export class ExecutionPieService {

    model: ExecutionPieModel;

    setExecutionPieModel(model: ExecutionPieModel) {
        this.model = model;
    }
}
