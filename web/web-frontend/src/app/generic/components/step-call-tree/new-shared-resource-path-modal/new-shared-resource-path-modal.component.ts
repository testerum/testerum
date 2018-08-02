import {Component, Input, OnInit, ViewChild} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";
import {ResourceService} from "../../../../service/resources/resource.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {PathChooserComponent} from "../../path-chooser/path-chooser.component";
import {NgForm} from "@angular/forms";
import {Observable, Subject} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'new-shared-resource-path-modal',
    templateUrl: 'new-shared-resource-path-modal.component.html',
    styleUrls: [
        'new-shared-resource-path-modal.component.scss'
    ],
})
export class NewSharedResourcePathModalComponent implements OnInit {

    @Input() resourceMapping: ResourceMapEnum;

    name: string;
    selectedDirectoryPath: Path = Path.createInstanceOfEmptyPath();

    @ViewChild("resourceModal") resourceModal: ModalDirective;
    @ViewChild(PathChooserComponent) pathChooserComponent: PathChooserComponent;
    @ViewChild(NgForm) form: NgForm;

    resourcePaths:Array<Path> = [];
    selectedResourcePathSubject: Subject<Path>;

    constructor(private resourceService: ResourceService) {
    }

    ngOnInit() {
    }

    show(): Observable<Path> {
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
        if(this.resourceMapping == null) return "";
        if(this.name == null) return "";
        let directories = this.selectedDirectoryPath != null ? this.selectedDirectoryPath.directories: [];
        return "/" + new Path(directories, this.name, this.resourceMapping.fileExtension).toString()
    }

    setSelectedDirectory(path: any) {
        this.selectedDirectoryPath = path;
    }

    update() {
        let directories = this.selectedDirectoryPath != null ? this.selectedDirectoryPath.directories: [];
        let selectedPath = new Path(directories, this.name, this.resourceMapping.fileExtension);

        this.selectedResourcePathSubject.next(
            selectedPath
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
