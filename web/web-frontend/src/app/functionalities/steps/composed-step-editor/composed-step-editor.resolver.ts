import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, Params, ActivatedRoute} from "@angular/router";
import {TestsService} from "../../../service/tests.service";
import {TestModel} from "../../../model/test/test.model";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
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
