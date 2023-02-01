import {Component, Input, OnInit} from '@angular/core';
import {HttpRequestBodyTypeForm} from "../../../../../../model/resource/http/http-request-body-type-form.model";
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {ObjectUtil} from "../../../../../../utils/object.util";
import {ArrayUtil} from "../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'http-body-type-form',
    templateUrl: 'http-body-type-form.component.html',
    styleUrls: ['http-body-type-form.component.scss']
})

export class HttpBodyTypeFormComponent implements OnInit {

    @Input() bodyTypeForms: Array<HttpRequestBodyTypeForm> = [];
    @Input() editMode: boolean;

    ngOnInit(): void {
        if (this.bodyTypeForms.length == 0) {
            this.bodyTypeForms.push(
                new HttpRequestBodyTypeForm()
            );
        }
    }

    onNewKeyChange() {
        this.checkNewKeyValueComplitness();
    }
    onNewValueChange() {
        this.checkNewKeyValueComplitness();
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.bodyTypeForms[this.bodyTypeForms.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if(StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.bodyTypeForms.push(
                new HttpRequestBodyTypeForm()
            );
        }
    }

    deleteFormEntry(formEntry: HttpRequestBodyTypeForm) {
        ArrayUtil.removeElementFromArray(this.bodyTypeForms, formEntry);
    }
}
