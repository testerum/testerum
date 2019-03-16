import {Component, ElementRef, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {OverlayPanel} from "primeng/primeng";
import {FlowUtil} from "../../../utils/flow.util";

@Component({
    selector: 'info-icon',
    templateUrl: './info-icon.component.html',
    styleUrls: ['./info-icon.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class InfoIconComponent implements OnInit {

    @Input() message: string;
    @Input() position: string = 'auto';
    @Input() maxWidth: string = '600px';
    @Input() maxHeight: string = '400px';
    @Input() width: string;
    @Input() height: string;

    @ViewChild(OverlayPanel) overlayPanel: OverlayPanel ;
    @ViewChild("actualTarget") actualTargetElement: ElementRef;

    private shouldShow: boolean;
    ngOnInit() {
    }

    show(event: any) {
        this.shouldShow = true;
        this.overlayPanel.show(event, this.actualTargetElement.nativeElement);
    }

    waitAndHide(event: any) {
        this.shouldShow = false;

        (async () => {
            await FlowUtil.delay(250);
            if (!this.shouldShow) {
                this.overlayPanel.hide()
            }
        })();
    }
}
