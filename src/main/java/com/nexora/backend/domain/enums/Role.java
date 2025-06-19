package com.nexora.backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nexora.backend.domain.enums.Permission.*;

@RequiredArgsConstructor
public enum Role {


    USER(Collections.emptySet()),
    SYSTEM_ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    USER_REGISTER_EMPLOYEE,
                    USER_REGISTER_MANAGER,
                    USER_REGISTER_HR,
                    USER_REGISTER_TEAM_LEAD,
                    USER_REGISTER_FINANCE,
                    USER_REGISTER_INTERN,
                    USER_REGISTER_CONTRACT
            )
    ),
    HR_MANAGER(
            Set.of(
                    HR_READ,
                    HR_CREATE,
                    HR_UPDATE,
                    HR_DELETE,
                    USER_REGISTER_EMPLOYEE,
                    USER_REGISTER_TEAM_LEAD,
                    USER_REGISTER_INTERN,
                    USER_REGISTER_CONTRACT
            )
    ),
    DEPARTMENT_MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    USER_REGISTER_EMPLOYEE
            )
    ),
    EMPLOYEE(
            Set.of(
                    EMPLOYEE_READ,
                    EMPLOYEE_UPDATE
            )
    ),
    JUNIOR_EMPLOYEE(
            Set.of(
                    EMPLOYEE_READ
            )
    ),
    FINANCE_DIRECTOR(
            Set.of(
                    PAYROLL_READ,
                    MANAGER_READ,
                    EMPLOYEE_READ,
                    USER_REGISTER_FINANCE
            )
    ),

    FINANCE_MANAGER(
            Set.of(
                    PAYROLL_READ,
                    EMPLOYEE_READ,
                    USER_REGISTER_FINANCE
            )
    ),

    FINANCE_OFFICER(
            Set.of(
                    PAYROLL_READ,
                    EMPLOYEE_READ
            )
    ),

    ACCOUNTANT(
            Set.of(
                    PAYROLL_READ,
                    EMPLOYEE_READ
            )
    ),
    TEAM_LEAD(
            Set.of(
                    EMPLOYEE_READ,
                    EMPLOYEE_UPDATE,
                    TEAM_LEAD_READ
            )
    ),
    INTERN(
            Set.of(
                    EMPLOYEE_READ
            )
    ),
    HR_ASSISTANT(
            Set.of(
                    HR_READ,
                    EMPLOYEE_READ
            )
    ),
    CONTRACT_WORKER(
            Set.of(
                    EMPLOYEE_READ
            )
    ),
    IT_SUPPORT(
            Set.of(
                    ADMIN_READ,
                    EMPLOYEE_READ
            )
    ),

    SECURITY_OFFICER(
            Set.of(
                    ADMIN_READ,
                    EMPLOYEE_READ
            )
    ), BUSINESS_ANALYST(
            Set.of(
                    EMPLOYEE_READ,
                    EMPLOYEE_UPDATE
            )
    ),

    QUALITY_ASSURANCE(
            Set.of(
                    EMPLOYEE_READ,
                    EMPLOYEE_UPDATE
            )
    ),

    MARKETING_MANAGER(
            Set.of(
                    MANAGER_READ,
                    EMPLOYEE_READ,
                    EMPLOYEE_UPDATE,
                    USER_REGISTER_EMPLOYEE
            )
    ),

    SALES_MANAGER(
            Set.of(
                    MANAGER_READ,
                    EMPLOYEE_READ,
                    EMPLOYEE_UPDATE,
                    USER_REGISTER_EMPLOYEE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
