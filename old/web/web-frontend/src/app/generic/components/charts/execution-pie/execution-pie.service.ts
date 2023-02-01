import {Injectable} from "@angular/core";
import {ExecutionPieModel} from "./model/execution-pie.model";

@Injectable()
export class ExecutionPieService {

    readonly pieModel: ExecutionPieModel = new ExecutionPieModel();

}
