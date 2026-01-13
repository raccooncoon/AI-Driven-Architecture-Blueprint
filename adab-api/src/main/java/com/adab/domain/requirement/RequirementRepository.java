package com.adab.domain.requirement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, String> {
    List<Requirement> findAllByOrderByRequirementIdAsc();

    List<Requirement> findAllByOrderBySequenceNoAsc();
}
