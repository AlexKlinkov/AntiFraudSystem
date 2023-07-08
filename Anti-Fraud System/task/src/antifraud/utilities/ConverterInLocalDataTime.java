package antifraud.utilities;

import java.time.LocalDateTime;

public class ConverterInLocalDataTime {
    public static LocalDateTime localDataTimeFromString(String dataInStringFormat) {
        if (!dataInStringFormat.isEmpty()) {
            String[] dataAndTime = dataInStringFormat.split("T");
            String[] data = dataAndTime[0].split("-");
            String[] time = dataAndTime[1].split(":");
            int year = Integer.parseInt(data[0]);
            int month = Integer.parseInt(data[1]);
            int day = Integer.parseInt(data[2]);
            int hour = Integer.parseInt(time[0]);
            int min = Integer.parseInt(time[1]);
            int sec = Integer.parseInt(time[2]);
            return LocalDateTime.of(year, month, day, hour, min, sec);
        }
        return null;
    }
}
