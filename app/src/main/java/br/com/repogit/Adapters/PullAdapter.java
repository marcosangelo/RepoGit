package br.com.repogit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.repogit.Model.PullRequest;
import br.com.repogit.R;
import br.com.repogit.Uteis.CircleEffect;

public class PullAdapter extends RecyclerView.Adapter<PullAdapter.PullViewHolder> {

    private List<PullRequest> mListPull;
    private Context context;

    public PullAdapter(List<PullRequest> mListPull, Context context) {
        this.mListPull = mListPull;
        this.context = context;
    }

    @Override
    public PullAdapter.PullViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForListItem = R.layout.pull_request_cell;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediatly = false;

        View view = inflater.inflate(layoutForListItem, parent, attachImmediatly);
        PullAdapter.PullViewHolder viewHolder = new PullAdapter.PullViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PullAdapter.PullViewHolder holder, int position) {

        PullRequest pull = mListPull.get(position);
        holder.bind(pull, position, context);
    }

    @Override
    public int getItemCount() {
        return mListPull.size();
    }


    public class PullViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPullReq, tvDescPull, tvUser, tvCreatedAt;
        public ImageView ivAvatar;
        public PullViewHolder(View itemView) {
            super(itemView);
            tvPullReq = itemView.findViewById(R.id.tvPullReq);
            tvDescPull = itemView.findViewById(R.id.tvDescPull);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvUser = itemView.findViewById(R.id.tvUser);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }

        void bind(final PullRequest pull, int pos, final Context context){
            if(pull.getUrlImage() != " ") {
                ivAvatar.setImageBitmap(null);
            }
            Picasso.with(context)
                    .load(pull.getUrlImage())
                    .transform(new CircleEffect())
                    .into(ivAvatar);

            tvPullReq.setText(pull.getPullRequest());
            tvDescPull.setText(pull.getDescPullRequest());
            tvCreatedAt.setText(pull.getDtCreatedAt().toString());
            tvUser.setText(pull.getUser());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pull.getHtmlUrl()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
