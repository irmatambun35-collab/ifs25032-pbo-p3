package domain.entity;

import java.util.Optional;

/**
 * Hari dalam seminggu (Senin – Minggu).
 * Urutan deklarasi enum dipakai sebagai urutan hari saat mengurutkan kegiatan.
 */
public enum Day {
    SENIN("Senin"),
    SELASA("Selasa"),
    RABU("Rabu"),
    KAMIS("Kamis"),
    JUMAT("Jumat"),
    SABTU("Sabtu"),
    MINGGU("Minggu");

    private final String label;

    Day(String label) {
        this.label = label;
    }

    /** Nama hari dalam format penulisan baku, misalnya "Senin". */
    public String label() {
        return label;
    }

    /** Mengubah teks (tanpa memperhatikan huruf besar/kecil) menjadi {@link Day}. */
    public static Optional<Day> fromLabel(String text) {
        if (text == null) {
            return Optional.empty();
        }
        String trimmed = text.trim();
        for (Day day : values()) {
            if (day.label.equalsIgnoreCase(trimmed)) {
                return Optional.of(day);
            }
        }
        return Optional.empty();
    }

    /** Nomor urut hari (0 = Senin). Teks yang tidak dikenal diletakkan di urutan paling akhir. */
    public static int orderOf(String text) {
        return fromLabel(text).map(Enum::ordinal).orElse(values().length);
    }
}
