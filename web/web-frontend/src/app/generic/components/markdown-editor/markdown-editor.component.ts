import {AfterViewInit, Component, ElementRef, Input, ViewChild} from '@angular/core';
import * as SimpleMDE from 'simplemde'

@Component({
    selector: 'markdown-editor',
    templateUrl: 'markdown-editor.component.html',
    styleUrls: ['markdown-editor.component.scss']
})

export class MarkdownEditorComponent implements AfterViewInit {


    private _value: string;

    @Input()
    set value(value: string) {
        if (this.simpleMDE) {
            if (!this.editMode) {
                this.setEditMode(true);
                this.simpleMDE.value(value?value:"");
                this.setEditMode(false);
            } else {
                this.simpleMDE.value(value?value:"");
            }
        } else {
            this._value = value;
        }
    }
    get value(): string {
        return this.simpleMDE.value();
    }

    @Input() options: any;

    @ViewChild('descriptionArea') textarea: ElementRef;
    simpleMDE: SimpleMDE;

    editMode: boolean = true;
    oldEditMode: boolean = true;

    ngAfterViewInit(): void {
        this.initMarkdownEditor()
    }

    initMarkdownEditor() {

        const config = {
            ...this.options,
            element: this.textarea.nativeElement
        };
        this.simpleMDE = new SimpleMDE(config);

        this.simpleMDE.value(this._value);

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
