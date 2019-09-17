import {StringUtils} from "./string-utils.util";
import {ObjectUtil} from "./object.util";

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
        let secondsInADay = 24*60*60*1000; // hours*minutes*seconds*milliseconds
        return Math.round(Math.abs((endDate.getTime() - startDate.getTime())/secondsInADay));
    }

    private static regexp = new RegExp('(\\d{1,2})-(\\d{1,2})-(\\d{4})(\\s+(\\d{1,2}):(\\d{1,2})(:(\\d{1,2}))?)?');
    static stringToDate(dateAsString: string): Date| null {

        if(StringUtils.isEmpty(dateAsString)) {return null;}

        if (!this.regexp.test(dateAsString)) { return null; }

        let dateParts = dateAsString.split(" ");
        let datePartAsString = dateParts.length > 0 ? dateParts[0] : null;
        let timePartAsString = dateParts.length > 1 ? dateParts[1] : null;

        if(datePartAsString == null) return null;

        dateParts = datePartAsString.split('-');
        let day   = dateParts.length > 0 && ObjectUtil.isANumber(dateParts[0]) ?  ObjectUtil.getAsNumber(dateParts[0]) : null;
        let month = dateParts.length > 1 && ObjectUtil.isANumber(dateParts[1]) ?  ObjectUtil.getAsNumber(dateParts[1]) - 1 : null;
        let year  = dateParts.length > 2 && ObjectUtil.isANumber(dateParts[2]) ?  ObjectUtil.getAsNumber(dateParts[2]) : null;

        if (timePartAsString) {
            let hour   = timePartAsString.length > 0 && ObjectUtil.isANumber(timePartAsString[0]) ?  ObjectUtil.getAsNumber(timePartAsString[0]) : null;
            let minute = timePartAsString.length > 1 && ObjectUtil.isANumber(timePartAsString[1]) ?  ObjectUtil.getAsNumber(timePartAsString[1]) : null;
            let second = timePartAsString.length > 2 && ObjectUtil.isANumber(timePartAsString[2]) ?  ObjectUtil.getAsNumber(timePartAsString[2]) : null;

            return new Date(year, month, day, hour, minute, second)
        }

        return new Date(year, month, day)
    }
}
