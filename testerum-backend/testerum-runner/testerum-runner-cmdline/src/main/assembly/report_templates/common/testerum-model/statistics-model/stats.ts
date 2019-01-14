import {StatsAll} from "./stats-all";
import {MarshallingUtils} from "../json-marshalling/marshalling-utils";

export class Stats {

    constructor(public readonly perDay: Map<Date, StatsAll>) { }

    static parse(input: Object): Stats {
        if (!input) {
            return null;
        }

        const perDay = MarshallingUtils.parseMap(
            input["perDay"],
            (key) => MarshallingUtils.parseLocalDate(key),
            (value) => StatsAll.parse(value)
        );

        return new Stats(perDay);
    }

}
