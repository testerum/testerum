
export class DateUtil {

    static dateToShortString(date: Date): string {
        let year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            day = date.getDate();

        return DateUtil.twoDigitToString(day)
            + "-"
            + DateUtil.twoDigitToString(month)
            + "-"
            + DateUtil.twoDigitToString(year);
    }

    static dateTimeToShortString(date: Date): string {
        let year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            day = date.getDate(),
            hour = date.getHours(),
            minute = date.getMinutes();

        return DateUtil.twoDigitToString(day)
            + "-"
            + DateUtil.twoDigitToString(month)
            + "-"
            + DateUtil.twoDigitToString(year)
            + " "
            + DateUtil.twoDigitToString(hour)
            + ":"
            + DateUtil.twoDigitToString(minute);
    }

    private static twoDigitToString(number: number): string {
        return number < 10 ? "0"+number : ""+number;
    }

    static durationToShortString(durantionInMillis: number): string {
        let date = new Date(durantionInMillis),
            hours = Math.floor(durantionInMillis / (3600*1000)),
            minutes = date.getMinutes(),
            seconds = date.getSeconds(),
            millis = ("" + date.getUTCMilliseconds()).slice(0, 2);

        let result = "" + millis;
        let suffix = "millis";

        if (seconds) {
            result = "" + seconds + "." + result;
            suffix = "seconds";
        }

        if (minutes) {
            result = "" + minutes + ":" + result;
            suffix = "minutes";
        }
        if (hours) {
            result = "" + hours + ":" + result;
            suffix = "hours";
        }

        return result + " " + suffix;
    }

    static getDaysBetweenDates(startDate: Date, endDate: Date): number {
        let oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds
        return Math.round(Math.abs((endDate.getTime() - startDate.getTime())/(oneDay)));
    }
}
