import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';


import {Feature} from "../model/feature/feature.model";
import {Path} from "../model/infrastructure/path/path.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {ServerRootMainNode} from "../model/main_tree/server-root-main-node.model";
import {FeaturesTreeFilter} from "../model/feature/filter/features-tree-filter.model";

@Injectable()
export class FeatureService {

    private FEATURE_URL = "/rest/features";

    constructor(private http: HttpClient) {}

    getFeatureTree(featureTreeFilter: FeaturesTreeFilter): Observable<ServerRootMainNode> {
        let body = featureTreeFilter.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ServerRootMainNode>(this.FEATURE_URL+"/tree", body, httpOptions).pipe(
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

    save(model: Feature): Observable<Feature> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<Feature>(this.FEATURE_URL, body, httpOptions).pipe(
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

    private static extractFeaturesTree(res: ServerRootMainNode): ServerRootMainNode {
        if(res == null) return null;
        return new ServerRootMainNode().deserialize(res);
    }
}


