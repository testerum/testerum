import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';


import {Feature} from "../model/feature/feature.model";
import {Path} from "../model/infrastructure/path/path.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {RootFeatureNode} from "../model/feature/tree/root-feature-node.model";
import {FeaturesTreeFilter} from "../model/feature/filter/features-tree-filter.model";
import {JsonUtil} from "../utils/json.util";

@Injectable()
export class FeatureService {

    private FEATURE_URL = "/rest/features";

    constructor(private http: HttpClient) {}

    getFeatureTree(featureTreeFilter: FeaturesTreeFilter): Observable<RootFeatureNode> {
        let body = featureTreeFilter.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<RootFeatureNode>(this.FEATURE_URL+"/tree", body, httpOptions).pipe(
            map(FeatureService.extractFeaturesTree));
    }

    getFeature(path: Path): Observable<Feature> {

        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<Feature>(this.FEATURE_URL, httpOptions).pipe(
            map(FeatureService.extractFeature));
    }

    save(model: Feature, fileAttachmentsAdded: File[], attachmentsPathsToDelete: Path[]): Observable<Feature> {
        const formdata: FormData = new FormData();

        formdata.append('feature', model.serialize());
        formdata.append("attachmentsPathsToDelete", JsonUtil.serializeArrayOfSerializable(attachmentsPathsToDelete));

        for (let i = 0; i < fileAttachmentsAdded.length; i++) {
            formdata.append("attachmentFiles", fileAttachmentsAdded[i])
        }

        return this.http
            .post<Feature>(this.FEATURE_URL, formdata).pipe(
            map(res => new Feature().deserialize(res)));
    }

    delete(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<void>(this.FEATURE_URL, httpOptions)
    }

    private static extractFeature(res: Feature): Feature {
        if(res == null) return null;
        return new Feature().deserialize(res);
    }

    private static extractFeaturesTree(res: RootFeatureNode): RootFeatureNode {
        if(res == null) return null;
        return new RootFeatureNode().deserialize(res);
    }

    copy(sourcePath: Path, destinationPath: Path):  Observable<Path> {
        const httpOptions = {
            params: new HttpParams()
                .append('sourcePath', sourcePath.toString())
                .append('destinationPath', destinationPath.toString())
        };

        return this.http
            .post<Path>(this.FEATURE_URL+"/copy", null, httpOptions).pipe(
                map(res => Path.deserialize(res)));
    }

    move(sourcePath: Path, destinationPath: Path): Observable<Path> {
        const httpOptions = {
            params: new HttpParams()
                .append('sourcePath', sourcePath.toString())
                .append('destinationPath', destinationPath.toString())
        };

        return this.http
            .post<Path>(this.FEATURE_URL+"/move", null, httpOptions).pipe(
                map(res => Path.deserialize(res)));
    }
}


