package usecase;

import domain.entity.Activity;
import domain.entity.Day;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use case yang menangani logika bisnis jadwal kegiatan.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ActivityUseCase {
    /** Format waktu yang diterima: H:mm atau HH:mm. */
    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d{1,2}):(\\d{2})$");

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua kegiatan yang tersedia. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Menambahkan kegiatan baru setelah validasi judul, hari, dan waktu. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        String validTitle = validateTitle(title);
        String validDay = validateDay(day);
        String validTime = validateTime(time);
        return activityRepository.save(validTitle, validDay, validTime);
    }

    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     * Semua field divalidasi lebih dulu sehingga perubahan tidak pernah diterapkan setengah-setengah.
     */
    public void updateActivity(int id, String title, String day, String time)
            throws EntityNotFoundException, ValidationException {
        Optional<Activity> found = activityRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Kegiatan", id);
        }

        String validTitle = title == null ? null : validateTitle(title);
        String validDay = day == null ? null : validateDay(day);
        String validTime = time == null ? null : validateTime(time);

        Activity activity = found.get();
        if (validTitle != null) {
            activity.changeTitle(validTitle);
        }
        if (validDay != null) {
            activity.changeDay(validDay);
        }
        if (validTime != null) {
            activity.changeTime(validTime);
        }

        activityRepository.update(activity);
    }

    /** Menghapus kegiatan berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeActivity(int id) throws EntityNotFoundException {
        boolean deleted = activityRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Kegiatan", id);
        }
    }

    /** Mencari kegiatan yang judulnya mengandung kata kunci (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword.trim().toLowerCase();
        return activityRepository.findBy(a -> a.getTitle().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan kegiatan sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Judul tidak boleh kosong; mengembalikan judul yang sudah dirapikan. */
    private String validateTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Judul kegiatan tidak boleh kosong!");
        }
        return title.trim();
    }

    /** Hari harus Senin–Minggu; mengembalikan penulisan baku (mis. "senin" menjadi "Senin"). */
    private String validateDay(String day) throws ValidationException {
        return Day.fromLabel(day)
                .map(Day::label)
                .orElseThrow(() -> new ValidationException("Hari tidak valid (Senin - Minggu)!"));
    }

    /** Waktu harus berformat jam:menit 00:00–23:59; mengembalikan format HH:mm. */
    private String validateTime(String time) throws ValidationException {
        Matcher matcher = TIME_PATTERN.matcher(time == null ? "" : time.trim());
        if (!matcher.matches()) {
            throw new ValidationException("Format waktu tidak valid (HH:mm)!");
        }
        int hour = Integer.parseInt(matcher.group(1));
        int minute = Integer.parseInt(matcher.group(2));
        if (hour > 23 || minute > 59) {
            throw new ValidationException("Format waktu tidak valid (HH:mm)!");
        }
        return String.format("%02d:%02d", hour, minute);
    }
}
