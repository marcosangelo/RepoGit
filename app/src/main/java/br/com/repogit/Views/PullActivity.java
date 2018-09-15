package br.com.repogit.Views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import br.com.repogit.Adapters.PullAdapter;
import br.com.repogit.Model.PullRequest;
import br.com.repogit.Model.PullRequestJSON;
import br.com.repogit.R;
import br.com.repogit.Servicos.GithubService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.repogit.Uteis.Constantes.API_PULL_REQUEST;

public class PullActivity extends AppCompatActivity {
    private static String TAG = "PullRequestActivity";
    private String baseUrl;
    RecyclerView rvPull;
    ProgressBar pbLoadingBar;
    int filtro;
    static int PULL_REQUEST_ABERTO = 0;
    static int PULL_REQUEST_FECHADO = 1;
    private List<PullRequest> openedpullList = new ArrayList<>();
    private List<PullRequest> closedpullList = new ArrayList<>();
    private PullAdapter mAdapter;
    PullRequest pullRequest;
    TextView tvFiltroAberto, tvFiltroFechado;
    ProgressDialog dialog;
    private TextView tvEmptyPullRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_request);
        pbLoadingBar = findViewById(R.id.pbLoadingBar);
        tvFiltroAberto = findViewById(R.id.tvFiltroAberto);
        tvFiltroFechado = findViewById(R.id.tvFiltroFechado);
        tvEmptyPullRequest = findViewById(R.id.tvEmptyPullRequest);
        rvPull = findViewById(R.id.rv_pull);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);

        filtro = PULL_REQUEST_ABERTO;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvPull.setLayoutManager(layoutManager);
        rvPull.setHasFixedSize(true);
        rvPull.setItemAnimator(new DefaultItemAnimator());
        rvPull.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        baseUrl = API_PULL_REQUEST + bundle.get("user").toString().toLowerCase()+ "/" + bundle.get("repo").toString().toLowerCase()+ "/";


        tvFiltroAberto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFiltroFechado.setBackgroundColor(Color.TRANSPARENT);
                tvFiltroFechado.setBackground(getResources().getDrawable(R.drawable.selection_box));
                tvFiltroFechado.setBackgroundColor(Color.TRANSPARENT);
                tvFiltroFechado.setTextColor(Color.BLACK);
                tvFiltroAberto.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvFiltroAberto.setBackground(getResources().getDrawable(R.drawable.selected_box));
                tvFiltroAberto.setTextColor(Color.WHITE);
                setPullRequestList(PULL_REQUEST_ABERTO);
            }
        });

        tvFiltroFechado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFiltroAberto.setBackgroundColor(Color.TRANSPARENT);
                tvFiltroAberto.setBackground(getResources().getDrawable(R.drawable.selection_box));
                tvFiltroAberto.setBackgroundColor(Color.TRANSPARENT);
                tvFiltroAberto.setTextColor(Color.BLACK);
                tvFiltroFechado.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvFiltroFechado.setBackground(getResources().getDrawable(R.drawable.selected_box));
                tvFiltroFechado.setTextColor(Color.WHITE);
                setPullRequestList(PULL_REQUEST_FECHADO);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        GithubService mainService = retrofit.create(GithubService.class);

        Call<ArrayList<PullRequestJSON>> call = mainService.listPullRequest();

        call.enqueue(new Callback<ArrayList<PullRequestJSON>>() {
            @Override
            public void onResponse(Call<ArrayList<PullRequestJSON>> call, Response<ArrayList<PullRequestJSON>> response) {
                if (!response.isSuccessful()) {
                    finish();
                } else {

                    if(String.valueOf(response.body()).equals("[]") ) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(PullActivity.this);
                        alert.setTitle(R.string.ops_title_message);
                        alert.setMessage(R.string.no_pull_request_message);
                        alert.setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        alert.show();

                    }else{
                        ArrayList<PullRequestJSON> pullReq = response.body();

                        for (PullRequestJSON pr: pullReq) {

                            pullRequest = new PullRequest(
                                    pr.getTitle(),
                                    pr.getBody(),
                                    pr.getHtmlUrl(),
                                    pr.getUser().getLogin(),
                                    pr.getUser().getAvatarUrl(),
                                    pr.getCreatedAt(),
                                    pr.getState()
                            );

                            if(pullRequest.isOpen() ) {
                                openedpullList.add(pullRequest);
                            }else{
                                closedpullList.add(pullRequest);
                            }
                            setPullRequestList(PULL_REQUEST_ABERTO);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PullRequestJSON>> call, Throwable t) {
                Log.e( "Erro:", " " + t.getMessage());
                Toast.makeText(PullActivity.this, R.string.connection_fail_message, Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }

    private void setPullRequestList(int filtro){
        tvEmptyPullRequest.setVisibility(View.INVISIBLE);
        if(filtro == PULL_REQUEST_ABERTO) {
            mAdapter = new PullAdapter(openedpullList, this);

            if(openedpullList.isEmpty()){
                tvEmptyPullRequest.setText(R.string.no_opened_pull_request_message);
                tvEmptyPullRequest.setVisibility(View.VISIBLE);
            }
        }

        if(filtro == PULL_REQUEST_FECHADO) {
            mAdapter = new PullAdapter(closedpullList, this);
            if(closedpullList.isEmpty()){
                tvEmptyPullRequest.setText(R.string.no_closed_pull_request_message);
                tvEmptyPullRequest.setVisibility(View.VISIBLE);
            }
        }

        mAdapter.notifyDataSetChanged();
        rvPull.setAdapter(mAdapter);

        if(pbLoadingBar.getVisibility() != View.GONE){
            pbLoadingBar.setVisibility(View.GONE);
            rvPull.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, MainActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }
}
