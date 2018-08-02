import {Component, OnInit} from '@angular/core';
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {HttpParam} from "./model/http-param.model";
import {HttpRequestService} from "../http-request.service";

@Component({
    moduleId: module.id,
    selector: 'http-params',
    templateUrl: 'http-params.component.html'
})

export class HttpParamsComponent implements OnInit {

    params: Array<HttpParam> = [];
    shouldShow:boolean = false;

    constructor(private httpCallService: HttpRequestService) {
    }

    ngOnInit(): void {
        this.refreshParamsFromUrl();
        this.addEmptyParam();
        this.httpCallService.editModeEventEmitter.subscribe((editMode:boolean) => {
            this.refreshParamsFromUrl()
        })
    }

    refreshParamsFromUrl() {
        let url = this.httpCallService.httpRequest.url;

        if(!url || !url.includes("?")) {
            this.params.length = 0;
            this.addEmptyParam();
            return;
        }

        let paramPartAsString = StringUtils.substringAfter(url.trim(), "?");
        let paramsAsStringWithEmpty: string[] = paramPartAsString.split("&");
        let paramsAsString: string[] = [];
        for (let paramWithEmpty of paramsAsStringWithEmpty) {
            if(paramWithEmpty) {
                paramsAsString.push(paramWithEmpty)
            }
        }

        for (let paramIndex = 0; paramIndex < paramsAsString.length; paramIndex++) {
            let paramAsString = paramsAsString[paramIndex];
            this.initParamFromString(paramAsString, paramIndex)
        }

        if(this.params.length > paramsAsString.length) {
            this.params.splice(
                paramsAsString.length,
                this.params.length - paramsAsString.length
            );
        }

        if (this.httpCallService.editMode) {
            this.addEmptyParam()
        }
    }

    private initParamFromString(paramAsString: string, paramIndex: number) {
        let key = StringUtils.substringBefore(paramAsString, "=");
        if(key == null) {
            key = paramAsString
        }

        let value = StringUtils.substringAfter(paramAsString, "=");

        let httpParam: HttpParam = null;
        if(this.params.length - 1 < paramIndex) {
            httpParam = new HttpParam();
            this.params.push(httpParam)
        } else {
            httpParam = this.params[paramIndex]
        }

        httpParam.key = key;
        httpParam.value = value;
    }

    private addEmptyParam() {
        this.params.push(
            new HttpParam()
        );
    }
    onNewKeyChange(event:any, index: number) {
        this.params[index].key = event;
        this.checkNewKeyValueComplitness();
        this.updateUrl()
    }

    onNewValueChange(event:any, index: number) {
        this.params[index].value = event;
        this.checkNewKeyValueComplitness();
        this.updateUrl()
    }

    updateUrl():void {
        let paramsString = this.getParamsString();

        let url = this.httpCallService.httpRequest.url;
        let urlDomainAndPathPart = StringUtils.substringBefore(url, "?");
        if(urlDomainAndPathPart == null) {
            urlDomainAndPathPart = url;
        }

        this.httpCallService.httpRequest.url = urlDomainAndPathPart + paramsString;
    }

    private getParamsString() {
        let result = "?";

        let isFirstParam = true;
        for (let param of this.params) {
            if(param.isEmpty()) {
                continue;
            }

            if(isFirstParam) {
                isFirstParam = false;
            } else {
                result += "&"
            }

            let key = param.key ? encodeURIComponent(param.key) : "";
            let value = param.value ? encodeURIComponent(param.value): "";
            result += key +"="+ value;
        }
        return result
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.params[this.params.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if(StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.addEmptyParam();
        }
    }

    show() {
        this.refreshParamsFromUrl();
        this.shouldShow = true;
    }

    hide() {
        this.shouldShow = false;
    }

    editMode(): boolean {
        return this.httpCallService.editMode
    }

    isEmpty(): boolean {
        for (let param of this.params) {
            if(!param.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
