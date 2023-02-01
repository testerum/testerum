import {Component, Input, ViewEncapsulation} from '@angular/core';
import {StringUtils} from "../../../../../../../../utils/string-utils.util";
import {HttpMockService} from "../../http-mock.service";
import {HttpMockRequestParam} from "../../model/request/http-mock-request-param.model";
import {HttpMockRequestParamsCompareMode} from "../../model/enums/http-mock-request-params-compare-mode.enum";
import {ArrayUtil} from "../../../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'http-mock-request-params',
    templateUrl: 'http-mock-request-params.component.html',
    styleUrls: ["http-mock-request-params.component.scss"],
    encapsulation: ViewEncapsulation.None
})
export class HttpMockRequestParamsComponent {

    @Input() params: Array<HttpMockRequestParam> = [];

    HttpMockRequestParamsCompareMode = HttpMockRequestParamsCompareMode;

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

    getCompareModes(): Array<HttpMockRequestParamsCompareMode> {
        return HttpMockRequestParamsCompareMode.enums;
    }

    onChangeCompareMode(param: HttpMockRequestParam, compareMode: HttpMockRequestParamsCompareMode) {
        param.compareMode = compareMode;

        if(compareMode == HttpMockRequestParamsCompareMode.ABSENT) {
            param.value = null
        }
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.params[this.params.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.params.push(
                new HttpMockRequestParam()
            );
        }
    }

    deleteParam(param: HttpMockRequestParam) {
        ArrayUtil.removeElementFromArray(this.params, param);
    }
}
