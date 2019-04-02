import {EMPTY, Observable} from 'rxjs';

import {tap} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {UrlService} from "../url.service";
import {StringUtils} from "../../utils/string-utils.util";

@Injectable()
export class NotFoundHttpInterceptor implements HttpInterceptor {

    constructor(private urlService: UrlService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(tap(
            (event: HttpEvent<any>) => {},
            (err: any) => {

                if (err instanceof HttpErrorResponse) {
                    let httpErrorResponse: HttpErrorResponse = err;
                    if (httpErrorResponse.status == 404) {
                        let url = new URL(httpErrorResponse.url);
                        let resourcePath = url.searchParams.get("path");
                        let resourceId = resourcePath ? StringUtils.substringBefore(resourcePath, ".") : null;
                        let resourceType = resourcePath ? StringUtils.substringAfter(resourcePath, ".") : null;
                        this.urlService.navigateToNotFoundPage(window.location.href, resourceId, resourceType);
                    }
                }

                return EMPTY;
            }
        ));
    }
}
