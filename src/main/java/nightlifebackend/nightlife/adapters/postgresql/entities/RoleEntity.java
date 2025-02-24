package nightlifebackend.nightlife.adapters.postgresql.entities;

public enum RoleEntity {
    ADMIN, OWNER, CLIENT;

    public static final String PREFIX = "ROLE_";

    public static RoleEntity of(String withPrefix) {
        return RoleEntity.valueOf(withPrefix.replace(RoleEntity.PREFIX, ""));
    }
}
