import {Injectable} from "@angular/core";
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {ComposedStepDef} from "../../../model/step/composed-step-def.model";
import {StepsService} from "../../../service/steps.service";
import {Path} from "../../../model/infrastructure/path/path.model";

@Injectable()
export class ComposedStepEditorResolver implements Resolve<any> {


    constructor(private route: ActivatedRoute,
                private stepsService: StepsService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let action = route.params['action'];
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

        if (action == "create") {
            let composedStepDef = new ComposedStepDef();
            composedStepDef.path = path;

            return composedStepDef;
        }

        return this.stepsService.getComposedStepDef(pathAsString);
    }
}
