import {Path} from "../../../model/infrastructure/path/path.model";
import {Subject} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable()
export class PathChooserService {
    selectPathSubject: Subject<Path> = new Subject<Path>();

}
