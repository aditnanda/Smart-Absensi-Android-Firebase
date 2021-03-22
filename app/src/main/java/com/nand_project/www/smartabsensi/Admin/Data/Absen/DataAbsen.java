package com.nand_project.www.smartabsensi.Admin.Data.Absen;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.nand_project.www.smartabsensi.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.text.TextUtils.split;

public class DataAbsen extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private RecyclerViewDataAdmin adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Model> models;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String[] date = new String[1];
    private List<Model> dataFilter;
    private String status ="all";
    private String nama = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_data_absen, container, false);
        models = new ArrayList<>();
        setHasOptionsMenu(true);
        recyclerView = rootView.findViewById(R.id.recyclerview_data_admin);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        initData();

        return rootView;
    }

    private void initData() {
        mSwipeRefreshLayout.setRefreshing(true);

        status = "all";
        nama ="";
        databaseReference.child("Absen_NAND").addValueEventListener(new ValueEventListener() {
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

                Log.d("data", models.toString());

                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /**
                 * Kode ini akan dipanggil ketika ada error dan
                 * pengambilan data gagal dan memprint error nya
                 * ke LogCat
                 */
                System.out.println(databaseError.getDetails() + " " + databaseError.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setRvAdapter(ArrayList<Model> models) {
        adapter = new RecyclerViewDataAdmin(models, getActivity());
        layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.filter_menu, menu);
        inflater.inflate(R.menu.save_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getActivity());

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
                Log.d("next", nextText.toString());
                dataFilter= new ArrayList<>();
                for (Model data : models) {
                    String nama = data.getNama().toLowerCase();
                    String nbi = data.getNbi().toLowerCase();
                    String masuk = data.getMasuk().toLowerCase();
//                    if (nama.contains(nextText) || masuk.contains(nextText) || nbi.contains(nextText)) {
//                        dataFilter.add(data);
//                    }

                    if (nama.contains(nextText)) {
                        dataFilter.add(data);

                    }

                }
                if (adapter != null) {
                    adapter.setFilter(dataFilter);
                    status ="nama";
                    nama = nextText;
                } else {
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


            case R.id.action_filter:
                //myDialogFilter();


                int mYear, mMonth, mDay, mHour, mMinute;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                if (dayOfMonth <= 9) {
                                    date[0] = "0"+dayOfMonth + "-0"
                                            + (monthOfYear + 1) + "-" + year;
                                } else {
                                    date[0] = dayOfMonth + "-"
                                            + (monthOfYear + 1) + "-" + year;
                                }

                                String nextText = date[0].toLowerCase();
                                dataFilter = new ArrayList<>();
                                for (Model data : models) {
                                    String masuk = data.getMasuk().toLowerCase();
                                    if (masuk.contains(nextText)) {
                                        dataFilter.add(data);
                                    }
                                }
                                if (adapter != null) {
                                    adapter.setFilter(dataFilter);
                                }

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
                status = "tanggal";
                nama ="";


                return true;
            case R.id.action_save:
                createPDF(nama);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRefresh() {

        // Pengecekan data
        if (adapter != null) {
            models.clear();
            adapter.notifyDataSetChanged();
            initData();
        } else {
            initData();
        }

    }

    public void createPDF(String nama) {
//reference to EditText
//create document object
        Document doc = new Document();
//output file path
        String outpath = "";
        if (status.equals("all")){
            outpath = Environment.getExternalStorageDirectory() + "/laporan_semua_absensi.pdf";
        }else if (status.equals("tanggal")){
            outpath = Environment.getExternalStorageDirectory() + "/laporan_absensi_" + date[0] + ".pdf";
        }else if (status.equals("nama")){
            outpath = Environment.getExternalStorageDirectory() + "/laporan_absensi_"+nama+".pdf";
        }

        try {
//create pdf writer instance
            PdfWriter.getInstance(doc, new FileOutputStream(outpath));
//open the document for writing
            doc.open();
//add paragraph to the document
            doc.add(new Paragraph("================================"));
            doc.add(new Paragraph("SMART ABSENSI"));
            doc.add(new Paragraph("================================\n\n"));


            for (int i = 0; i < models.size(); i++) {
                String[] waktumasuk = split(models.get(i).getMasuk(), " ");
                String waktuFilter = waktumasuk[0].toString();

                //Log.d("ddd", waktumasuk[0] + " " + date[0] + ", " + waktuFilter.trim().equals(dataWaktu.trim()));
                if (status.equals("all")){
                    doc.add(new Paragraph("==================="));
                    doc.add(new Paragraph("Nama : " + models.get(i).getNama()));
                    doc.add(new Paragraph("NBI : " + models.get(i).getNbi()));
                    doc.add(new Paragraph("Waktu Absen : " + models.get(i).getMasuk()));
                    doc.add(new Paragraph("Waktu Keluar : " + models.get(i).getKeluar()));
                    doc.add(new Paragraph("==================="));
                }else if (status.equals("tanggal")){
                    String dataWaktu = date[0].toString();
                    if (waktuFilter.trim().contains(dataWaktu.trim())) {
                        doc.add(new Paragraph("==================="));
                        doc.add(new Paragraph("Nama : " + models.get(i).getNama()));
                        doc.add(new Paragraph("NBI : " + models.get(i).getNbi()));
                        doc.add(new Paragraph("Waktu Absen : " + models.get(i).getMasuk()));
                        doc.add(new Paragraph("Waktu Keluar : " + models.get(i).getKeluar()));
                        doc.add(new Paragraph("==================="));
                    }
                }
            }

            if (status.equals("nama")){
                for (int i =0 ; i<dataFilter.size();i++){
                    doc.add(new Paragraph("==================="));
                    doc.add(new Paragraph("Nama : " + dataFilter.get(i).getNama()));
                    doc.add(new Paragraph("NBI : " + dataFilter.get(i).getNbi()));
                    doc.add(new Paragraph("Waktu Absen : " + dataFilter.get(i).getMasuk()));
                    doc.add(new Paragraph("Waktu Keluar : " + dataFilter.get(i).getKeluar()));
                    doc.add(new Paragraph("==================="));
                }
            }

            doc.add(new Paragraph("\n\n================================\n"));
            doc.add(new Paragraph(date[0] + " dibuat oleh http://www.nand-project.com"));
//close the document
            doc.close();
            Toast.makeText(getActivity(), "Laporan berhasil dibuat", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
