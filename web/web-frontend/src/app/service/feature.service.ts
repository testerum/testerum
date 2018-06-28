import {Injectable} from "@angular/core";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {Feature} from "../model/feature/feature.model";
import {Path} from "../model/infrastructure/path/path.model";
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from "@angular/common/http";
import {RootServerTreeNode} from "../model/tree/root-server-tree-node.model";

@Injectable()
export class FeatureService {

    private FEATURE_URL = "/rest/features";

    constructor(private http: HttpClient) {}

    getFeatureTree(): Observable<RootServerTreeNode> {

        return this.http
            .get<any>(this.FEATURE_URL+"/tree")
            .map(FeatureService.extractFeaturesTree);
    }

    getFeature(path: Path): Observable<Feature> {

        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<any>(this.FEATURE_URL, httpOptions)
            .map(FeatureService.extractFeature);
    }

    create(model: Feature): Observable<Feature> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.FEATURE_URL, body, httpOptions)
            .map(FeatureService.extractFeature);
    }

    delete(path: Path): Observable<void> {

        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<any>(this.FEATURE_URL, httpOptions)
            .map(FeatureService.extractFeature);
    }

    update(model: Feature): Observable<Feature> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<any>(this.FEATURE_URL, body, httpOptions)
            .map(FeatureService.extractFeature);
    }

    private static extractFeature(res:  HttpResponse<Feature>): Feature {
        if(res == null) return null;
        return new Feature().deserialize(res);
    }

    private static extractFeaturesTree(res:  HttpResponse<RootServerTreeNode>): RootServerTreeNode {
        if(res == null) return null;
        return new RootServerTreeNode().deserialize(res);
    }
}


