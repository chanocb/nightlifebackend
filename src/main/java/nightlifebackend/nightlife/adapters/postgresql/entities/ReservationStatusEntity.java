package nightlifebackend.nightlife.adapters.postgresql.entities;

public enum ReservationStatusEntity {
    ASSISTED,
    PENDING,
    EXPIRED;

    public static final String PREFIX = "RESERVATION_STATUS_";

    public static ReservationStatusEntity of(String withPrefix) {
        return ReservationStatusEntity.valueOf(withPrefix.replace(ReservationStatusEntity.PREFIX, ""));
    }
}
