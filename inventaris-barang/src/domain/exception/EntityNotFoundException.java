package domain.exception;

/**
 * Exception yang dilempar ketika entitas dengan ID tertentu tidak ditemukan.
 */
public class EntityNotFoundException extends DomainException {
    private final String entityName;
    private final int id;

    public EntityNotFoundException(String entityName, int id) {
        super(entityName + " dengan ID " + id + " tidak ditemukan.");
        this.entityName = entityName;
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public int getId() {
        return id;
    }
}
