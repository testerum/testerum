import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    EventEmitter, Input,
    OnInit,
    Output, Type,
    ViewChild, ViewContainerRef
} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {Arg} from "../../../../model/arg/arg.model";
import {ResourceComponent} from "../../../../functionalities/resources/editors/resource-component.interface";
import {ArgNodeComponent} from "../nodes/arg-node/arg-node.component";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";
import {JsonUtil} from "../../../../utils/json.util";
import {ObjectUtil} from "../../../../utils/object.util";
import {ResourceService} from "../../../../service/resources/resource.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {PathChooserContainerModel} from "../../path-chooser/model/path-chooser-container.model";
import {PathChooserComponent} from "../../path-chooser/path-chooser.component";
import {NgForm} from "@angular/forms";
import {Setting} from "../../../../functionalities/config/settings/model/setting.model";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";

@Component({
    moduleId: module.id,
    selector: 'new-shared-resource-path-modal',
    templateUrl: 'new-shared-resource-path-modal.component.html',
    styleUrls: [
        'new-shared-resource-path-modal.component.css',
        '../../../../generic/css/generic.css',
        '../../../../generic/css/forms.css'
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
