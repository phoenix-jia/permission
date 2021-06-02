package com.famesmart.privilege.security;

import com.famesmart.privilege.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

@Data
@Builder
public class UserDetailsCustom implements UserDetails {
    private Integer id;

    private String username;

    @JsonIgnore
    private String password;

    private String name;

    private String phone;

    private String position;

    private Integer platform;

    private Date expireAt;

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Integer residentId;

    private String areaLevel;

    private String homePageType;

    private  boolean accountNonExpired;

    private  boolean accountNonLocked;

    private  boolean credentialsNonExpired;

    private  boolean enabled;

    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static UserDetailsCustom create(Users user) {

        boolean enabled = user.getDeletedAt() == null;
        boolean nonExpired = user.getExpireAt() == null || user.getExpireAt().after(new Date());

        return UserDetailsCustom.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .phone(user.getPhone())
                .position(user.getPosition())
                .platform(user.getPlatform())
                .residentId(user.getResidentId())
                .areaLevel(user.getAreaLevel())
                .homePageType(user.getHomePageType())
                .accountNonExpired(nonExpired)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(enabled)
                .build();
    }
}

