import {Injectable} from "@angular/core";
import {Observable} from 'rxjs/Observable';
import {Path} from "../model/infrastructure/path/path.model";
import {Router} from "@angular/router";
import {FileDirectoryChooserContainerModel} from "../generic/components/form/file_dir_chooser/model/file-directory-chooser-container.model";
import {FileSystemDirectory} from "../model/file/file-system-directory.model";
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";

@Injectable()
export class FileSystemService {

    private BASE_URL = "/rest/file_system/directory_tree";

    constructor(private router: Router,
                private http: HttpClient) {
    }

    getDirectoryTree(path: Path): Observable<FileDirectoryChooserContainerModel> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<any>(this.BASE_URL, httpOptions)
            .map(FileSystemService.extractFileDirectory);
    }

    private static extractFileDirectory(res: HttpResponse<FileDirectoryChooserContainerModel>): FileDirectoryChooserContainerModel {
        let fileSystemDirectory = new FileSystemDirectory().deserialize(res);

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
