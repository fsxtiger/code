package cn.golingo.glep.education.provider.remote.impl;

import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionCreateModel;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionPageDTO;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionQueryModel;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionVO;
import cn.golingo.glep.complex.grpc.client.competition.model.String;
import cn.golingo.glep.education.provider.remote.RemoteComplexService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author shuoxuan.fang
 * @date 2025/04/24
 */
@Service
public class RemoteComplexGrpcServiceImpl implements RemoteComplexGrpcService {
    private CompetitionServiceGrpc.CompetitionServiceBlockingStub competitionServiceBlockingStub;

    @PostConstruct
    public void init() {
        competitionServiceBlockingStub = CompetitionServiceGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public CompetitionPageDTO list(CompetitionQueryModel competitionQueryModel) {
        return competitionServiceBlockingStub.list(CompetitionQueryModel competitionQueryModel);
    }

    @Override
    public CompetitionVO create(CompetitionCreateModel competitionCreateModel) {
        return competitionServiceBlockingStub.create(CompetitionCreateModel competitionCreateModel);
    }

    @Override
    public CompetitionVO getCompetitionNum(String string) {
        return competitionServiceBlockingStub.getCompetitionNum(String string);
    }

}