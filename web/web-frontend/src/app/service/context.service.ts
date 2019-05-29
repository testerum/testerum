import {EventEmitter, Injectable} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {Project} from "../model/home/project.model";
import {RunnerTreeNodeModel} from "../functionalities/features/tests-runner/tests-runner-tree/model/runner-tree-node.model";
import {StringUtils} from "../utils/string-utils.util";
import {LicenseInfo} from "../model/user/license/license-info.model";
import {UserService} from "./user.service";
import {UserLicenseInfo} from "../model/user/license/user-license-info.model";
import {LicenseAlertModalService} from "../functionalities/user/license/alert/license-alert-modal.service";

@Injectable()
export class ContextService {

    private currentProject: Project;
    projectChangedEventEmitter: EventEmitter<Project> = new EventEmitter<Project>();

    license: LicenseContext;

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;

    constructor(private userService: UserService) {
        this.license = new LicenseContext(userService);
    }

    init(): Promise<any> {
        return this.license.init();
    }

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

class LicenseContext {
    private LOCAL_STORAGE_AUTH_TOKEN_KEY = "authToken";
    private LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT = "LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT";

    private licenseInfo: LicenseInfo;

    constructor(private userService: UserService) {
    }

    init(): Promise<any> {
        return this.userService.getLicenseInfo()
            .toPromise()
            .then( (licenseInfo: LicenseInfo) => {
                this.licenseInfo = licenseInfo;
        });
    }

    isLoggedIn(): boolean {
        if (!this.licenseInfo) return false;

        return !!this.licenseInfo.currentUserLicense;
    }

    hasValidLicenseOrTrialValidOrExpired(): boolean {
        if (!this.licenseInfo) return false;

        if (this.licenseInfo.currentUserLicense && this.licenseInfo.currentUserLicense.expired == false) {
            return true;
        }

        if (this.licenseInfo.trialLicense) {
            return true;
        }

        return false;
    }

    setAuthToken(authToken: string, userLicenseInfo: UserLicenseInfo) {
        let licenseInfo = new LicenseInfo();
        licenseInfo.currentUserLicense = userLicenseInfo;
        licenseInfo.serverHasLicenses = true;

        this.licenseInfo = licenseInfo;
        localStorage.setItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY, authToken);

        this.deleteLastUsedOfRemainingDaysLicenseAlert();
    }

    getAuthToken(): string {
        return localStorage.getItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
    }

    logout() {
        localStorage.removeItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
        this.licenseInfo = null;
    }

    getLicenseInfo(): LicenseInfo {
        return this.licenseInfo;
    }

    getLastUsedOfRemainingDaysLicenseAlert(): number {
        let lastUsedOfRemainingDaysLicenseAlert = localStorage.getItem(this.LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT);
        //if is not initialized, set the initial value as an big number
        return lastUsedOfRemainingDaysLicenseAlert ? +lastUsedOfRemainingDaysLicenseAlert : 99999;
    }

    deleteLastUsedOfRemainingDaysLicenseAlert() {
        localStorage.removeItem(this.LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT);
    }

    saveLastUsedOfRemainingDaysLicenseAlert(nextAlertDaysRemaining: number) {
        localStorage.setItem(this.LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT, ""+nextAlertDaysRemaining);
    }
}
