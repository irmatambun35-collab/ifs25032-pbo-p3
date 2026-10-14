package domain.entity;

/**
 * Entity inti yang merepresentasikan satu kegiatan terjadwal.
 * Berada di layer domain — tidak bergantung pada layer lain dan bebas dari
 * urusan tampilan maupun penyimpanan.
 */
public class Activity {
    /** ID unik kegiatan, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Judul kegiatan. */
    private String title;

    /** Hari pelaksanaan, misalnya "Senin". */
    private String day;

    /** Waktu pelaksanaan dalam format HH:mm. */
    private String time;

    public Activity(int id, String title, String day, String time) {
        this.id = id;
        this.title = title;
        this.day = day;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    /** Mengubah judul kegiatan. */
    public void changeTitle(String title) {
        this.title = title;
    }

    /** Mengubah hari pelaksanaan. */
    public void changeDay(String day) {
        this.day = day;
    }

    /** Mengubah waktu pelaksanaan. */
    public void changeTime(String time) {
        this.time = time;
    }
}
