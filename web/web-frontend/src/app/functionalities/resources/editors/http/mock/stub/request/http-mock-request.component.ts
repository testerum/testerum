import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {HttpMockService} from "../http-mock.service";
import {HttpMockRequestMethod} from "../model/enums/http-mock-request-method";
import {HttpMockRequest} from "../model/request/http-mock-request.model";
import {CollapsablePanelComponent} from "../../../../../../../generic/components/panels/collapsable-panel/collapsable-panel.component";

@Component({
    moduleId: module.id,
    selector: 'http-mock-request',
    templateUrl: 'http-mock-request.component.html',
    styleUrls: [
        'http-mock-request.component.scss',
        '../../../../../../../generic/css/generic.scss',
        '../../../../../../../generic/css/forms.scss'
    ]
})

export class HttpMockRequestComponent implements AfterViewInit {

    HttpMockRequestMethod = HttpMockRequestMethod;

    @ViewChild("queryParamPanel") queryParamPanel: CollapsablePanelComponent;
    @ViewChild("headersPanel") headersPanel: CollapsablePanelComponent;
    @ViewChild("bodyPanel") bodyPanel: CollapsablePanelComponent;
    @ViewChild("scenarioPanel") scenarioPanel: CollapsablePanelComponent;

    constructor(private httpMockService:HttpMockService) {
    }

    ngAfterViewInit(): void {
        if(!this.getModel().hasEmptyParams()) {
            this.queryParamPanel.expand();
        }

        if(!this.getModel().hasEmptyHeaders()) {
            this.headersPanel.expand();
        }

        if(!this.getModel().hasEmptyBody()) {
            this.bodyPanel.expand();
        }

        if(!this.getModel().hasEmptyScenario()) {
            this.scenarioPanel.expand();
        }
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }

    getModel(): HttpMockRequest {
        return this.httpMockService.httpMock.expectedRequest;
    }

    getHttpMethods(): Array<HttpMockRequestMethod> {
        return HttpMockRequestMethod.enums;
    }

    methodChange(value: HttpMockRequestMethod) {
        this.getModel().method = value;
    }

}
