package com.typesafe.messagingmonitor.broker;

import com.typesafe.messagingmonitor.BrokerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BrokerService {

    private final BrokerRepository brokerRepository;
    private final BrokerMapper brokerMapper;

    public List<BrokerDTO> getBrokers(boolean withEmbedded) {
        List<BrokerDTO> list = brokerRepository.findAll().stream().map(brokerMapper::toDTO).collect(Collectors.toList());
        if (withEmbedded) {
            BrokerDTO embedded = new BrokerDTO();
            embedded.setPublicId("EMBEDDED");
            embedded.setHost("Embedded");
            list.add(0, embedded);
        }
        return list;
    }

    public BrokerDTO findBrokerByPublicId(String publicId) {
        return brokerMapper.toDTO(brokerRepository.findByPublicId(publicId));
    }

    public void deleteBroker(String agentPublicId) {
        brokerRepository.deleteByPublicId(agentPublicId);
    }

    public void saveBroker(BrokerDTO brokerDTO) {
        brokerRepository.save(brokerMapper.toPO(brokerDTO));
    }

}
