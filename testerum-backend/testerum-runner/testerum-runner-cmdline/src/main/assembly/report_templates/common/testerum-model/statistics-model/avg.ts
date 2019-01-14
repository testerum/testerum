export class Avg {

    constructor(public readonly sum: number,
                public readonly count: number) { }

    static parse(input: Object): Avg {
        if (!input) {
            return null;
        }

        const sum = input["sum"] as number;
        const count = input["count"] as number;

        return new Avg(sum, count);
    }

}
