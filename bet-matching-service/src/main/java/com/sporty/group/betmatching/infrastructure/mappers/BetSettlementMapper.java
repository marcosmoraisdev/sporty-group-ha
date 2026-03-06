package com.sporty.group.betmatching.infrastructure.mappers;

import com.sporty.group.betmatching.domain.SettlementDecision;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    BetSettlementMessage toMessage(SettlementDecision settlementDecision);
}
