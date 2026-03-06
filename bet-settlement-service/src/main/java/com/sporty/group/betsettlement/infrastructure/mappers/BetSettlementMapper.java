package com.sporty.group.betsettlement.infrastructure.mappers;

import com.sporty.group.betsettlement.domain.BetSettlement;
import com.sporty.group.betsettlement.infrastructure.entity.BetSettlementEntity;
import com.sporty.group.commonlib.messaging.settlement.BetSettlementMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementMapper {

    @Mapping(target = "betId", source = "betId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "eventMarketId", source = "eventMarketId")
    @Mapping(target = "expectedWinnerId", source = "expectedWinnerId")
    @Mapping(target = "actualWinnerId", source = "actualWinnerId")
    @Mapping(target = "betAmount", source = "betAmount")
    @Mapping(target = "result", source = "result")
    BetSettlement toDomain(BetSettlementMessage betSettlementMessage);

    @Mapping(target = "id", ignore = true)
    BetSettlementEntity toEntity(BetSettlement betSettlement);
}
