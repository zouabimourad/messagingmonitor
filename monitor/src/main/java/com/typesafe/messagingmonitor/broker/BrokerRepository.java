package com.typesafe.messagingmonitor.broker;


import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerRepository extends JpaRepository<Broker, Integer> {

    void deleteByPublicId(String agentPublicId);

    Broker findByPublicId(String publicId);
}
