import {Component, Input} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
    moduleId: module.id,
    selector: 'collapsable-panel',
    templateUrl: 'collapsable-panel.component.html',
    styleUrls: ["collapsable-panel.component.scss"],
    animations: [
        trigger('expandCollapse', [
            state('open', style({
                'height': '*'
            })),
            state('close', style({
                'height': '0px'
            })),
            transition('open => close', animate('400ms ease-in-out')),
            transition('close => open', animate('400ms ease-in-out'))
        ])
    ]
})

export class CollapsablePanelComponent {

    @Input() title: string;
    @Input() heading: number = 1;
    collapsed: boolean = false;
    animationState: string = 'open';

    @Input('collapsed')
    set setCollapsed(value: boolean) {
        if (this.collapsed != value) {
            this.collapsed = value;
            this.animationState = this.collapsed ? 'close' : 'open';
        }
    }

    animate() {
        if(this.animationState == "close") {
            this.animationState = "open";
        } else {
            this.animationState = "close";
        }
    }

    collapse() {
        this.collapsed = true;
        this.animationState = "close";
    }

    expand() {
        this.collapsed = false;
        this.animationState = "open";
    }

    onAnimateEnd() {
    }
}
