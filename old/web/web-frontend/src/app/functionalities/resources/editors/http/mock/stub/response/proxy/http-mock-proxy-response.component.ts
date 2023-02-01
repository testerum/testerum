import {Component} from '@angular/core';
import {HttpMockService} from "../../http-mock.service";
import {HttpMockProxyResponse} from "../../model/response/http-mock-proxy-response.model";

@Component({
    moduleId: module.id,
    selector: 'http-mock-proxy-response',
    templateUrl: 'http-mock-proxy-response.component.html',
    styleUrls: ["http-mock-proxy-response.component.scss"]
})
export class HttpMockProxyResponseComponent {

    constructor(private httpMockService:HttpMockService) {
    }

    getModel(): HttpMockProxyResponse {
        return this.httpMockService.httpMock.proxyResponse
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }
}
