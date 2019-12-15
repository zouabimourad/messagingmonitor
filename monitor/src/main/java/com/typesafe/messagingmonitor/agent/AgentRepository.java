package com.typesafe.messagingmonitor.agent;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Integer> {

    void deleteByPublicId(String agentPublicId);

    Optional<Agent> findByPublicId(String publicId);

}
