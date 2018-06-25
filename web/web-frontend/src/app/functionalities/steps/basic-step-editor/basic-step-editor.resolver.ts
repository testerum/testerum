import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, ActivatedRoute, Router} from "@angular/router";
import {StepsService} from "../../../service/steps.service";

@Injectable()
export class BasicStepEditorResolver implements Resolve<any> {

    constructor(private route: ActivatedRoute,
                private router: Router,
                private stepsService: StepsService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let pathAsString = route.params['path'];
        if(!pathAsString) {
            this.router.navigate(["/automated/steps"]);
        }

        return this.stepsService.getBasicStepDef(pathAsString);
    }
}
