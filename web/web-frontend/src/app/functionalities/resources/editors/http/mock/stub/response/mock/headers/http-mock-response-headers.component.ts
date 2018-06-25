import {Component, Input} from '@angular/core';
import {HttpMockService} from "../../../http-mock.service";
import {HttpMockResponseHeader} from "../../../model/response/http-mock-response-header.model";
import {StringUtils} from "../../../../../../../../../utils/string-utils.util";
import {HttpResponseVerifyHeadersList} from "../../../../../response_verify/header/model/http-response-verify-headers-list.model";
import {ArrayUtil} from "../../../../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'http-mock-response-headers',
    templateUrl: 'http-mock-response-headers.component.html',
    styleUrls: ["http-mock-response-headers.component.css"]
})

export class HttpMockResponseHeadersComponent {

    @Input() headers: Array<HttpMockResponseHeader> = [];
    suggestionHeaders: any[];

    constructor(private httpMockService:HttpMockService) {
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }

    onNewKeyChange(value: any, other: any) {
        this.checkNewKeyValueComplitness();
    }

    onNewValueChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.headers[this.headers.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.headers.push(
                new HttpMockResponseHeader()
            );
        }
    }

    filterSuggestionHeaders(event: any) {
        this.suggestionHeaders = [];
        for (let i = 0; i < HttpResponseVerifyHeadersList.headers.length; i++) {
            let header = HttpResponseVerifyHeadersList.headers[i];
            if (header.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
                this.suggestionHeaders.push(header);
            }
        }
    }

    deleteHeader(header: HttpMockResponseHeader) {
        ArrayUtil.removeElementFromArray(this.headers, header);
    }

}
