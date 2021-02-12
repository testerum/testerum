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
    Renderer2,
    SimpleChanges,
    ViewChild
} from '@angular/core';
import {filter, take} from 'rxjs/operators';
import {editor} from 'monaco-editor';

import {MonacoEditorLoaderService} from '../../services/monaco-editor-loader.service';
import IDimension = editor.IDimension;

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
	height: 100%;
}`
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonacoEditorComponent implements OnInit, OnChanges, OnDestroy {
    @Input() options: editor.IStandaloneEditorConstructionOptions;
    @Input() value: string;
    @Input() parentElemntRef: HTMLElement;
    @Input() autoSizing: boolean = true;
    @Input() minHeight: number = 400;
    @Output() valueChange = new EventEmitter<string>();

    @ViewChild('editor', { static: true }) editorContent: ElementRef;

    container: HTMLDivElement;
    editor: editor.IStandaloneCodeEditor;

    latestChangedValue: string;

    constructor(private monacoLoader: MonacoEditorLoaderService,
                private renderer: Renderer2) { }

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
        if (this.editor && changes['value'] != null && this.value != this.latestChangedValue) {
            if (this.value) {
                this.editor.setValue(this.value)
            } else {
                this.editor.setValue("")
            }
        }
    }

    private initMonaco() {

        let opts: editor.IStandaloneEditorConstructionOptions = {
            value: [this.value].join('\n'),
            language: 'text',
            theme: 'vc',
            automaticLayout: true,
            scrollbar: {
                handleMouseWheel: false,
                vertical: 'hidden',
                horizontal: 'auto',
            },
            scrollBeyondLastLine: false,
            minimap: {
                enabled: false,
            },
            lineNumbersMinChars: 3,
            fontFamily: "'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace",
            fontSize: 12
        };

        if (this.options) {
            opts = Object.assign({}, opts, this.options);
        }

        let theme: editor.IStandaloneThemeData = {
            base: 'vs',
            inherit: true,
            rules: [],
            colors: {
                'editorLineNumber.foreground': '#C0BEBC',
                'editorGutter.background': '#f0f0f0'
            }
        };
        monaco.editor.defineTheme('myTheme', theme);

        this.editor = monaco.editor.create(this.container, opts);
        this.editor.getModel().updateOptions(
            {
                indentSize: 2,
                insertSpaces: true,
                tabSize: 2
            });
        // this.editor.layout();

        monaco.editor.setTheme('myTheme');

        this.editor.onDidChangeModelContent(() => {
            this.latestChangedValue = this.editor.getValue()
            this.valueChange.emit(this.latestChangedValue);
            this.refresh();
        });

        this.editor.onDidBlurEditorText(() => {
            this.valueChange.emit(this.editor.getValue());
            this.refresh()
        });

        this.editor.onDidChangeCursorPosition( e => {
            setTimeout(this.scrollCursorInView,100, this.parentElemntRef);
        });

        this.refresh();
    }

    private scrollCursorInView(parentElement: HTMLElement) {
        let element = parentElement.querySelector(".cursor");
        element.scrollIntoView({block: "nearest", inline: "nearest"});
    }

    onResized(event) {
        // if (this.editor) {
        //     this.editor.layout({
        //         width: event.newWidth,
        //         height: event.newHeight
        //     });
        // }
    }

    ngOnDestroy() {
        if (this.editor) {
            this.editor.dispose();
        }
    }

    refresh(): void {
        this.autoresize();
    }

    resizeToFit(): void {
        this.autoresize();
    }

    setSize(dimension: IDimension): void {
        this.resize(dimension);
    }

    protected autoresize() {
        if (this.autoSizing) {
            this.resize(null);
        }
    }

    protected resize(dimension: IDimension | null): void {
        let elemToResize = this.parentElemntRef;
        if (elemToResize) {
            const layoutSize = this.computeLayoutSize(elemToResize , dimension);
            this.parentElemntRef.style.height = ""+layoutSize.height+"px";
            this.editor.layout();
        }
    }

    protected computeLayoutSize(hostNode: HTMLElement, dimension: IDimension | null): IDimension {
        if (dimension && dimension.width >= 0 && dimension.height >= 0) {
            return dimension;
        }
        const width = (!dimension || dimension.width < 0) ?
            this.getWidth(hostNode) :
            dimension.width;

        const height = (!dimension || dimension.height < 0) ?
            this.getHeight(hostNode) :
            dimension.height;

        return { width: width, height: height };
    }

    protected getWidth(hostNode: HTMLElement): number {
        return hostNode.offsetWidth;
    }

    protected getHeight(hostNode: HTMLElement): number {
        if (!this.autoSizing) {
            return hostNode.offsetHeight;
        }

        const contentHeight = this.editor.getContentHeight();

        const horizontalScrollbarHeight = this.editor.getLayoutInfo().horizontalScrollbarHeight;

        const editorHeight = contentHeight + horizontalScrollbarHeight;
        if (this.minHeight < 0) {
            return editorHeight;
        }
        const defaultHeight = this.minHeight + horizontalScrollbarHeight;
        return Math.max(defaultHeight, editorHeight);
    }
}







