/*
 * Copyright (C) 2016 Erik Jhordan Rey.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jhordan.euro_cleanarchitecture.view.presenter;

import android.support.annotation.NonNull;
import com.example.jhordan.euro_cleanarchitecture.domain.model.Team;
import com.example.jhordan.euro_cleanarchitecture.domain.usecase.DefaultSubscriber;
import com.example.jhordan.euro_cleanarchitecture.domain.usecase.GetEuroTeamByFlag;
import com.example.jhordan.euro_cleanarchitecture.view.viewmodel.TeamViewModel;
import com.example.jhordan.euro_cleanarchitecture.view.viewmodel.mapper.TeamViewModelToTeamMapper;
import javax.inject.Inject;

public class TeamDetailPresenter extends Presenter<TeamDetailPresenter.View> {

  private final GetEuroTeamByFlag getEuroTeamByFlag;
  private final TeamViewModelToTeamMapper mapper;
  private String teamFlag;

  @Inject public TeamDetailPresenter(@NonNull GetEuroTeamByFlag getEuroTeamByFlag,
      @NonNull TeamViewModelToTeamMapper mapper) {
    this.getEuroTeamByFlag = getEuroTeamByFlag;
    this.mapper = mapper;
  }

  @Override public void initialize() {
    super.initialize();
    getView().showLoading();
    getEuroTeamByFlag.searchTeamByFlag(teamFlag);
    getEuroTeamByFlag.execute(new TeamSubscriber());
  }

  private final class TeamSubscriber extends DefaultSubscriber<Team> {

    @Override public void onCompleted() {
      super.onCompleted();
      getView().hideLoading();
    }

    @Override public void onError(Throwable e) {
      super.onError(e);
      e.printStackTrace();
      getView().hideLoading();
    }

    @Override public void onNext(Team team) {
      super.onNext(team);
      TeamViewModel teamViewModel = mapper.reverseMap(team);
      getView().showTeam(teamViewModel);
    }
  }

  public void setTeamFlag(String teamFlag) {
    this.teamFlag = teamFlag;
  }

  public void destroy(){
    this.getEuroTeamByFlag.unsubscribe();
    setView(null);
  }

  public interface View extends Presenter.View {
    void showTeam(TeamViewModel teamViewModel);
  }
}
