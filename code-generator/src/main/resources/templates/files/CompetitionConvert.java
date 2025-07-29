package cn.golingo.glep.education.provider.convert;

import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionCreateModel;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionQueryModel;
import cn.golingo.glep.complex.grpc.client.competition.model.String;
import cn.unipus.glsp.base.wrapper.PageResultWrapper;
import example.CompetitionCreateParam;
import example.CompetitionQueryParam;
import example.CompetitionVO;

/**
 * @author shuoxuan.fang
 * @date 2025/04/24
 */

@Mapper(
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
public interface CompetitionConvert {
    CompetitionDTO dtoToVO(CompetitionVO competitionVO);

    CompetitionQueryModel paramToModel(CompetitionQueryParam competitionQueryParam);

    CompetitionDTO dtoToVO(CompetitionVO competitionVO);

    CompetitionCreateModel paramToModel(CompetitionCreateParam competitionCreateParam);

    CompetitionDTO dtoToVO(CompetitionVO competitionVO);
}