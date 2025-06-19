package com.nexora.backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    /* Admin Permissions */
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    /* HR Permissions */
    HR_READ("hr:read"),
    HR_CREATE("hr:create"),
    HR_UPDATE("hr:update"),
    HR_DELETE("hr:delete"),

    /* Employee Permissions */
    EMPLOYEE_READ("employee:read"),
    EMPLOYEE_UPDATE("employee:update"),

    /* Manager Permissions */
    MANAGER_READ("manager:read"),
    MANAGER_UPDATE("manager:update"),

    /* Payroll/Finance Permissions */
    PAYROLL_READ("payroll:read"),
    PAYROLL_CREATE("payroll:create"),
    PAYROLL_UPDATE("payroll:update"),
    PAYROLL_DELETE("payroll:delete"),

    /* Team Lead Permissions */
    TEAM_LEAD_READ("team_lead:read"),
    TEAM_LEAD_UPDATE("team_lead:update"),

    /* IT/Technical Permissions */
    IT_READ("it:read"),
    IT_UPDATE("it:update"),
    IT_CREATE("it:create"),
    IT_DELETE("it:delete"),

    /* Security Permissions */
    SECURITY_READ("security:read"),
    SECURITY_UPDATE("security:update"),

    /* Business Analysis Permissions */
    BUSINESS_ANALYSIS_READ("business_analysis:read"),
    BUSINESS_ANALYSIS_UPDATE("business_analysis:update"),

    /* Quality Assurance Permissions */
    QA_READ("qa:read"),
    QA_UPDATE("qa:update"),

    /* Marketing Permissions */
    MARKETING_READ("marketing:read"),
    MARKETING_UPDATE("marketing:update"),
    MARKETING_CREATE("marketing:create"),

    /* Sales Permissions */
    SALES_READ("sales:read"),
    SALES_UPDATE("sales:update"),
    SALES_CREATE("sales:create"),

    /* Registration Permissions */
    USER_REGISTER_EMPLOYEE("user:register_employee"),
    USER_REGISTER_MANAGER("user:register_manager"),
    USER_REGISTER_HR("user:register_hr"),
    USER_REGISTER_TEAM_LEAD("user:register_team_lead"),
    USER_REGISTER_FINANCE("user:register_finance"),
    USER_REGISTER_INTERN("user:register_intern"),
    USER_REGISTER_CONTRACT("user:register_contract"),
    USER_REGISTER_IT("user:register_it"),
    USER_REGISTER_SECURITY("user:register_security"),
    USER_REGISTER_MARKETING("user:register_marketing"),
    USER_REGISTER_SALES("user:register_sales");

    private final String permission;
}
