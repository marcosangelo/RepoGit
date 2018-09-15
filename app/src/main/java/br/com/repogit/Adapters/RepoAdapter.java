package br.com.repogit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.repogit.Dao.RepositorioDao;
import br.com.repogit.Model.Repositorio;
import br.com.repogit.R;
import br.com.repogit.Uteis.CircleEffect;
import br.com.repogit.Views.PullActivity;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoViewHolder> {

    private Context context;
    private List<Repositorio> hitorico;
    protected List<Repositorio> listaFiltro;
    RepositorioDao repositorioDao;
    Animation animation;
    private int maxPosition = -1;

    public RepoAdapter(List<Repositorio> mListRepo, Context context, RepositorioDao repositoryDao) {

        this.hitorico = mListRepo;
        this.listaFiltro = new ArrayList<>();
        this.listaFiltro.addAll(this.hitorico);
        this.context = context;
        this.repositorioDao = repositoryDao;
    }


    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForListItem = R.layout.repository_cell;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediatly = false;

        View view = inflater.inflate(layoutForListItem, parent, attachImmediatly);
        RepoViewHolder viewHolder = new RepoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepoViewHolder holder, int position) {

        Repositorio repo = hitorico.get(position);

        if (listaFiltro.size() > 0) {
            repo = listaFiltro.get(position);
        }

        holder.bind(repo, position, context);

    }


    @Override
    public int getItemCount() {

        if (listaFiltro.size() > 0) {
            return listaFiltro.size();
        }

        return hitorico.size();

    }

    public void searchRepositories(String filter) {
        listaFiltro.clear();
        if (filter.isEmpty()) {
            listaFiltro.addAll(hitorico);
        } else {
            for (Repositorio item : hitorico) {
                if (item.getNmRepo().toLowerCase().contains(filter.toLowerCase())) {
                    listaFiltro.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class RepoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvRepo, tvDescRepo, tvUser, tvNmUser, tvStar, tvFork;
        public ImageView ivAvatar, ivFav;

        public RepoViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivFav = itemView.findViewById(R.id.ivFav);

            tvRepo = itemView.findViewById(R.id.tvRepositotio);
            tvDescRepo = itemView.findViewById(R.id.tvDescRepo);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvNmUser = itemView.findViewById(R.id.tvCreatedAt);
            tvStar = itemView.findViewById(R.id.tvStar);
            tvFork = itemView.findViewById(R.id.tvFork);
        }


        void bind(final Repositorio repo, final int pos, final Context context) {
            if (!repo.getUrlImage().equals(" ")) {
                ivAvatar.setImageBitmap(null);
            }
            Picasso.with(context)
                    .load(repo.getUrlImage())
                    .transform(new CircleEffect())
                    .into(ivAvatar);
            tvRepo.setText(repo.getNmRepo());
            tvDescRepo.setText(repo.getDescRepo());
            tvUser.setText(repo.getUser());

            tvStar.setText(String.valueOf(repo.getStars()));
            tvFork.setText(String.valueOf(repo.getForks()));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, PullActivity.class);
                    i.putExtra("user", repo.getUser());
                    i.putExtra("repo", repo.getNmRepo());
                    context.startActivity(i);
                }
            });

            verifyFavorite(repo);

            ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (repo.isFavorited()) {
                        repo.setFavorited(false);
                    } else {
                        repo.setFavorited(true);
                    }
                    verifyFavorite(repo);
                    repositorioDao.updateRepository(repo);
                }
            });

            if (pos > maxPosition) {
                animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
                itemView.startAnimation(animation);
                maxPosition = pos;
            }
        }

        private void verifyFavorite(Repositorio repository) {
            if (repository.isFavorited()) {
                ivFav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_24dp));
                ivFav.setBackgroundColor(context.getResources().getColor(R.color.red));
            } else {
                ivFav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_24dp));
                ivFav.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
        }


    }
}
