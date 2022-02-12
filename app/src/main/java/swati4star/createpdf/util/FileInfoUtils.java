package swati4star.createpdf.util;

import java.io.File;
import java.util.Date;

public final class FileInfoUtils {

    private static FileInfoUtils fileInfoUtils = new FileInfoUtils();

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private FileInfoUtils() {
    }

    /* Static 'instance' method */
    public static FileInfoUtils getInstance() {
        return fileInfoUtils;
    }

    // GET PDF DETAILS

    /**
     * Gives a formatted last modified date for pdf ListView
     *
     * @param file file object whose last modified date is to be returned
     * @return String date modified in formatted form
     **/
    public static String getFormattedDate(File file) {
        Date lastModDate = new Date(file.lastModified());
        String[] formatDate = lastModDate.toString().split(" ");
        String time = formatDate[3];
        String[] formatTime = time.split(":");
        String date = formatTime[0] + ":" + formatTime[1];

        return formatDate[0] + ", " + formatDate[1] + " " + formatDate[2] + " at " + date;
    }

    /**
     * Gives a formatted size in MB for every pdf in pdf ListView
     *
     * @param file file object whose size is to be returned
     * @return String Size of pdf in formatted form
     */
    public static String getFormattedSize(File file) {
        return String.format("%.2f MB", (double) file.length() / (1024 * 1024));
    }
}
