import {Component, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Observable} from "rxjs/Rx";
import {Subject} from "rxjs/Subject";

@Component({
    selector: 'update-incompatibility-dialog',
    templateUrl: 'update-incompatibility-dialog.component.html',
    styleUrls: ['update-incompatibility-dialog.component.scss', '../../../../generic/css/generic.scss']
})
export class UpdateIncompatibilityDialogComponent {

    @ViewChild("infoModal") infoModal: ModalDirective;

    pathsForAffectedTests: Array<Path> = [];
    pathsForDirectAffectedSteps: Array<Path> = [];
    pathsForTransitiveAffectedSteps: Array<Path> = [];

    responseSubject: Subject<any> = new Subject<any>();

    show(pathsForAffectedTests: Array<Path>,
         pathsForDirectAffectedSteps: Array<Path>,
         pathsForTransitiveAffectedSteps: Array<Path>): Observable<any> {

        this.pathsForAffectedTests.length = 0;
        this.pathsForDirectAffectedSteps.length = 0;
        this.pathsForTransitiveAffectedSteps.length = 0;
        pathsForAffectedTests.forEach( it => this.pathsForAffectedTests.push(it));
        pathsForDirectAffectedSteps.forEach( it => this.pathsForDirectAffectedSteps.push(it));
        pathsForTransitiveAffectedSteps.forEach( it => this.pathsForTransitiveAffectedSteps.push(it));

        this.infoModal.show();

        return this.responseSubject
    }

    continue(): void {
        this.infoModal.hide();
        this.responseSubject.next()
    }

    close(): void {
        this.infoModal.hide();
    }
}
