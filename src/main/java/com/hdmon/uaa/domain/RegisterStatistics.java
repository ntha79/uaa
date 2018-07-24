package com.hdmon.uaa.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A RegisterStatistics.
 */
@Entity
@Table(name = "jhi_register_statistics")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RegisterStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_day")
    private Integer currentDay;

    @Column(name = "current_month")
    private Integer currentMonth;

    @Column(name = "current_year")
    private Integer currentYear;

    @Column(name = "count")
    private Integer count;

    @Column(name = "count_android")
    private Integer countAndroid;

    @Column(name = "count_ios")
    private Integer countIos;

    @Column(name = "count_web")
    private Integer countWeb;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentDay() {
        return currentDay;
    }

    public RegisterStatistics currentDay(Integer currentDay) {
        this.currentDay = currentDay;
        return this;
    }

    public void setCurrentDay(Integer currentDay) {
        this.currentDay = currentDay;
    }

    public Integer getCurrentMonth() {
        return currentMonth;
    }

    public RegisterStatistics currentMonth(Integer currentMonth) {
        this.currentMonth = currentMonth;
        return this;
    }

    public void setCurrentMonth(Integer currentMonth) {
        this.currentMonth = currentMonth;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public RegisterStatistics currentYear(Integer currentYear) {
        this.currentYear = currentYear;
        return this;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getCount() {
        return count;
    }

    public RegisterStatistics count(Integer count) {
        this.count = count;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountAndroid() {
        return countAndroid;
    }

    public RegisterStatistics countAndroid(Integer countAndroid) {
        this.countAndroid = countAndroid;
        return this;
    }

    public void setCountAndroid(Integer countAndroid) {
        this.countAndroid = countAndroid;
    }

    public Integer getCountIos() {
        return countIos;
    }

    public RegisterStatistics countIos(Integer countIos) {
        this.countIos = countIos;
        return this;
    }

    public void setCountIos(Integer countIos) {
        this.countIos = countIos;
    }

    public Integer getCountWeb() {
        return countWeb;
    }

    public RegisterStatistics countWeb(Integer countWeb) {
        this.countWeb = countWeb;
        return this;
    }

    public void setCountWeb(Integer countWeb) {
        this.countWeb = countWeb;
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
        RegisterStatistics registerStatistics = (RegisterStatistics) o;
        if (registerStatistics.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), registerStatistics.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RegisterStatistics{" +
            "id=" + getId() +
            ", currentDay=" + getCurrentDay() +
            ", currentMonth=" + getCurrentMonth() +
            ", currentYear=" + getCurrentYear() +
            ", count=" + getCount() +
            ", countAndroid=" + getCountAndroid() +
            ", countIos=" + getCountIos() +
            ", countWeb=" + getCountWeb() +
            "}";
    }
}
