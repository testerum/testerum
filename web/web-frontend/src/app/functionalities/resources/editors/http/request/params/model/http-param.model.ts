

export class HttpParam {
    key: string;
    value: string;

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
