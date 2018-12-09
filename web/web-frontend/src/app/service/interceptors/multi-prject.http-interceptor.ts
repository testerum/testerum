import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {ContextService} from "../context.service";

@Injectable()
export class MultiProjectHttpInterceptor implements HttpInterceptor {


    constructor(private contextService: ContextService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        if (this.contextService.project) {
            request = request.clone({
                setHeaders: {
                    Project: this.contextService.project.name
                }
            });
        }

        return next.handle(request);
    }
}
