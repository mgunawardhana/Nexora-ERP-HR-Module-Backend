package com.nexora.backend.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexora.backend.domain.entity.EmployeeDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {

    @JsonProperty("employee_name")
    private String employeeName;
    @JsonProperty("Age")
    private Integer age;
    @JsonProperty("BusinessTravel")
    private String businessTravel;
    @JsonProperty("DailyRate")
    private BigDecimal dailyRate;
    @JsonProperty("Department")
    private String department;
    @JsonProperty("DistanceFromHome")
    private Integer distanceFromHome;
    @JsonProperty("Education")
    private Integer education;
    @JsonProperty("EducationField")
    private String educationField;
    @JsonProperty("EnvironmentSatisfaction")
    private Integer environmentSatisfaction;
    @JsonProperty("Gender")
    private String gender;
    @JsonProperty("HourlyRate")
    private BigDecimal hourlyRate;
    @JsonProperty("JobInvolvement")
    private Integer jobInvolvement;
    @JsonProperty("JobLevel")
    private Integer jobLevel;
    @JsonProperty("JobRole")
    private String jobRole;
    @JsonProperty("JobSatisfaction")
    private Integer jobSatisfaction;
    @JsonProperty("MaritalStatus")
    private String maritalStatus;
    @JsonProperty("MonthlyIncome")
    private BigDecimal monthlyIncome;
    @JsonProperty("MonthlyRate")
    private BigDecimal monthlyRate;
    @JsonProperty("NumCompaniesWorked")
    private Integer numCompaniesWorked;
    @JsonProperty("OverTime")
    private String overTime;
    @JsonProperty("RelationshipSatisfaction")
    private Integer relationshipSatisfaction;
    @JsonProperty("StockOptionLevel")
    private Integer stockOptionLevel;
    @JsonProperty("TotalWorkingYears")
    private Integer totalWorkingYears;
    @JsonProperty("TrainingTimesLastYear")
    private Integer trainingTimesLastYear;
    @JsonProperty("WorkLifeBalance")
    private Integer workLifeBalance;
    @JsonProperty("YearsAtCompany")
    private Integer yearsAtCompany;
    @JsonProperty("YearsInCurrentRole")
    private Integer yearsInCurrentRole;
    @JsonProperty("YearsSinceLastPromotion")
    private Integer yearsSinceLastPromotion;
    @JsonProperty("YearsWithCurrManager")
    private Integer yearsWithCurrManager;

    /**
     * Factory method to create a PredictionRequest from an EmployeeDetails entity.
     * It will only map the fields that exist in EmployeeDetails.
     */
    public static PredictionRequest from(EmployeeDetails details) {
        return PredictionRequest.builder()
                .employeeName(details.getEmployeeName())
                .age(details.getAge())
                .businessTravel(details.getBusinessTravel())
                .dailyRate(details.getDailyRate())
                .department(details.getDepartment())
                .distanceFromHome(details.getDistanceFromHome())
                .education(details.getEducation())
                .educationField(details.getEducationField())
                .environmentSatisfaction(details.getEnvironmentSatisfaction())
                .gender(details.getGender())
                .hourlyRate(details.getHourlyRate())
                .jobInvolvement(details.getJobInvolvement())
                .jobLevel(details.getJobLevel())
                .jobRole(details.getJobRole())
                .jobSatisfaction(details.getJobSatisfaction())
                .maritalStatus(details.getMaritalStatus())
                .monthlyIncome(details.getMonthlyIncome())
                .monthlyRate(details.getMonthlyRate())
                .numCompaniesWorked(details.getNumCompaniesWorked())
                .overTime(details.getOverTime())
                .relationshipSatisfaction(details.getRelationshipSatisfaction())
                .stockOptionLevel(details.getStockOptionLevel())
                .totalWorkingYears(details.getTotalWorkingYears())
                .trainingTimesLastYear(details.getTrainingTimesLastYear())
                .workLifeBalance(details.getWorkLifeBalance())
                .yearsAtCompany(details.getYearsAtCompany())
                .yearsInCurrentRole(details.getYearsInCurrentRole())
                .yearsSinceLastPromotion(details.getYearsSinceLastPromotion())
                .yearsWithCurrManager(details.getYearsWithCurrManager())
                .build();
    }
}