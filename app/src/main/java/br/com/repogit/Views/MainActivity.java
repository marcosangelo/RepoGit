package br.com.repogit.Views;

import android.app.SearchManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import br.com.repogit.Adapters.RepoAdapter;
import br.com.repogit.Dao.RepositorioDao;
import br.com.repogit.Model.Item;
import br.com.repogit.Model.Repositorio;
import br.com.repogit.Model.RepositorioJSON;
import br.com.repogit.R;
import br.com.repogit.Servicos.GithubService;
import br.com.repogit.db.Database;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.repogit.Uteis.Constantes.API_REPOSITORIES;


public class MainActivity extends AppCompatActivity {


    private List<Repositorio> repoList = new ArrayList<>();
    private RepoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    Repositorio repository;
    LinearLayoutManager layoutManager;
    int pag = 1;
    boolean mLoading = false;
    int currentItem, totalItem, scrollOutItem;
    private static final String DATABASE_NAME = "OfflineDatabase";
    RepositorioDao repositoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database database = Room.databaseBuilder(getApplicationContext(),
                Database.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
        repositoryDao = database.getRepositoryDao();
        mRecyclerView = findViewById(R.id.rv_repo);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        repoList = repositoryDao.getAll();
        if (isNetworkAvailable()) {
            getData(pag);
        }


        mAdapter = new RepoAdapter(repoList, this, repositoryDao);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mLoading = true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem = layoutManager.getChildCount();
                totalItem = layoutManager.getItemCount();
                scrollOutItem = layoutManager.findFirstVisibleItemPosition();
                if (mLoading && (currentItem + scrollOutItem == totalItem)) {
                    pag++;
                    getData(pag);
                    mLoading = false;
                }
            }
        });
    }

    public void getData(int pag) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_REPOSITORIES)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GithubService mainService = retrofit.create(GithubService.class);
        Map<String, String> params = new HashMap<>();
        params.put("q", "language:Java");
        params.put("sort", "stars");
        params.put("page", String.valueOf(pag));
        Call<RepositorioJSON> requestStatus = mainService.listRepos(params);

        requestStatus.enqueue(new Callback<RepositorioJSON>() {
            @Override
            public void onResponse(Call<RepositorioJSON> call, Response<RepositorioJSON> response) {
                if (!response.isSuccessful()) {
                    Log.i("TAG", "Erro!!!!!!!!!: " + response.code());
                } else {
                    RepositorioJSON repo = response.body();

                    for (Item item : repo.getItems()) {

                        repository = new Repositorio(
                                item.getName(),
                                item.getDescription(),
                                item.getOwner().getLogin(),
                                item.getForksCount(),
                                item.getStargazersCount(),
                                item.getOwner().getAvatarUrl());

                        repoList.add(repository);
                        repositoryDao.insertRepository(repository);
                        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), repoList.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<RepositorioJSON> call, Throwable t) {
                Log.e("Erro:", " " + t.getMessage());
                Toast.makeText(MainActivity.this, "Falha na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.searchRepositories(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.searchRepositories(newText);
                return true;
            }
        });
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

