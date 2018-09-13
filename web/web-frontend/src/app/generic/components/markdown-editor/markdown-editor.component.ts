import {
    AfterViewInit,
    Component,
    ElementRef,
    Input,
    OnChanges,
    SimpleChanges,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import * as SimpleMDE from 'simplemde'

@Component({
    selector: 'markdown-editor',
    templateUrl: 'markdown-editor.component.html',
    styleUrls: ['markdown-editor.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class MarkdownEditorComponent implements AfterViewInit {

    private value: string; //did this with setter and getters because binding this value on update is not working

    @Input() options: any;
    @Input() editMode: boolean = true;
    oldEditMode: boolean = true;

    @ViewChild('descriptionArea') textarea: ElementRef;
    simpleMDE: SimpleMDE;

    setValue(value: string): void {
        if (this.simpleMDE) {
            if (!this.editMode) {
                this.setEditMode(true);
                this.simpleMDE.value(value ? value : "");
                this.setEditMode(false);
            } else {
                this.simpleMDE.value(value ? value : "");
            }
        }
        this.value = value;
    }

    getValue(): string {
        return this.simpleMDE.value();
    }

    ngAfterViewInit(): void {
        this.initMarkdownEditor()
    }

    initMarkdownEditor() {
        const config = {
            ...this.options,
            element: this.textarea.nativeElement
        };
        this.simpleMDE = new SimpleMDE(config);

        this.simpleMDE.value(this.value);

        this.handleEditModeChanged();
    }

    setEditMode(value: boolean) {
        this.editMode = value;
        this.handleEditModeChanged()
    }

    private handleEditModeChanged() {
        if (this.editMode != this.oldEditMode) {
            if (this.simpleMDE) {
                this.simpleMDE.togglePreview();
                this.simpleMDE.gui.toolbar.hidden = !this.editMode;
                this.oldEditMode = this.editMode;
            }
        }
    }
}
