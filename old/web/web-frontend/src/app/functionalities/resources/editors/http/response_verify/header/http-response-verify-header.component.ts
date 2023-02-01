import {Component, Input, ViewEncapsulation} from '@angular/core';
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {HttpResponseVerifyHeadersList} from "./model/http-response-verify-headers-list.model";
import {HttpResponseVerifyService} from "../http-response-verify.service";
import {HttpResponseHeaderVerify} from "../model/http-response-header-verify.model";
import {HttpResponseVerifyHeadersCompareMode} from "../model/enums/http-response-verify-headers-compare-mode.enum";
import {ArrayUtil} from "../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'http-response-verify-header',
    templateUrl: 'http-response-verify-header.component.html',
    styleUrls: ["http-response-verify-header.component.scss"],
    encapsulation: ViewEncapsulation.None
})
export class HttpResponseVerifyHeaderComponent {

    @Input() headers: Array<HttpResponseHeaderVerify> = [];
    suggestionHeaders: any[];

    constructor(private httpResponseVerifyService: HttpResponseVerifyService) {
    }

    isEditMode(): boolean {
        return this.httpResponseVerifyService.editMode
    }

    onNewKeyChange(value: any, other: any) {
        this.checkNewKeyValueComplitness();
    }

    onNewValueChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    getHttpResponseVerifyHeadersCompareModes(): Array<HttpResponseVerifyHeadersCompareMode> {
        return HttpResponseVerifyHeadersCompareMode.enums;
    }

    onChangeCompareMode(header: HttpResponseHeaderVerify, compareMode: HttpResponseVerifyHeadersCompareMode) {
        header.compareMode = compareMode;
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.headers[this.headers.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.headers.push(
                new HttpResponseHeaderVerify()
            );
        }
    }

    filterSuggestionHeaders(event: any) {
        this.suggestionHeaders = [];
        for (let i = 0; i < HttpResponseVerifyHeadersList.headers.length; i++) {
            let header = HttpResponseVerifyHeadersList.headers[i];
            if (header.toLowerCase().indexOf(event.query.toLowerCase()) >= 0) {
                this.suggestionHeaders.push(header);
            }
        }
    }

    deleteHeader(header: HttpResponseHeaderVerify) {
        ArrayUtil.removeElementFromArray(this.headers, header);
    }
}
