import {Component, Input} from '@angular/core';
import {StringUtils} from "../../../../../../../../utils/string-utils.util";
import {HttpMockRequestHeadersList} from "./model/http-mock-request-headers-list.model";
import {HttpMockRequestHeader} from "../../model/request/http-mock-request-header.model";
import {HttpMockService} from "../../http-mock.service";
import {HttpMockRequestHeadersCompareMode} from "../../model/enums/http-mock-request-headers-compare-mode.enum";
import {ArrayUtil} from "../../../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'http-mock-request-header',
    templateUrl: 'http-mock-request-header.component.html',
    styleUrls: ["http-mock-request-header.component.scss"]
})
export class HttpMockRequestHeaderComponent {

    @Input() headers: Array<HttpMockRequestHeader> = [];
    suggestionHeaders: any[];

    HttpMockRequestHeadersCompareMode = HttpMockRequestHeadersCompareMode;

    constructor(private httpMockService: HttpMockService) {
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode
    }

    onNewKeyChange(value: any, other: any) {
        this.checkNewKeyValueComplitness();
    }

    onNewValueChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    getCompareModes(): Array<HttpMockRequestHeadersCompareMode> {
        return HttpMockRequestHeadersCompareMode.enums;
    }

    onChangeCompareMode(header: HttpMockRequestHeader, compareMode: HttpMockRequestHeadersCompareMode) {
        header.compareMode = compareMode;

        if(compareMode == HttpMockRequestHeadersCompareMode.ABSENT) {
            header.value = null
        }
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.headers[this.headers.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.headers.push(
                new HttpMockRequestHeader()
            );
        }
    }

    filterSuggestionHeaders(event: any) {
        this.suggestionHeaders = [];
        for (let i = 0; i < HttpMockRequestHeadersList.headers.length; i++) {
            let header = HttpMockRequestHeadersList.headers[i];
            if (header.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
                this.suggestionHeaders.push(header);
            }
        }
    }

    deleteHeader(header: HttpMockRequestHeader) {
        ArrayUtil.removeElementFromArray(this.headers, header);
    }
}
