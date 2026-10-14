package framework.view;

import adapter.presenter.FinancePresenter;
import domain.entity.SortOption;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.FinanceUseCase;

/**
 * Tampilan konsol aplikasi catatan keuangan.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis — hanya menangani interaksi user.
 */
public class FinanceView {
    /** Use case yang menjalankan operasi bisnis. */
    private final FinanceUseCase useCase;

    /** Presenter yang memformat hasil ke output layar. */
    private final FinancePresenter presenter;

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar transaksi dan saldo terkini sebelum menu
            presenter.showTransactions(useCase.getAllTransactions(), useCase.getBalance());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input.trim().toLowerCase()) {
                case "1" -> addTransaction(TransactionType.INCOME);
                case "2" -> addTransaction(TransactionType.EXPENSE);
                case "3" -> searchTransaction();
                case "4" -> sortTransaction();
                case "5" -> presenter.showBalance(useCase.getBalance());
                case "6" -> removeTransaction();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                System.out.println();
            }
        }
    }

    /** Mencetak opsi menu ke layar. */
    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah Pemasukan");
        System.out.println("2. Tambah Pengeluaran");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Lihat Saldo");
        System.out.println("6. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah transaksi (pemasukan atau pengeluaran). */
    private void addTransaction(TransactionType type) {
        String label = type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
        System.out.println("[Tambah " + label + "]");

        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (InputUtil.isCancel(description)) {
            return;
        }

        String strAmount = InputUtil.input("Jumlah");

        try {
            long amount = parseAmount(strAmount);
            presenter.showAddSuccess(useCase.addTransaction(description, amount, type));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form cari transaksi berdasarkan keterangan. */
    private void searchTransaction() {
        System.out.println("[Cari Transaksi]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(useCase.searchTransactions(keyword), keyword);
        }
    }

    /** Form urutkan transaksi berdasarkan pilihan user. */
    private void sortTransaction() {
        System.out.println("[Urutkan Transaksi]");
        System.out.println("1. Jumlah (Terkecil)");
        System.out.println("2. Jumlah (Terbesar)");
        System.out.println("3. Pemasukan Dulu");
        System.out.println("4. Pengeluaran Dulu");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        System.out.println();
        if (InputUtil.isCancel(input)) {
            return;
        }

        // Konversi input angka ke enum domain
        SortOption option = mapSortOption(input.trim());
        if (option == null) {
            presenter.showInvalidSortOption();
            return;
        }

        presenter.showSortedTransactions(useCase.sortTransactions(option));
    }

    /** Form hapus transaksi berdasarkan ID. */
    private void removeTransaction() {
        System.out.println("[Hapus Transaksi]");
        String strId = InputUtil.input("ID Transaksi (x Jika Batal)");

        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = parseId(strId);
            useCase.removeTransaction(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /**
     * Mengonversi input string menjadi ID.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private int parseId(String value) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("ID tidak valid!");
        }
    }

    /**
     * Mengonversi input string menjadi jumlah uang.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private long parseAmount(String value) throws ValidationException {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah tidak valid!");
        }
    }

    /** Memetakan pilihan menu (1-4) ke {@link SortOption} domain. */
    private SortOption mapSortOption(String input) {
        return switch (input) {
            case "1" -> SortOption.AMOUNT_ASC;
            case "2" -> SortOption.AMOUNT_DESC;
            case "3" -> SortOption.INCOME_FIRST;
            case "4" -> SortOption.EXPENSE_FIRST;
            default -> null;
        };
    }
}
