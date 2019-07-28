import {
    ChangeDetectionStrategy,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild
} from '@angular/core';
import {filter, take} from 'rxjs/operators';
import {editor} from 'monaco-editor';

import {MonacoEditorLoaderService} from '../../services/monaco-editor-loader.service';

declare const monaco: any;

@Component({
    selector: 'monaco-editor',
    template: `<div #container materiaResized (resized)="onResized($event)" class="editor-container" fxFlex>
	<div class="wrapper">
		<div
			#editor
			class="monaco-editor"
			[style.width.px]="container.offsetWidth"
			[style.height.px]="container.offsetHeight" style="min-width: 0;"
		></div>
	</div>
</div>`,
    styles: [
        `:host {
	flex: 1;
	box-sizing: border-box;
	flex-direction: column;
	display: flex;
	overflow: hidden;
	max-width: 100%;
	min-wdith: 0;
}
.wrapper {
	width: 0px; height: 0px;
}
.editor-container {
	text-overflow: ellipsis;
	overflow: hidden;
	position: relative;
	min-width: 0;
	display: table;
	width: 100%;
	height: 100%;
}`
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonacoEditorComponent implements OnInit, OnChanges, OnDestroy {
    @Input() options: editor.IEditorConstructionOptions;
    @Input() value: string;
    @Output() valueChange = new EventEmitter<string>();

    @ViewChild('editor') editorContent: ElementRef;

    container: HTMLDivElement;
    editor: editor.IStandaloneCodeEditor;

    constructor(private monacoLoader: MonacoEditorLoaderService) { }

    ngOnInit() {
        this.container = this.editorContent.nativeElement;
        this.monacoLoader.isMonacoLoaded.pipe(
            filter(isLoaded => isLoaded),
            take(1)
        ).subscribe(() => {
            this.initMonaco();
        });
    }

    ngOnChanges(changes: SimpleChanges) {
        if (this.editor && changes.options && !changes.options.firstChange) {
            if (changes.options.previousValue.language !== changes.options.currentValue.language) {
                monaco.editor.setModelLanguage(
                    this.editor.getModel(),
                    this.options && this.options.language ? this.options.language : 'text'
                );
            }
            if (changes.options.previousValue.theme !== changes.options.currentValue.theme) {
                monaco.editor.setTheme(changes.options.currentValue.theme);
            }
            if (changes.options.previousValue.readOnly !== changes.options.currentValue.readOnly) {
                this.editor.updateOptions({ readOnly: changes.options.currentValue.readOnly });
            }
        }
    }

    private initMonaco() {
        let opts: editor.IEditorConstructionOptions = {
            value: [this.value].join('\n'),
            language: 'text',
            automaticLayout: true,
            scrollBeyondLastLine: false,
            theme: 'vc'
        };

        if (this.options) {
            opts = Object.assign({}, opts, this.options);
        }

        this.editor = monaco.editor.create(this.container, opts);

        this.editor.layout();

        this.editor.onDidChangeModelContent(() => {
            this.valueChange.emit(this.editor.getValue())
        });

        this.editor.onDidBlurEditorText(() => {
            this.valueChange.emit(this.editor.getValue())
        });
    }

    onResized(event) {
        if (this.editor) {
            this.editor.layout({
                width: event.newWidth,
                height: event.newHeight
            });
        }
    }

    ngOnDestroy() {
        if (this.editor) {
            this.editor.dispose();
        }
    }
}
