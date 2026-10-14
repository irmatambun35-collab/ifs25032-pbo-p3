package domain.entity;

/** Jenis transaksi keuangan. */
public enum TransactionType {
    /** Uang masuk (menambah saldo). */
    INCOME,

    /** Uang keluar (mengurangi saldo). */
    EXPENSE
}
