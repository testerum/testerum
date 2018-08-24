import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Path} from "../model/infrastructure/path/path.model";
import {FileDirectoryChooserContainerModel} from "../generic/components/form/file_dir_chooser/model/file-directory-chooser-container.model";
import {FileSystemDirectory} from "../model/file/file-system-directory.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {CreateFileSystemDirectoryRequest} from "../model/file/create-file-system-directory-request.model";

@Injectable()
export class FileSystemService {

    private BASE_URL = "/rest/file_system";

    constructor(private http: HttpClient) {
    }

    getDirectoryTree(absoluteJavaPathAsString: string): Observable<FileDirectoryChooserContainerModel> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', absoluteJavaPathAsString)
        };

        return this.http
            .get<FileDirectoryChooserContainerModel>(this.BASE_URL + "/directory_tree", httpOptions).pipe(
            map(FileSystemService.extractFileDirectory));
    }

    private static extractFileDirectory(res: FileDirectoryChooserContainerModel): FileDirectoryChooserContainerModel {
        let fileSystemDirectory = new FileSystemDirectory().deserialize(res);

        return FileSystemService.mapFileDirectoryToChooserModel(fileSystemDirectory, null);
    }

    private static mapFileDirectoryToChooserModel(fileSystemDirectory: FileSystemDirectory, parent: FileDirectoryChooserContainerModel): FileDirectoryChooserContainerModel {
        let result = new FileDirectoryChooserContainerModel(
            parent,
            fileSystemDirectory.name,
            fileSystemDirectory.absoluteJavaPath,
            fileSystemDirectory.hasChildrenDirectories
        );

        for (let childDirectory of fileSystemDirectory.childrenDirectories) {
            result.getChildren().push(
                new FileDirectoryChooserContainerModel(
                    result,
                    childDirectory.name,
                    childDirectory.absoluteJavaPath,
                    childDirectory.hasChildrenDirectories
                )
            )
        }

        return result;
    }

    createFileSystemDirectory(absoluteJavaPathOfParentDir: string, newDirName: string): Observable<FileSystemDirectory> {

        let body = new CreateFileSystemDirectoryRequest(absoluteJavaPathOfParentDir, newDirName).serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<FileSystemDirectory>(this.BASE_URL + '/create_directory', body, httpOptions);
    }
}
