package cn.golingo.glep.education.provider.service;

import cn.unipus.glsp.base.wrapper.PageResultWrapper;
import example.CompetitionCreateParam;
import example.CompetitionQueryParam;
import example.CompetitionVO;

/**
 * @author shuoxuan.fang
 * @date 2025/04/24
 */
public interface CompetitionService {

    PageResultWrapper<CompetitionVO> list(CompetitionQueryParam competitionQueryParam);

    CompetitionVO create(CompetitionCreateParam competitionCreateParam);

    CompetitionVO getCompetitionNum(String string);

}