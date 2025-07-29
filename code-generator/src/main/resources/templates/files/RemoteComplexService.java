package cn.golingo.glep.education.provider.remote;

import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionCreateModel;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionPageDTO;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionQueryModel;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionVO;
import cn.golingo.glep.complex.grpc.client.competition.model.String;

/**
 * @author shuoxuan.fang
 * @date 2025/04/24
 */
 public interface RemoteComplexGrpcService {
     CompetitionPageDTO list(CompetitionQueryModel competitionQueryModel);

     CompetitionVO create(CompetitionCreateModel competitionCreateModel);

     CompetitionVO getCompetitionNum(String string);

 }