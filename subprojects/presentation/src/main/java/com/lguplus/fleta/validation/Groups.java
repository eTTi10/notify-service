package com.lguplus.fleta.validation;

public class Groups {

    /**
     * Validation group for sa_id, stb_mac of CommonVo
     */
    public interface R1 {}  // Not blank for sa_id.
    public interface R2 {}  // Not blank for stb_mac.

    /**
     * Validation groups for start_num, req_count for CommonPagingVo
     */
    public interface R3 {}  // Not blank for start_num.
    public interface R4 {}  // Pattern for start_num.
    public interface R5 {}  // Bounds for start_num.
    public interface R6 {}  // Constraints for req_count.

    /**
     * Validation groups for start_num, paging_type for CommonNewPagingVo
     */
    public interface R7 {}  // Pattern for paging_type
    public interface R8 {}  // Bounds for start_num

    // Custom Groups
    public interface C1 {};
    public interface C2 {}
    public interface C3 {}
    public interface C4 {}
    public interface C5 {}
    public interface C6 {}
    public interface C7 {}
    public interface C8 {}
    public interface C9 {}
    public interface C10 {}
    public interface C11 {}
    public interface C12 {}
    public interface C13 {}
    public interface C14 {}
}
