package br.com.repogit.Servicos;

import java.util.ArrayList;
import java.util.Map;

import br.com.repogit.Model.PullRequestJSON;
import br.com.repogit.Model.RepositorioJSON;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 *
 */

public interface GithubService {
    @GET("search/repositories")
    Call<RepositorioJSON> listRepos(@QueryMap Map<String, String> params);

    @GET("pulls")
    Call<ArrayList<PullRequestJSON>> listPullRequest();
}
