import {Injectable} from "@angular/core";
import {Http, RequestOptions, Response, Headers} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import {ErrorService} from "./error.service";
import {Path} from "../model/infrastructure/path/path.model";
import {Router} from "@angular/router";
import {FileDirectoryChooserContainerModel} from "../generic/components/form/file_dir_chooser/model/file-directory-chooser-container.model";
import {FileSystemDirectory} from "../model/file/file-system-directory.model";

@Injectable()
export class FileSystemService {

    private BASE_URL = "/rest/file_system/directory_tree";

    constructor(private router: Router,
                private http: Http,
                private errorService: ErrorService) {
    }

    getDirectoryTree(path: Path): Observable<FileDirectoryChooserContainerModel> {
        return this.http
            .get(this.BASE_URL, {params: {path: path.toString()}})
            .map(FileSystemService.extractFileDirectory)
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    private static extractFileDirectory(res: Response): FileDirectoryChooserContainerModel {
        let fileSystemDirectory = new FileSystemDirectory().deserialize(res.json());

        return FileSystemService.mapFileDirectoryToChooserModel(fileSystemDirectory, null);
    }

    private static mapFileDirectoryToChooserModel(fileSystemDirectory: FileSystemDirectory, parent: FileDirectoryChooserContainerModel): FileDirectoryChooserContainerModel {
        let result = new FileDirectoryChooserContainerModel(
            parent,
            fileSystemDirectory.path,
            fileSystemDirectory.hasChildrenDirectories
        );

        for (let childDirectory of fileSystemDirectory.childrenDirectories) {
            result.getChildren().push(
                new FileDirectoryChooserContainerModel(
                    result,
                    childDirectory.path,
                    childDirectory.hasChildrenDirectories
                )
            )
        }

        return result;
    }
}
