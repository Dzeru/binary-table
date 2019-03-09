package com.pie.binarytable.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements UserDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@NotNull
    private Long userAccountsId;

	@NotNull
    private String username;

	@NotNull
    private String password;

	@NotNull
    private String name;

    private boolean active;
    private String updatePassword; //UUID for updating password

    @NotNull
    private String registrationDate;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    /*@OrderBy("goalName")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Goal> goals;*/

    public Long getId() {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserAccountsId()
    {
        return userAccountsId;
    }

    public void setUserAccountsId(Long userAccountsId)
    {
        this.userAccountsId = userAccountsId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUpdatePassword()
    {
        return updatePassword;
    }

    public void setUpdatePassword(String updatePassword)
    {
        this.updatePassword = updatePassword;
    }

    public String getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate)
    {
        this.registrationDate = registrationDate;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isActive() == user.isActive() &&
                Objects.equals(getId(), user.getId()) &&
                Objects.equals(getUserAccountsId(), user.getUserAccountsId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getUpdatePassword(), user.getUpdatePassword()) &&
                Objects.equals(getRegistrationDate(), user.getRegistrationDate()) &&
                Objects.equals(getRoles(), user.getRoles());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getId(), getUserAccountsId(), getUsername(), getPassword(), getName(), isActive(), getUpdatePassword(), getRegistrationDate(), getRoles());
    }

    @Override
    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", userAccountsId=" + userAccountsId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", updatePassword='" + updatePassword + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", roles=" + roles +
                '}';
    }
/*
	public Set<Goal> getGoals()
	{
		return goals;
	}

	public void setGoals(Set<Goal> goals)
	{
		this.goals = goals;
	}
	*/
}
