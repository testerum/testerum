import {Component, OnInit} from '@angular/core';
import {HttpMockService} from "../../http-mock.service";
import {HttpMockFaultResponse} from "../../model/enums/http-mock-fault-response.enum";
import {HttpMock} from "../../model/http-mock.model";

@Component({
    moduleId: module.id,
    selector: 'http-mock-fault-response',
    templateUrl: 'http-mock-fault-response.component.html'
})

export class HttpMockFaultResponseComponent {

    HttpMockFaultResponse = HttpMockFaultResponse;

    constructor(private httpMockService:HttpMockService) {
    }

    getModel(): HttpMock {
        return this.httpMockService.httpMock
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }
}
