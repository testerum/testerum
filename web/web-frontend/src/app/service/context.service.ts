import {Injectable} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {ActivatedRouteSnapshot, Router} from "@angular/router";

@Injectable()
export class ContextService {

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;


    constructor(private router: Router) {
    }

    getProjectName(): string {
        return this.getProjectFromUrl();
    }

    setProjectName(projectName: string) {
        this.router.navigate(["/" + projectName + "/features"]);
    }

    setPathToCut(stepCallContainerComponent: StepCallContainerComponent) {
        this.stepToCopy = null;
        this.stepToCut = stepCallContainerComponent;
    }

    setPathToCopy(stepCallContainerComponent: StepCallContainerComponent) {
        this.stepToCopy = stepCallContainerComponent;
        this.stepToCut = null;
    }

    canPaste(stepCallContainerModel: StepCallContainerModel): boolean {
        return (this.stepToCopy != null && !(this.stepToCopy.model.stepCall == stepCallContainerModel.stepCall))
            || (this.stepToCut != null && !(this.stepToCut.model.stepCall == stepCallContainerModel.stepCall));
    }

    private getProjectFromUrl(): string {
        let root: ActivatedRouteSnapshot = this.router.routerState.snapshot.root;
        if(root.children.length == 0) {
            return null;
        }

        let firstActivatedRoute = root.children[0];
        return firstActivatedRoute.params['project'];
    }
}
