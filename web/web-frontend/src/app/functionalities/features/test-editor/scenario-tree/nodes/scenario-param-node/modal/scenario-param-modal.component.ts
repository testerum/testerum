import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ComponentRef, ViewChild} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {ScenarioParam} from "../../../../../../../model/test/scenario/param/scenario-param.model";

@Component({
    moduleId: module.id,
    selector: 'arg-modal',
    templateUrl: 'scenario-param-modal.component.html',
    styleUrls: [
        'scenario-param-modal.component.scss'
    ],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class ScenarioParamModalComponent {

    oldScenarioParam: ScenarioParam;
    newScenarioParam: ScenarioParam;

    modalComponentRef: ComponentRef<ScenarioParamModalComponent>;
    modalSubject: Subject<ScenarioParam|null>;

    modalTitle;

    @ViewChild("resourceModal") modal: ModalDirective;

    constructor(private cd: ChangeDetectorRef) {
    }

    ngAfterViewInit(): void {
        this.modalTitle = "Edit Scenario Param";

        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    isValid() {
        return false;
    }

    onCancel() {
        this.modalSubject.next(null);
        this.modal.hide();
    }

    onOk() {

    }
}
