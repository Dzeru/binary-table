package com.pie.binarytable.entities;

import org.springframework.security.core.GrantedAuthority;

/*
For Spring Security purposes
 */
public enum Role implements GrantedAuthority
{
    USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
