import {StepSearchItem} from "./model/step-search-item.model";
import {StepSearchTokenizationUtil} from "./util/step-search-tokenization.util";
import {LevenshteinDistanceUtil} from "./util/levenshtein-distance.util";
import {StepPhaseEnum, StepPhaseUtil} from "../../model/enums/step-phase.enum";

export class StepSearch<T extends StepSearchItem> {

    static readonly MIN_MATCHING_PERCENTAGE = 0.6;
    static readonly MIN_TOKEN_MATCHING_PERCENTAGE = 0.6;

    constructor(public items: Array<T>) {
    }

    find(query, previewsStepPhase: StepPhaseEnum | null): Array<T> {
        let queryTokens = StepSearchTokenizationUtil.tokenize(query);

        if (queryTokens.length > 0 && queryTokens[0] === "and" && previewsStepPhase != null) {
            queryTokens[0] = StepPhaseUtil.toLowerCaseString(previewsStepPhase);
        }

        this.setMatchingPercentageOnItems(queryTokens);

        let result: T[] = [];
        for (const item of this.items) {
            if (item.matchingPercentage >= StepSearch.MIN_MATCHING_PERCENTAGE) {
                result.push(item)
            }
        }

        result.sort((a, b) => b.matchingPercentage - a.matchingPercentage);

        return result;
    }

    private setMatchingPercentageOnItems(queryTokens: Array<string>) {
        for (const item of this.items) {
            this.setMatchingPercentageOnItem(queryTokens, item);
        }
    }

    private setMatchingPercentageOnItem(queryTokens: Array<string>, item: T) {
        item.matchingPercentage = 0;
        item.isPerfectMatch = false;

        let allQueryTokensMatchingPercentage: number[] = [];

        for (const queryToken of queryTokens) {
            let bestMatchingForToken: number = 0;

            for (const token of item.tokens) {
                if(bestMatchingForToken == 100) break;
                let matchingForToken = LevenshteinDistanceUtil.levenshteinInPercentage(token, queryToken);

                if(matchingForToken < StepSearch.MIN_TOKEN_MATCHING_PERCENTAGE) matchingForToken = 0;
                if(token.startsWith(queryToken)&& matchingForToken == 0) matchingForToken = 0.8;

                bestMatchingForToken = Math.max(matchingForToken, bestMatchingForToken);
            }
            allQueryTokensMatchingPercentage.push(bestMatchingForToken)
        }

        let fullMatchingPercentage = this.calculateAverageMatchingPercentage(allQueryTokensMatchingPercentage);

        if (fullMatchingPercentage == 1 && queryTokens.length == item.tokens.length) {
            let isPerfectMatch: boolean = true;
            for (let i = 0; i < queryTokens.length; i++) {
                const queryToken = queryTokens[i];
                const itemToken = item.tokens[i];

                if (queryToken !== itemToken) {
                    isPerfectMatch = false;
                }
            }
            item.isPerfectMatch = isPerfectMatch;
        }

        item.matchingPercentage = fullMatchingPercentage;
    }

    private calculateAverageMatchingPercentage(allQueryTokensMatchingPercentage: number[]) {
        if(allQueryTokensMatchingPercentage.length == 0) return 0;

        let matchingPercentageSum = 0;
        for (const matchingPercentage of allQueryTokensMatchingPercentage) {
            matchingPercentageSum += matchingPercentage
        }
        return matchingPercentageSum / allQueryTokensMatchingPercentage.length;
    }
}
