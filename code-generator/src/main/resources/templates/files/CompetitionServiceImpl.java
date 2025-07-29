package cn.golingo.glep.education.provider.service.impl;

import cn.golingo.glep.education.provider.remote.RemoteComplexService;
import cn.golingo.glep.education.provider.remote.RemoteComplexService;
import cn.golingo.glep.education.provider.service.CompetitionService;
import cn.unipus.glsp.base.wrapper.PageResultWrapper;
import example.CompetitionCreateParam;
import example.CompetitionQueryParam;
import example.CompetitionVO;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author shuoxuan.fang
 * @date 2025/04/24
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {
    @Resource
    private RemoteComplexService remoteComplexService;

    @Override
    public PageResultWrapper<CompetitionVO> list(CompetitionQueryParam competitionQueryParam) {

    }
    @Override
    public CompetitionVO create(CompetitionCreateParam competitionCreateParam) {

    }
    @Override
    public CompetitionVO getCompetitionNum(String string) {

    }
}