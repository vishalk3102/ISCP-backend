package com.iscp.backend.repositories;

import com.iscp.backend.models.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing {@link Evidence} entities.
 */
@Repository
public interface EvidenceRepository extends JpaRepository<Evidence,String> {

    /**
     * Find all evidences associated with a given security ID.
     *
     * @param securityId the ID of the security compliance to find the Evidences.
     * @return a list of filenames associated with the given security ID.
     */
    @Query("SELECT e.fileName FROM Evidence e WHERE e.securityCompliance.securityId = :securityId")
    List<String> findFileNameBySecurityId(@Param("securityId") String securityId);


    /**
     * Retrieves an {@link Evidence} entity based on its filename.
     * @param filename the name of the evidence file to retrieve.
     * @return an {@link Evidence} object associated with the given filename, or null if no evidence is found.
     */
    Evidence findByFileName(@Param("filename") String filename);
}
