import {Component, Input, OnInit} from '@angular/core';
import {LogLevel} from "../../../../../model/test/event/enums/log-level.enum";

@Component({
    selector: 'log-line-collapsable',
    templateUrl: './log-line-collapsable.component.html',
    styleUrls: ['./log-line-collapsable.component.scss']
})
export class LogLineCollapsableComponent {

    @Input() mainMessage: string;
    @Input() collapsedMessage: string;
    @Input() logLevel: LogLevel;
    @Input() collapsed: boolean = true;

    LogLevel= LogLevel;

    toggle() {
        this.collapsed = !this.collapsed
    }
}
