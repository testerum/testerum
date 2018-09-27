import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';

@Component({
    selector: 'info-icon',
    templateUrl: './info-icon.component.html',
    styleUrls: ['./info-icon.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class InfoIconComponent implements OnInit {

    @Input() message: string;
    @Input() position: string = 'auto';

    ngOnInit() {
    }
}
