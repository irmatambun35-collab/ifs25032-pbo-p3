package usecase;

import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case yang menangani logika bisnis kontak teman.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ContactUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IContactRepository contactRepository;

    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** Mengambil semua kontak yang tersedia. */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /** Menambahkan kontak baru setelah validasi nama, telepon, dan email. */
    public Contact addContact(String name, String phone, String email) throws ValidationException {
        String validName = require(name, "Nama kontak tidak boleh kosong!");
        String validPhone = require(phone, "Nomor telepon tidak boleh kosong!");
        String validEmail = require(email, "Email tidak boleh kosong!");
        return contactRepository.save(validName, validPhone, validEmail);
    }

    /**
     * Mengubah nama, telepon, dan/atau email kontak.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     */
    public void updateContact(int id, String name, String phone, String email)
            throws EntityNotFoundException, ValidationException {
        Optional<Contact> found = contactRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Kontak", id);
        }

        String validName = name == null ? null : require(name, "Nama kontak tidak boleh kosong!");
        String validPhone = phone == null ? null : require(phone, "Nomor telepon tidak boleh kosong!");
        String validEmail = email == null ? null : require(email, "Email tidak boleh kosong!");

        Contact contact = found.get();
        if (validName != null) {
            contact.changeName(validName);
        }
        if (validPhone != null) {
            contact.changePhone(validPhone);
        }
        if (validEmail != null) {
            contact.changeEmail(validEmail);
        }

        contactRepository.update(contact);
    }

    /** Menghapus kontak berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeContact(int id) throws EntityNotFoundException {
        boolean deleted = contactRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Kontak", id);
        }
    }

    /** Mencari kontak yang namanya mengandung kata kunci (case-insensitive). */
    public List<Contact> searchContacts(String keyword) {
        String lowerKeyword = keyword.trim().toLowerCase();
        return contactRepository.findBy(c -> c.getName().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan kontak sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Contact> sortContacts(SortOption option) {
        return contactRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Memastikan nilai tidak kosong; mengembalikan nilai yang sudah dirapikan. */
    private String require(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
        return value.trim();
    }
}
