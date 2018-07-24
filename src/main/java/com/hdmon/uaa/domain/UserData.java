package com.hdmon.uaa.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A UserData.
 */
@Entity
@Table(name = "jhi_user_data")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "gender", nullable = false)
    private Integer gender;

    @Column(name = "address")
    private String address;

    @Column(name = "birthday")
    private Long birthday;

    @Column(name = "about")
    private String about;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "source_province")
    private String sourceProvince;

    @Column(name = "current_province")
    private String currentProvince;

    @Column(name = "marriage")
    private Integer marriage;

    @Column(name = "list_company")
    private String listCompany;

    @Column(name = "list_school")
    private String listSchool;

    @Column(name = "slogan")
    private String slogan;

    @Column(name = "actived_level")
    private Integer activedLevel;

    @OneToOne
    @JoinColumn(unique = true, name = "user_id", referencedColumnName = "id")
    private User userData;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public Long getUserId() {
        return userId;
    }

    public UserData userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getGender() {
        return gender;
    }

    public UserData gender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public UserData address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getBirthday() {
        return birthday;
    }

    public UserData birthday(Long birthday) {
        this.birthday = birthday;
        return this;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getAbout() {
        return about;
    }

    public UserData about(String about) {
        this.about = about;
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public UserData coverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSourceProvince() {
        return sourceProvince;
    }

    public UserData sourceProvince(String sourceProvince) {
        this.sourceProvince = sourceProvince;
        return this;
    }

    public void setSourceProvince(String sourceProvince) {
        this.sourceProvince = sourceProvince;
    }

    public String getCurrentProvince() {
        return currentProvince;
    }

    public UserData currentProvince(String currentProvince) {
        this.currentProvince = currentProvince;
        return this;
    }

    public void setCurrentProvince(String currentProvince) {
        this.currentProvince = currentProvince;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public UserData marriage(Integer marriage) {
        this.marriage = marriage;
        return this;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public String getListCompany() {
        return listCompany;
    }

    public UserData listCompany(String listCompany) {
        this.listCompany = listCompany;
        return this;
    }

    public void setListCompany(String listCompany) {
        this.listCompany = listCompany;
    }

    public String getListSchool() {
        return listSchool;
    }

    public UserData listSchool(String listSchool) {
        this.listSchool = listSchool;
        return this;
    }

    public void setListSchool(String listSchool) {
        this.listSchool = listSchool;
    }

    public String getSlogan() {
        return slogan;
    }

    public UserData slogan(String slogan) {
        this.slogan = slogan;
        return this;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public User getUserData() {
        return userData;
    }

    public UserData userData(User user) {
        this.userData = user;
        return this;
    }

    public void setUserData(User user) {
        this.userData = user;
    }

    /**
     * 0: unactive, 1: mobile, 2: email, 3: both, 4: openid
     */
    public Integer getActivedLevel() {
        return activedLevel;
    }

    /**
     * 0: unactive, 1: mobile, 2: email, 3: both, 4: openid
     */
    public void setActivedLevel(Integer activedLevel) {
        this.activedLevel = activedLevel;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserData userData = (UserData) o;
        if (userData.getUserId() == null || getUserId() == null) {
            return false;
        }
        return Objects.equals(getUserId(), userData.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserId());
    }

    @Override
    public String toString() {
        return "UserData{" +
            "userId=" + getUserId() +
            ", gender=" + getGender() +
            ", address='" + getAddress() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", about='" + getAbout() + "'" +
            ", coverUrl='" + getCoverUrl() + "'" +
            ", sourceProvince='" + getSourceProvince() + "'" +
            ", currentProvince='" + getCurrentProvince() + "'" +
            ", marriage=" + getMarriage() +
            ", listCompany='" + getListCompany() + "'" +
            ", listSchool='" + getListSchool() + "'" +
            ", slogan='" + getSlogan() + "'" +
            ", activedLevel='" + getActivedLevel() + "'" +
            "}";
    }
}
