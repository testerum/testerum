import {EventEmitter, Injectable} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {Project} from "../model/home/project.model";
import {RunnerTreeNodeModel} from "../functionalities/features/tests-runner/tests-runner-tree/model/runner-tree-node.model";

@Injectable()
export class ContextService {

    private currentProject: Project;
    projectChangedEventEmitter: EventEmitter<Project> = new EventEmitter<Project>();

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;

    isProjectSelected(): boolean {
        return this.currentProject != null;
    }

    getCurrentProject(): Project {
        return this.currentProject
    }

    getProjectName(): string {
        return this.currentProject ? this.currentProject.name: null;
    }

    setCurrentProject(newProject: Project) {
        if(newProject == null && this.currentProject == null) return;
        if(newProject != null && this.currentProject != null && newProject.path == this.currentProject.path) return;

        this.currentProject = newProject;
        this.setPageTitle(newProject);

        this.projectChangedEventEmitter.emit(newProject);
    }

    getProjectPath(): string {
        return this.currentProject ? this.currentProject.path: null;
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

    private setPageTitle(currentProject: Project) {
        if (!currentProject || !currentProject.name) {
            document.title = "Testerum";
            return;
        }

        document.title = currentProject.name + " - Testerum" ;
    }
}
