import {Component} from '@angular/core';
import {HttpMockService} from "../../../http-mock.service";
import {HttpMockResponseBody} from "../../../model/response/http-mock-response-body.model";
import {HttpMockResponseBodyType} from "../../../model/enums/http-mock-response-body-type.enum";
import {HttpMockResponse} from "../../../model/response/http-mock-response.model";

@Component({
    moduleId: module.id,
    selector: 'http-mock-response-body',
    templateUrl: 'http-mock-response-body.component.html',
    styleUrls: [
        'http-mock-response-body.component.scss',
        '../../../../../../../../../generic/css/generic.scss',
        '../../../../../../../../../generic/css/forms.scss'
    ]
})

export class HttpMockResponseBodyComponent {

    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    constructor(private httpMockService:HttpMockService) {
    }

    getModel(): HttpMockResponseBody {
        return this.httpMockService.httpMock.mockResponse.body;
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }

    getHttpMockResponseBodyTypes(): Array<HttpMockResponseBodyType> {
        return HttpMockResponseBodyType.enums;
    }

    bodyTypeChanged(event: HttpMockResponseBodyType) {
        let response: HttpMockResponse = this.httpMockService.httpMock.mockResponse;
        response.setContentTypeHeader(event);

    }
}
