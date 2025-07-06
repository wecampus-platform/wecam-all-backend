package org.example.wecambackend.common.context;

public class CouncilContextHolder {

    private static final ThreadLocal<Long> councilIdHolder = new ThreadLocal<>();

    public static void setCouncilId(Long councilId) {
        councilIdHolder.set(councilId);
    }

    public static Long getCouncilId() {
        return councilIdHolder.get();
    }

    public static void clear() {
        councilIdHolder.remove();
    }
}
