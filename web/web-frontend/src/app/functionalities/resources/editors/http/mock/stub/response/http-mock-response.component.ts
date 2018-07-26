import {Component} from '@angular/core';
import {HttpMockService} from "../http-mock.service";
import {HttpResponseStatusCode} from "../../../../../../../model/resource/http/enum/http-response-status-code.enum";
import {HttpMockResponse} from "../model/response/http-mock-response.model";
import {HttpMockResponseType} from "../model/enums/http-mock-response-type.enum";

@Component({
    moduleId: module.id,
    selector: 'http-mock-response',
    templateUrl: 'http-mock-response.component.html',
    styleUrls: [
        'http-mock-response.component.scss',
        '../../../../../../../generic/css/generic.scss',
        '../../../../../../../generic/css/forms.scss'
    ]
})

export class HttpMockResponseComponent {

    HttpMockResponseType = HttpMockResponseType;

    constructor(private httpMockService:HttpMockService) {
    }

    getModel(): HttpMockResponse {
        return this.httpMockService.httpMock.mockResponse;
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }

    getSelectedResponse(): HttpMockResponseType {
        return this.httpMockService.httpMock.selectedResponse;
    }

    display(selectedTab: HttpMockResponseType) {
        if(!this.isEditMode()) {
            return;
        }
        this.httpMockService.httpMock.selectedResponse = selectedTab;
    }

    getStatusCodeText(): string {
        let httpResponseStatusCode = HttpResponseStatusCode.fromStatusCodeNumber(
            this.getModel().statusCode
        );

        if(httpResponseStatusCode != null) {
            return httpResponseStatusCode.toString()
        }
        return "";
    }
}
