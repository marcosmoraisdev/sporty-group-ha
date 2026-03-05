package com.sporty.groupha.betmatching.infrastructure.mappers;

import com.sporty.groupha.betmatching.domain.SettlementDecision;
import com.sporty.groupha.commonlib.messaging.settlement.BetSettlementMessage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMessageMapper {

    BetSettlementMessage toMessage(SettlementDecision settlementDecision);
}
