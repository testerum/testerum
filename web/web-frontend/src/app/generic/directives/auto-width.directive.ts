import {AfterContentChecked, Directive, ElementRef, HostListener, Input} from "@angular/core";

@Directive({
    selector: '[auto-width]'
})
export class AutoWidthDirective {

    @Input() minSize: number = 3;

    constructor(private el: ElementRef) {
    }

    @HostListener('keyup') onKeyUp() {
        this.resize();
    }

    @HostListener('focus') onFocus() {
        this.resize();
    }

    private resize() {
        let size = this.el.nativeElement.value.length;
        size = this.minSize < size ? size : this.minSize
        this.el.nativeElement.setAttribute('size', size);
    }
}
