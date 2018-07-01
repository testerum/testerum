import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot} from "@angular/router";
import {StepsService} from "../../../service/steps.service";
import {UrlService} from "../../../service/url.service";

@Injectable()
export class BasicStepEditorResolver implements Resolve<any> {

    constructor(private urlService: UrlService,
                private stepsService: StepsService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let pathAsString = route.params['path'];
        if(!pathAsString) {
            this.urlService.navigateToSteps();
        }

        return this.stepsService.getBasicStepDef(pathAsString);
    }
}
