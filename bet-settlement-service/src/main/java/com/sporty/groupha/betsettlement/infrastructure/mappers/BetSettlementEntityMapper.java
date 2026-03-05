package com.sporty.groupha.betsettlement.infrastructure.mappers;

import com.sporty.groupha.betsettlement.domain.BetSettlement;
import com.sporty.groupha.betsettlement.infrastructure.entity.BetSettlementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BetSettlementEntityMapper {

    BetSettlementEntity toEntity(BetSettlement betSettlement);
}
