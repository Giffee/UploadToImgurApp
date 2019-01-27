package com.lubotin.serega.pictureapplication.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lubotin.serega.pictureapplication.api.Api;
import com.lubotin.serega.pictureapplication.api.RestClient;
import com.lubotin.serega.pictureapplication.R;
import com.lubotin.serega.pictureapplication.model.Image;
import com.lubotin.serega.pictureapplication.view.SquareImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Image> imagesList;
    private File chosenFile;
    private Button addAllLinksButton;
    private ListView listView;
    private DrawerLayout drawerLayout;

    public RecyclerViewAdapter(Context context, List<Image> imagesList,
                               Button addAllLinksButton, ListView listView, DrawerLayout drawerLayout) {
        this.context = context;
        this.imagesList = imagesList;
        this.addAllLinksButton = addAllLinksButton;
        this.listView = listView;
        this.drawerLayout = drawerLayout;
        setOnButtonClickListener();
    }

    //Adding onClickListener for "links" button
    private void setOnButtonClickListener() {
        addAllLinksButton.setOnClickListener(v -> {
            ArrayList<String> list = new ArrayList<>();
            //Get all links
            for (Image i : imagesList) {
                if (i.getData() != null) {
                    list.add( i.getData().getLink());
                }
            }
            //ArrayAdapter for ListView with links
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.drawer_list_view_item, list);

            listView.setAdapter(adapter);
            //Open drawer if "links" button was pressed
            drawerLayout.openDrawer(listView);
        });
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item,
                parent,
                false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        String uri = imagesList.get(position).getImageUri();
        Glide.with(context).load(uri).into(holder.imageView);

        setOnImageClickListener(holder, position);
    }

    //Adding onClickListener for images to upload to imgur
    private void setOnImageClickListener(ViewHolder holder, int position) {
        holder.imageView.setOnClickListener(v -> {
            if (imagesList.get(position).getData() != null) {
                Toast.makeText(context, "This image has already been loaded", Toast.LENGTH_SHORT).show();
            } else {
                getFilePath(position);
                //Make progressBar visible while image is uploading to imgur
                holder.progressBar.setVisibility(View.VISIBLE);
                //Get a Api and make Retrofit request
                getCall().enqueue(new Callback<Image>() {
                    @Override
                    public void onResponse(Call<Image> call, Response<Image> response) {
                        //Make progressBar invisible
                        holder.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            imagesList.get(position).setData(response.body().getData());
                        } else {
                            onFailure(call, new Throwable("Response error with \"" + response.message() + "\"."));
                        }
                    }

                    @Override
                    public void onFailure(Call<Image> call, Throwable t) {
                        //Make progressBar invisible
                        holder.progressBar.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("Response error")
                                .setMessage("Response failure with \"" + t.getMessage() + "\"")
                                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    //Get images paths
    private void getFilePath(int position) {
        String filePath = imagesList.get(position).getImageUri();
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        chosenFile = new File(filePath);
    }

    //Get Api
    private Call<Image> getCall() {
        Api api = RestClient.getImgurInstance();
        return api.postImage(
                MultipartBody.Part.createFormData(
                        "image",
                        chosenFile.getName(),
                        RequestBody.create(
                                MediaType.parse("image/*"),
                                chosenFile)));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView imageView;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
