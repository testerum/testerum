import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {ContextService} from "../context.service";

@Injectable()
export class AuthenticationHttpInterceptor implements HttpInterceptor {

    constructor(private contextService: ContextService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        if (this.contextService.isLoggedIn()) {
            request = request.clone({
                setHeaders: {
                    'X-Testerum-Auth': this.contextService.getAuthToken()
                }
            });
        }

        return next.handle(request);
    }
}
