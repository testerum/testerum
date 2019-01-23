import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {ContextService} from "../context.service";

@Injectable()
export class MultiProjectHttpInterceptor implements HttpInterceptor {


    constructor(private contextService: ContextService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        if (this.contextService.isProjectSelected()) {
            request = request.clone({
                setHeaders: {
                    'X-Testerum-Project': this.contextService.getProjectPath()
                }
            });
        }

        return next.handle(request);
    }
}
