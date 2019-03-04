import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Path} from "../model/infrastructure/path/path.model";
import {FileDirTreeContainerModel} from "../generic/components/form/file_dir_chooser/file-dir-tree/model/file-dir-tree-container.model";
import {FileSystemDirectory} from "../model/file/file-system-directory.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {CreateFileSystemDirectoryRequest} from "../model/file/create-file-system-directory-request.model";
import {PathInfo} from "../model/infrastructure/path/path-info.model";

@Injectable()
export class FileSystemService {

    private BASE_URL = "/rest/file_system";

    constructor(private http: HttpClient) {
    }

    getDirectoryTree(absoluteJavaPathAsString: string): Observable<FileDirTreeContainerModel> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', absoluteJavaPathAsString)
        };

        return this.http
            .get<FileDirTreeContainerModel>(this.BASE_URL + "/directory_tree", httpOptions).pipe(
            map(FileSystemService.extractFileDirectory));
    }

    private static extractFileDirectory(res: FileDirTreeContainerModel): FileDirTreeContainerModel {
        let fileSystemDirectory = new FileSystemDirectory().deserialize(res);

        return FileSystemService.mapFileDirectoryToChooserModel(fileSystemDirectory, null);
    }

    private static mapFileDirectoryToChooserModel(fileSystemDirectory: FileSystemDirectory, parent: FileDirTreeContainerModel): FileDirTreeContainerModel {
        let result = new FileDirTreeContainerModel(
            parent,
            fileSystemDirectory.name,
            fileSystemDirectory.absoluteJavaPath,
            fileSystemDirectory.isProject,
            fileSystemDirectory.canCreateChild,
            fileSystemDirectory.hasChildrenDirectories
        );

        this.mapChildrenFileDirectoryToChooserModel(fileSystemDirectory.childrenDirectories, result);

        return result;
    }

    private static mapChildrenFileDirectoryToChooserModel(childrenDirectoryToMap: Array<FileSystemDirectory>, parent: FileDirTreeContainerModel) {

        for (let childDirectory of childrenDirectoryToMap) {
            let fileDirectoryChooserContainerModel = new FileDirTreeContainerModel(
                parent,
                childDirectory.name,
                childDirectory.absoluteJavaPath,
                childDirectory.isProject,
                childDirectory.canCreateChild,
                childDirectory.hasChildrenDirectories
            );

            parent.getChildren().push(
                fileDirectoryChooserContainerModel
            );

            this.mapChildrenFileDirectoryToChooserModel(childDirectory.childrenDirectories, fileDirectoryChooserContainerModel);
        }
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

    getPathInfo(pathAsString: string): Observable<PathInfo> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<PathInfo>(this.BASE_URL + "/path/info", httpOptions)
            .pipe(map( it => new PathInfo().deserialize(it)));
    }
}
