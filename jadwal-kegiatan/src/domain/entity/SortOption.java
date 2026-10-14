package domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan kegiatan.
 * Setiap opsi membawa comparator-nya sendiri sehingga logika pengurutan
 * terpusat di domain, bukan tersebar sebagai angka ajaib di view/use case.
 */
public enum SortOption {
    /** Urutkan berdasarkan hari (Senin–Minggu), lalu waktu. */
    DAY(Comparator.comparingInt((Activity a) -> Day.orderOf(a.getDay()))
            .thenComparing(Activity::getTime)),

    /** Urutkan berdasarkan waktu (paling pagi dulu), lalu hari. */
    TIME(Comparator.comparing(Activity::getTime)
            .thenComparingInt((Activity a) -> Day.orderOf(a.getDay()))),

    /** Urutkan judul dari A ke Z (case-insensitive). */
    TITLE_ASC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER)),

    /** Urutkan judul dari Z ke A (case-insensitive). */
    TITLE_DESC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());

    /** Comparator yang digunakan untuk mengurutkan daftar kegiatan. */
    private final Comparator<Activity> comparator;

    SortOption(Comparator<Activity> comparator) {
        this.comparator = comparator;
    }

    /** Mengembalikan comparator yang sesuai dengan opsi ini. */
    public Comparator<Activity> comparator() {
        return comparator;
    }
}
