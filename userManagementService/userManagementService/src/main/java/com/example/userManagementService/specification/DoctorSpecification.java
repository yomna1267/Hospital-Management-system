package com.example.userManagementService.specification;

import com.example.userManagementService.models.Doctor;
import org.springframework.data.jpa.domain.Specification;

public class DoctorSpecification {

    public static Specification<Doctor> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            String namePattern = "%" + name.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("firstName")), namePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("lastName")), namePattern)
            );
        };
    }

    public static Specification<Doctor> hasSpecialty(String specialty) {
        return (root, query, criteriaBuilder) -> {
            String specialtyPattern = "%" + specialty.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("specialty")), specialtyPattern);
        };
    }

    public static Specification<Doctor> hasExperienceGreaterThanOrEqual(int experienceYears) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("experienceYears"), experienceYears);
    }

    public static Specification<Doctor> searchDoctors(String name, Integer experienceYears, String specialty) {
        Specification<Doctor> specification = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            specification = specification.and(hasName(name));
        }
        if (experienceYears != null) {
            specification = specification.and(hasExperienceGreaterThanOrEqual(experienceYears));
        }
        if (specialty != null && !specialty.isEmpty()) {
            specification = specification.and(hasSpecialty(specialty));
        }

        return specification;
    }
}
