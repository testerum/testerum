import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Path} from "../model/infrastructure/path/path.model";
import {FileTreeContainer} from "../generic/components/form/file_chooser/file-tree/model/file-tree.container";
import {FileSystemDirectory} from "../model/file/file-system-directory.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {CreateFileSystemDirectoryRequest} from "../model/file/create-file-system-directory-request.model";
import {PathInfo} from "../model/infrastructure/path/path-info.model";
import {FileSystemEntry} from "../model/file/file-system-entry.model";
import {FileSystemFile} from "../model/file/file-system-file.model";
import {FileTreeNode} from "../generic/components/form/file_chooser/file-tree/model/file-tree-node.model";

@Injectable()
export class FileSystemService {

    private BASE_URL = "/rest/file_system";

    constructor(private http: HttpClient) {
    }

    getDirectoryTree(absoluteJavaPathAsString: string, showFiles: boolean = false): Observable<FileTreeContainer> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', absoluteJavaPathAsString)
                .append('showFiles', ""+showFiles)
        };

        return this.http
            .get<FileTreeContainer>(this.BASE_URL + "/directory_tree", httpOptions).pipe(
            map(FileSystemService.extractFileDirectory));
    }

    private static extractFileDirectory(res: FileTreeContainer): FileTreeContainer {
        let fileSystemDirectory = new FileSystemDirectory().deserialize(res);

        return FileSystemService.mapFileDirectoryToChooserModel(fileSystemDirectory, null);
    }

    private static mapFileDirectoryToChooserModel(fileSystemDirectory: FileSystemDirectory, parent: FileTreeContainer): FileTreeContainer {
        let result = new FileTreeContainer(
            parent,
            fileSystemDirectory.name,
            fileSystemDirectory.absoluteJavaPath,
            fileSystemDirectory.isProject,
            fileSystemDirectory.canCreateChild,
            fileSystemDirectory.hasChildren
        );

        this.mapChildrenFileDirectoryToChooserModel(fileSystemDirectory.children, result);

        return result;
    }

    private static mapChildrenFileDirectoryToChooserModel(childrenDirectoryToMap: Array<FileSystemEntry>, parent: FileTreeContainer) {

        for (let child of childrenDirectoryToMap) {

            let fileTreeNode: FileTreeNode;

            if (child instanceof FileSystemFile) {
                fileTreeNode = new FileTreeNode(parent, child.name, child.absoluteJavaPath);
            }

            if (child instanceof FileSystemDirectory) {
                fileTreeNode = new FileTreeContainer(
                    parent,
                    child.name,
                    child.absoluteJavaPath,
                    child.isProject,
                    child.canCreateChild,
                    child.hasChildren
                );
                this.mapChildrenFileDirectoryToChooserModel(child.children, fileTreeNode as FileTreeContainer);
            }

            parent.getChildren().push(
                fileTreeNode
            );
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
