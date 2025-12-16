package lotecs.auth.domain.tenant.model;

public enum SiteStatus {
    DRAFT("준비중"),
    PUBLISHED("게시됨"),
    SUSPENDED("일시중지"),
    ARCHIVED("보관됨");

    private final String description;

    SiteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublished() {
        return this == PUBLISHED;
    }

    public boolean canPublish() {
        return this == DRAFT;
    }

    public boolean canUnpublish() {
        return this == PUBLISHED;
    }

    public boolean canSuspend() {
        return this == PUBLISHED;
    }

    public boolean canResume() {
        return this == SUSPENDED;
    }

    public boolean canArchive() {
        return this == DRAFT || this == SUSPENDED;
    }
}
