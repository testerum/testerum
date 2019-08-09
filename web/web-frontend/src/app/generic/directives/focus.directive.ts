import {AfterViewInit, Directive, ElementRef, Input, OnChanges, OnInit, SimpleChanges} from "@angular/core";

@Directive({
    selector: '[focus]'
})
export class FocusDirective implements AfterViewInit, OnChanges {
    @Input() focus: boolean;
    private element: HTMLElement;

    constructor($element: ElementRef) {
        this.element = $element.nativeElement;
    }

    ngAfterViewInit(): void {
        if (this.focus) {
            FocusDirective.focusElement(this.element);
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        const focus = changes.focus;
        if (
            focus.currentValue !== focus.previousValue &&
            focus.currentValue === true
        ) {
            FocusDirective.focusElement(this.element);
        }
    }

    static focusElement(element: HTMLElement): void {
        if(element.tagName.toLowerCase() == 'input') {
            element.focus();
        } else {
            let inputElements = element.querySelectorAll('input');
            if (inputElements != null && inputElements.length > 0) {
                inputElements[0].focus()
            }
        }
    }
}
