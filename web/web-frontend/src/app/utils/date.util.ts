
export class DateUtil {

    static dateTimeToShortString(date: Date): string {
        let year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            day = date.getDate(),
            hour = date.getHours(),
            minute = date.getMinutes();

        return DateUtil.twoDigitToString(day)
            + "/"
            + DateUtil.twoDigitToString(month)
            + "/"
            + DateUtil.twoDigitToString(year)
            + " "
            + DateUtil.twoDigitToString(hour)
            + ":"
            + DateUtil.twoDigitToString(minute);
    }

    private static twoDigitToString(number: number): string {
        return number < 10 ? "0"+number : ""+number;
    }
}
