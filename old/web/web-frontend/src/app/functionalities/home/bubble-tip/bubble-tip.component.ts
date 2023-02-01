import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
    selector: 'bubble-tip',
    templateUrl: './bubble-tip.component.html',
    styleUrls: ['./bubble-tip.component.scss'],
    animations: [
        trigger('fade', [
            transition('void => *', [
                style({opacity: 0, 'display': 'inline-block'}),
                animate(3000, style({opacity: 0})),
                animate(2000, style({opacity: 1})),
            ])
        ])
    ]
})
export class BubbleTipComponent {

    show: boolean = true;

    onClose() {
        this.show = false;
    }
}
