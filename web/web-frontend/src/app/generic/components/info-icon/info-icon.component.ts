import {ChangeDetectorRef, Component, ElementRef, Input, ViewChild, ViewEncapsulation} from '@angular/core';
import {FlowUtil} from "../../../utils/flow.util";
import {OverlayPanel} from "primeng/overlaypanel";

@Component({
    selector: 'info-icon',
    templateUrl: './info-icon.component.html',
    styleUrls: ['./info-icon.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class InfoIconComponent {

    @Input() message: string;
    @Input() position: string = 'auto';
    @Input() maxWidth: string = '600px';
    @Input() maxHeight: string = '400px';
    @Input() width: string;
    @Input() height: string;
    @Input() iconFontSize: string = '1.3em';

    @ViewChild(OverlayPanel, { static: true }) overlayPanel: OverlayPanel;
    @ViewChild("actualTarget", { static: true }) actualTargetElement: ElementRef;

    private isVisible: boolean = false;
    private shouldHide: boolean = false;
    constructor(private cd: ChangeDetectorRef) {}

    show(event: any) {
        this.shouldHide = false;

        if (!this.isVisible) {
            this.isVisible = true;
            this.overlayPanel.show(event, this.actualTargetElement.nativeElement);
        }

    }

    waitAndHide(event: any) {
        this.shouldHide = true;

        (async () => {
            await FlowUtil.delay(250);
            if (this.isVisible && this.shouldHide) {
                this.overlayPanel.hide();
                this.isVisible = false;

                this.refresh();
            }
        })();
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }
}
