import {Component, Input, ViewChild} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";
import {ResourceService} from "../../../../service/resources/resource.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {PathChooserComponent} from "../../path-chooser/path-chooser.component";
import {NgForm} from "@angular/forms";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";

@Component({
    moduleId: module.id,
    selector: 'select-shared-resource-modal',
    templateUrl: 'select-shared-resource-modal.component.html',
    styleUrls: [
        'select-shared-resource-modal.component.scss',
        '../../../../generic/css/generic.scss',
        '../../../../generic/css/forms.scss'
    ],
})
export class SelectSharedResourceModalComponent {

    @Input() resourceMapping: ResourceMapEnum;

    selectedResourcePath: Path;

    @ViewChild("resourceModal") resourceModal: ModalDirective;
    @ViewChild(PathChooserComponent) pathChooserComponent: PathChooserComponent;
    @ViewChild(NgForm) form: NgForm;

    resourcePaths:Array<Path> = [];
    selectedResourcePathSubject: Subject<Path>;

    constructor(private resourceService: ResourceService) {
    }

    show(): Observable<Path> {
        this.selectedResourcePath = null;
        this.selectedResourcePathSubject = new Subject<Path>();

        this.resourceService.getResourcePaths(this.resourceMapping.fileExtension).subscribe(
            (paths: Array<Path>) => {
                this.resourcePaths.length = 0;
                paths.forEach(path => this.resourcePaths.push(path));
                this.pathChooserComponent.initJsonModel();
                this.resourceModal.show();
            }
        );
        return this.selectedResourcePathSubject.asObservable();
    }

    getNewResourcePathAsString(): string {
        if(this.selectedResourcePath == null) return "";
        return "/" + this.selectedResourcePath.toString()
    }

    setSelectedDirectory(path: any) {
        this.selectedResourcePath = path;
    }

    update() {
        this.selectedResourcePathSubject.next(
            this.selectedResourcePath
        );
        this.resourceModal.hide();
    }

    cancel() {
        this.selectedResourcePathSubject.next(
            null
        );
        this.resourceModal.hide();
    }

    isValid(): boolean {
        return this.form.valid;
    }
}
