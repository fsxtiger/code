package cn.golingo.glep.complex.service;

import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionCreateModel;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionDTO;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionPageDTO;
import cn.golingo.glep.complex.grpc.client.competition.model.CompetitionQueryModel;
import cn.golingo.glep.complex.grpc.client.competition.model.String;

/**
 * @author shuoxuan.fang
 * @date 2025/04/24
 */

 public interface CompetitionService {
     CompetitionPageDTO list(CompetitionQueryModel competitionQueryModel);

     CompetitionDTO create(CompetitionCreateModel competitionCreateModel);

     CompetitionDTO getCompetitionNum(String string);

 }
