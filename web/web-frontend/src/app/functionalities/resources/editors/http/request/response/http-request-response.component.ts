import {Component, OnInit} from '@angular/core';
import {HttpRequestService} from "../http-request.service";

@Component({
    moduleId: module.id,
    selector: 'http-request-response',
    templateUrl: 'http-request-response.component.html',
    styleUrls: ['http-request-response.component.scss']
})

export class HttpResponseComponent implements OnInit {

    httpCallService:HttpRequestService;

    constructor(httpCallService: HttpRequestService) {
        this.httpCallService = httpCallService;
    }

    ngOnInit() {
    }
}
