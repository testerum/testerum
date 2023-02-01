import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {StringUtils} from "../../../../../utils/string-utils.util";
import {DateUtil} from "../../../../../utils/date.util";
import {ResourceContextActions} from "../../infrastructure/model/resource-context-actions.model";

@Component({
    selector: 'date-input-basic-resource',
    templateUrl: './date-input-basic-resource.component.html',
    styleUrls: ['./date-input-basic-resource.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DateInputBasicResourceComponent implements OnInit {


    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() contextActions: ResourceContextActions;

    date: Date;
    inputDate: string;

    ngOnInit(): void {
        if (StringUtils.isNotEmpty(this.model.content)) {
            this.date = DateUtil.isoDateStringToDate(this.model.content);
            this.inputDate = DateUtil.dateToIsoDateString(this.date);
        }
    }

    onDateSelect(event: Date) {
        this.date = event;
        this.inputDate = DateUtil.dateToIsoDateString(this.date);
        this.model.content = DateUtil.dateToIsoDateString(this.date);
    }

    inputChange(event: any) {
        this.inputDate = event;
        this.date = DateUtil.isoDateStringToDate(event);
        this.model.content = DateUtil.dateToIsoDateString(this.date);
    }

    getDescription(): string {
        return "You can specify a date using the following format:\n" +
            "<code>yyyy-MM-ddTHH:mm:ss.SSSZ</code>\n" +
            "<br/>" +
            "mm - month\n" +
            "dd - day\n" +
            "yyyy - year\n" +
            "hh - hour\n" +
            "mm - minutes\n" +
            "ss - seconds\n" +
            "SSS - milliseconds\n" +
            "<br/>" +
            "example: <code>1981-10-31T04:17:40.000Z</code>\n" +
            "<br/>" +
            "You can also use variables from the context.\n" +
            "e.g.: <code>{{variable_name}}</code>"
    }
}
