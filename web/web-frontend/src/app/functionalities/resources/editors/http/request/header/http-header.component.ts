import {
    AfterViewInit, Component, DoCheck, ElementRef, Input, OnChanges, OnInit, Renderer,
    Renderer2
} from '@angular/core';
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {HttpRequestHeader} from "../../../../../../model/resource/http/http-request-header.model";
import {HeadersList} from "./model/headers-list.model";
import {ObjectUtils} from "primeng/components/utils/objectutils";
import {ObjectUtil} from "../../../../../../utils/object.util";
import {HttpRequestService} from "../http-request.service";
import {ArrayUtil} from "../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'http-header',
    templateUrl: 'http-header.component.html',
    styleUrls: ["http-header.component.css"]
})

export class HttpHeaderComponent implements OnInit {

    @Input() headers: Array<HttpRequestHeader> = [];
    suggestionHeaders: any[];

    constructor(private httpCallService:HttpRequestService) {
    }

    ngOnInit(): void {
        if (this.headers.length == 0) {
            this.headers.push(
                new HttpRequestHeader()
            );
        }
    }

    isEditMode(): boolean {
        return this.httpCallService.editMode
    }

    onNewKeyChange(value: any, other:any) {
        this.checkNewKeyValueComplitness();
    }
    onNewValueChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.headers[this.headers.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if(StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.headers.push(
                new HttpRequestHeader()
            );
        }
    }

    filterSuggestionHeaders(event: any) {
        this.suggestionHeaders = [];
        for(let i = 0; i < HeadersList.headers.length; i++) {
            let header = HeadersList.headers[i];
            if(header.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
                this.suggestionHeaders.push(header);
            }
        }
    }

    deleteHeader(header: HttpRequestHeader) {
        ArrayUtil.removeElementFromArray(this.headers, header);
    }
}
