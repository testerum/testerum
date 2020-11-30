import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Subject} from "rxjs";
import {StepUsageDialogModeEnum} from "./model/step-usage-dialog-mode.enum";
import {UrlService} from "../../../../service/url.service";
import {StepUsageDialogResponseEnum} from "./model/step-usage-dialog-response.enum";

@Component({
    selector: 'step-usage-dialog',
    templateUrl: 'step-usage-dialog.component.html',
    styleUrls: ['step-usage-dialog.component.scss']
})
export class StepUsageDialogComponent implements AfterViewInit {

    @ViewChild("stepUsageModal", { static: true }) modal: ModalDirective;

    mode: StepUsageDialogModeEnum;
    pathsForAffectedTests: Array<Path> = [];
    pathsForDirectAffectedSteps: Array<Path> = [];
    pathsForTransitiveAffectedSteps: Array<Path> = [];

    DELETE_STEP = StepUsageDialogModeEnum.DELETE_STEP
    UPDATE_STEP = StepUsageDialogModeEnum.UPDATE_STEP
    INFO_STEP = StepUsageDialogModeEnum.INFO_STEP

    modalComponentRef: ComponentRef<StepUsageDialogComponent>;
    modalSubject = new Subject<StepUsageDialogResponseEnum>();

    urlService: UrlService;
    constructor(urlService: UrlService) {
        this.urlService = urlService
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    init(mode: StepUsageDialogModeEnum,
         pathsForAffectedTests: Array<Path>,
         pathsForDirectAffectedSteps: Array<Path>,
         pathsForTransitiveAffectedSteps: Array<Path>) {

        this.mode = mode;
        this.pathsForAffectedTests.length = 0;
        this.pathsForDirectAffectedSteps.length = 0;
        this.pathsForTransitiveAffectedSteps.length = 0;
        pathsForAffectedTests.forEach( it => this.pathsForAffectedTests.push(it));
        pathsForDirectAffectedSteps.forEach( it => this.pathsForDirectAffectedSteps.push(it));
        pathsForTransitiveAffectedSteps.forEach( it => this.pathsForTransitiveAffectedSteps.push(it));
    }

    continue(): void {
        this.modal.hide();
        this.modalSubject.next(StepUsageDialogResponseEnum.OK)
    }

    close(): void {
        this.modal.hide();
        this.modalSubject.next(StepUsageDialogResponseEnum.CANCEL)
    }

    getNameFromPath(path: Path): string {
        let result = path.toDirectoryString();

        if (path.fileName) {
            result += path.fileName
        }
        return result;
    }
}
