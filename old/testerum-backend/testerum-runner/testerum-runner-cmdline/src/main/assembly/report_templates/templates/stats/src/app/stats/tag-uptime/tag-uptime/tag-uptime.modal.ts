
export class TagUptime {
    readonly tag: string;
    readonly uptime: number;

    constructor(tag: string, uptime: number) {
        this.tag = tag;
        this.uptime = uptime;
    }
}
