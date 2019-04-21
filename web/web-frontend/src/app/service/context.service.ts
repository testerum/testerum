import {EventEmitter, Injectable} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {Project} from "../model/home/project.model";
import {RunnerTreeNodeModel} from "../functionalities/features/tests-runner/tests-runner-tree/model/runner-tree-node.model";
import {StringUtils} from "../utils/string-utils.util";

@Injectable()
export class ContextService {
    private LOCAL_STORAGE_AUTH_TOKEN_KEY = "authToken";

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
        if(newProject != null
            && this.currentProject != null
            && newProject.path == this.currentProject.path
            && newProject.name == this.currentProject.name) return;

        this.currentProject = newProject;
        this.setPageTitle(newProject);

        this.projectChangedEventEmitter.emit(newProject);
    }

    getProjectPath(): string {
        return this.currentProject ? this.currentProject.path: null;
    }

    isLoggedIn(): boolean {
        let authToken = localStorage.getItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
        return !StringUtils.isEmpty(authToken)
    }

    setAuthToken(authToken: string) {
        localStorage.setItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY, authToken);
    }

    getAuthToken(): string {
        return localStorage.getItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
    }

    logout() {
        localStorage.removeItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
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

    refreshPage() {
        if (this.isProjectSelected()) {
            window.location.href = window.location.protocol + "//" + window.location.host + "/" + this.getProjectName() + "/" ;
        } else {
            window.location.href = window.location.protocol + "//" + window.location.host + "/";
        }
    }
}
