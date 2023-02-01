import {SelectItem} from "primeng/api";

export class StringSelectItem implements SelectItem {
    disabled: boolean;
    icon: string;
    label: string;
    styleClass: string;
    title: string;
    value: any;

    constructor(label: string, value: string = null, disabled: boolean = null, icon: string = null, styleClass: string = null, title: string= null) {
        this.label = label;
        value ? this.value = value : this.value = label;

        this.disabled = disabled;
        this.icon = icon;
        this.styleClass = styleClass;
        this.title = title;
    }
}
