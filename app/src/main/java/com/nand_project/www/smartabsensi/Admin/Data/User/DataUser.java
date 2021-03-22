package com.nand_project.www.smartabsensi.Admin.Data.User;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nand_project.www.smartabsensi.R;

import java.util.ArrayList;
import java.util.List;

public class DataUser extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private RecyclerViewDataUser adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Model> models;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private Dialog myDialog, myDialogAdd;
    private EditText tv_nama, tv_nbi;
    private Button btn_simpan;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_data_user, container, false);
        models = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerview_data_user);

        setHasOptionsMenu(true);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        if(adapter != null){
            models.clear();
            adapter.notifyDataSetChanged();
            initData();
        }else {
            initData();
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("reload"));

//        try {
//            // generate a 150x150 QR code
//            Bitmap bm = encodeAsBitmap(barcode_content, BarcodeFormat.QR_CODE, 150, 150);
//
//            if(bm != null) {
//                image_view.setImageBitmap(bm);
//            }
//        } catch (WriterException e) { //eek }

        return rootView;
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            onRefresh();
        }
    };


    private void initData() {
        mSwipeRefreshLayout.setRefreshing(true);
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /**
                 * Saat ada data baru, masukkan datanya ke ArrayList
                 */
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    /**
                     * Mapping data pada DataSnapshot ke dalam object Barang
                     * Dan juga menyimpan primary key pada object Barang
                     * untuk keperluan Edit dan Delete data
                     */
                    Model model = noteDataSnapshot.getValue(Model.class);
                    model.setKey(noteDataSnapshot.getKey());

                    /**
                     * Menambahkan object Barang yang sudah dimapping
                     * ke dalam ArrayList
                     */
                    models.add(model);
                }

                /**
                 * Inisialisasi adapter dan data barang dalam bentuk ArrayList
                 * dan mengeset Adapter ke dalam RecyclerView
                 */

                setRvAdapter(models);
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /**
                 * Kode ini akan dipanggil ketika ada error dan
                 * pengambilan data gagal dan memprint error nya
                 * ke LogCat
                 */
                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setRvAdapter(ArrayList<Model> models) {
        adapter = new RecyclerViewDataUser(models,getActivity());
        layoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void myTambahButton() {
        myDialogAdd = new Dialog(getActivity());
        myDialogAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialogAdd.setContentView(R.layout.popup_tambah_user);

        tv_nama = myDialogAdd.findViewById(R.id.nama);
        tv_nbi = myDialogAdd.findViewById(R.id.nbi_user);
        btn_simpan = myDialogAdd.findViewById(R.id.btnSimpan);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_nama.getText().toString().equals("")){
                    tv_nama.setError("Nama harus diisi");
                }else if (tv_nbi.getText().toString().equals("")){
                    tv_nbi.setError("NBI harus diisi");
                }else {
                    inputData(new Model(tv_nama.getText().toString(), tv_nbi.getText().toString()),myDialogAdd);
                }
            }
        });

        myDialogAdd.setCancelable(true);
        myDialogAdd.show();
        Window window = myDialogAdd.getWindow();
        window.setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
    }

    private void inputData(Model model, final Dialog myDialogAdd) {
        databaseReference.child("User").push().setValue(model).addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                myDialogAdd.dismiss();
                Toast.makeText(getActivity(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tambah_menu, menu);
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView  = new SearchView(getActivity());

        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nextText) {
                //Data akan berubah saat user menginputkan text/kata kunci pada SearchView
                nextText = nextText.toLowerCase();
                List<Model> dataFilter = new ArrayList<>();
                for(Model data : models){
                    String nama = data.getNama().toLowerCase();
                    String nbi = data.getNbi().toLowerCase();
                    if(nama.contains(nextText) || nbi.contains(nextText)){
                        dataFilter.add(data);
                    }
                }
                if(adapter != null){
                    adapter.setFilter(dataFilter);
                }else {
                    return false;
                }
                return true;

            }
        });
        searchItem.setActionView(searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_tambah:
                //Toast.makeText(getActivity(), "Comingssonn", Toast.LENGTH_SHORT).show();
                myTambahButton();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {

        // Pengecekan data
        if(adapter != null){
            models.clear();
            adapter.notifyDataSetChanged();
            initData();
        }else {
            initData();
        }

    }


}
